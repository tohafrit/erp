package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.*
import ru.korundm.entity.*
import ru.korundm.enumeration.AppSettingsAttr.PRODUCT_PRICE_UNLOCK_TIME
import ru.korundm.enumeration.ProductDeciphermentAttr.*
import ru.korundm.enumeration.ProductDeciphermentTypeEnum
import ru.korundm.enumeration.ProductDeciphermentTypeEnum.*
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.*
import ru.korundm.helper.FileStorageType.ProductDeciphermentFile
import ru.korundm.helper.TabrOut.Companion.instance
import ru.korundm.helper.manager.decipherment.DeciphermentManager
import ru.korundm.report.excel.util.ExcelUtil
import ru.korundm.util.FileStorageUtil.extractSingular
import ru.korundm.util.FileStorageUtil.validateFile
import ru.korundm.util.KtCommonUtil.attachDocumentXLSX
import ru.korundm.util.KtCommonUtil.getUser
import ru.korundm.util.KtCommonUtil.readDynamic
import ru.korundm.util.KtCommonUtil.userFullName
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@ActionController([RequestPath.Action.Prod.PRODUCT + "/detail/decipherment"])
class ProductDetailDeciphermentActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val productDeciphermentService: ProductDeciphermentService,
    private val productDeciphermentAttrValService: ProductDeciphermentAttrValService,
    private val productDeciphermentPeriodService: ProductDeciphermentPeriodService,
    private val productPricePeriodService: ProductPricePeriodService,
    private val productService: ProductService,
    private val fileStorageService: FileStorageService,
    private val labourIntensityOperationService: ProductLabourIntensityOperationService,
    private val productWorkCostService: ProductWorkCostService,
    private val productWorkCostJustificationService: ProductWorkCostJustificationService,
    private val labourIntensityService: ProductLabourIntensityService,
    private val productSpecReviewJustificationService: ProductSpecReviewJustificationService,
    private val productSpecResearchJustificationService: ProductSpecResearchJustificationService,
    private val productSpecReviewPriceService: ProductSpecReviewPriceService,
    private val productSpecResearchPriceService: ProductSpecResearchPriceService,
    private val labourIntensityEntryService: ProductLabourIntensityEntryService,
    private val basicEconomicIndicatorService: BasicEconomicIndicatorService,
    private val okpdCodeService: OkpdCodeService,
    private val companyService: CompanyService,
    private val manager: DeciphermentManager,
    private val productPriceAnalysisValService: ProductPriceAnalysisValService,
    private val lotService: LotService,
    private val appSettingsService: AppSettingsService
) {

    // Список расшифровок
    @GetMapping("/load")
    fun load(periodId: Long?): List<*> {
        data class Item(
            var id: Long? = null,
            var type: ProductDeciphermentTypeEnum? = null,
            var name: String? = null,
            var readiness: Boolean = false,
            var approved: Boolean = false,
            var createDate: LocalDate? = null,
            var createdBy: String? = null,
            var fileHash: String? = null,
            var comment: String? = null,
            var canEdit: Boolean = false,
            var canApprove: Boolean = false,
            var canUnApprove: Boolean = false
        )
        val data = productDeciphermentService.getAllByPeriodId(periodId)
        val fileList = fileStorageService.readAny(data, ProductDeciphermentFile)
        return productDeciphermentService.getAllByPeriodId(periodId).map{ Item(
            it.id,
            it.type?.enum,
            it.type?.name,
            it.ready,
            it.approved,
            it.createDate,
            it.createdBy?.userOfficialName,
            fileList.extractSingular(it, ProductDeciphermentFile)?.urlHash,
            it.comment,
            it.approved.not() || it.type?.enum == FORM_1,
            it.ready,
            it.approved
        ) }
    }

    // Удаление расшифровки
    @DeleteMapping("/delete/{id}")
    fun delete(@PathVariable id: Long) = baseService.exec {
        productDeciphermentAttrValService.deleteAllByDeciphermentId(id)
        productDeciphermentService.deleteById(id)
    }

    // Выбор расшифровки из списка и привязки ее к периоду
    @PostMapping("/add/select")
    fun addSelect(
        session: HttpSession,
        periodId: Long,
        @RequestParam typeIdList: List<Long>
    ) = baseService.exec {
        val period = productDeciphermentPeriodService.read(periodId) ?: throw AlertUIException("Период не найден")
        if (productDeciphermentService.existsByPeriodIdAndTypeIdIn(periodId, typeIdList)) throw AlertUIException("Некоторые из форм уже добавлены в период")
        val user = session.getUser()
        val date = LocalDate.now()
        typeIdList.forEach {
            val decipherment = ProductDecipherment()
            decipherment.period = period
            decipherment.type = ProductDeciphermentType(it)
            decipherment.createDate = date
            decipherment.createdBy = user
            productDeciphermentService.save(decipherment)
        }
    }

    // Загрузка списка периодов изделия для расчета цены
    @GetMapping("/period/load")
    fun periodLoad(request: HttpServletRequest, productId: Long): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var planName: String = "",
            var planStartDate: LocalDate? = null,
            var planComment: String? = null,
            var reportName: String? = null,
            var reportStartDate: LocalDate? = null,
            var reportComment: String? = null,
            var rowCount: Long = 0
        ) : RowCountable { override fun rowCount() = rowCount }
        val input = TabrIn(request)
        return instance(input, productDeciphermentPeriodService.findProductPeriodTableData(input, productId, Item::class))
    }

    // Сохранение периода расшифровки
    @PostMapping("/period/edit/save")
    fun periodEditSave(
        session: HttpSession,
        @RequestPart form: DynamicObject
    ): ValidatorResponse {
        val user = session.getUser()
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)

        val productId = form.long(ObjAttr.PRODUCT_ID)
        val formId = form.long(ObjAttr.ID)
        val planPeriodId = form.long(ObjAttr.PLAN_PERIOD_ID)
        if (formId == null && planPeriodId == null) errors.putError(ObjAttr.PLAN_PERIOD_ID, ValidatorMsg.REQUIRED)
        val reportPeriodId = form.long(ObjAttr.REPORT_PERIOD_ID)

        if (response.isValid) baseService.exec {
            if (formId == null && productDeciphermentPeriodService.existsByProductIdAndPricePeriodId(productId, planPeriodId)) throw AlertUIException("Плановый период уже привязан к изделию")
            val product = productId?.let { productService.read(it) } ?: throw AlertUIException("Изделие не найдено")
            val planPeriod = planPeriodId?.let { productPricePeriodService.read(it) } ?: throw AlertUIException("Плановый период не найден")
            val reportPeriod = reportPeriodId?.let { productDeciphermentPeriodService.read(it) ?: throw AlertUIException("Отчетный период не найден") }
            if (reportPeriod?.endDate != null) throw AlertUIException("Отчетный период уже связан с другим плановым периодом")
            val period = formId?.let { productDeciphermentPeriodService.read(formId) ?: throw AlertUIException("Период изделия не найден") } ?: ProductDeciphermentPeriod()
            if (formId == null) {
                period.product = product
                period.pricePeriod = planPeriod
            }
            if (reportPeriod == null) {
                if (period.prevPeriod != null) {
                    period.prevPeriod?.endDate = null
                    productDeciphermentPeriodService.save(period.prevPeriod)
                }
                period.prevPeriod = null
            } else {
                if (reportPeriod.product != period.product) throw AlertUIException("Изделие отченого периода не должно отличаться от изделия редактируемого периода")
                if (reportPeriod.pricePeriod?.startDate?.isAfter(period.pricePeriod?.startDate) == true) throw AlertUIException("Дата начала отчетного периода не может быть позже даты планируемого")
                period.prevPeriod = reportPeriod
                reportPeriod.endDate = period.pricePeriod?.startDate
                productDeciphermentPeriodService.save(reportPeriod)
            }
            productDeciphermentPeriodService.save(period)

            if (formId == null) {
                val now = LocalDate.now()
                listOf(FORM_1, FORM_2, FORM_3, FORM_4, FORM_5, FORM_6_1, FORM_6_2, FORM_6_3, FORM_7, FORM_7_1, FORM_8, FORM_9, FORM_14, FORM_14_1, FORM_16, FORM_17, FORM_18, FORM_19, FORM_20, EXPLANATION_NOTE).forEach {
                    val decipherment = ProductDecipherment()
                    decipherment.period = period
                    decipherment.type = ProductDeciphermentType(it.id)
                    decipherment.createDate = now
                    decipherment.createdBy = user
                    productDeciphermentService.save(decipherment)
                }
                response.putAttribute(ObjAttr.ID, period.id)
            }
        }
        return response
    }

    // Удаление периода расчета цены изделия
    @DeleteMapping("/period/delete/{id}")
    fun periodDelete(@PathVariable id: Long) = productDeciphermentPeriodService.deleteById(id)

    // Загрузка списка периодов расчета цен, доступных для добавления к изделию
    @GetMapping("/period/edit/plan-period/load")
    fun periodEditPlanPeriodLoad(
        request: HttpServletRequest,
        filterData: String,
        productId: Long
    ): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var name: String = "",
            var startDate: LocalDate? = null,
            var comment: String? = null
        )
        val input = TabrIn(request)
        return instance(input, productPricePeriodService.findProductPeriodTableData(input, jsonMapper.readDynamic(filterData), productId)) { Item(
            it.id,
            it.name,
            it.startDate,
            it.comment
        ) }
    }

    // Загрузка списка отчетных периодов изделия
    @GetMapping("/period/edit/report-period/load")
    fun periodEditReportPeriodLoad(
        request: HttpServletRequest,
        productId: Long,
        planPeriodId: Long,
        excludePeriodId: Long?
    ): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var name: String = "",
            var startDate: LocalDate? = null,
            var comment: String? = null,
            var rowCount: Long = 0
        ) : RowCountable { override fun rowCount() = rowCount }
        val input = TabrIn(request)
        return instance(input, productDeciphermentPeriodService.findReportPeriodTableData(input, productId, planPeriodId, excludePeriodId, Item::class))
    }

    // Выгрузка расшифровки в excel
    @GetMapping("/download-form")
    fun downloadForm(response: HttpServletResponse, id: Long) {
        val attr = appSettingsService.findFirstByAttr(PRODUCT_PRICE_UNLOCK_TIME)
        if (attr?.dateVal == null || DAYS.between(attr.dateVal, LocalDate.now()) > 30) return
        val decipherment = productDeciphermentService.read(id)
        decipherment?.type?.let { type ->
            type.enum.excelCl?.let { cl ->
                response.attachDocumentXLSX(cl.getDeclaredConstructor().newInstance().generate(id), type.name)
            }
        }
    }

    // Выгрузка утвержденных расшифровок в excel
    @GetMapping("/download-approved-forms")
    fun downloadApprovedForms(response: HttpServletResponse, id: Long) {
        val wbList = productDeciphermentService.getAllByPeriodId(id).map { it.type?.enum?.excelCl?.getDeclaredConstructor()?.newInstance()?.generate(it.id ?: 0) as XSSFWorkbook }
        val wb = XSSFWorkbook()
        ExcelUtil.mergeExcelFiles(wb, wbList)
        response.attachDocumentXLSX(wb, "Расшифровки")
    }

    // Загрузка данных для окна обоснований стоимости
    @GetMapping("/edit-form9/cost-justification/load")
    fun editForm9CostJustificationLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var name: String = "",
            var approvalDate: LocalDate = LocalDate.MIN,
            var createDate: LocalDate = LocalDate.MIN, // дата создания
            var comment: String? = "",
            var rowCount: Long = 0,
        ) : RowCountable { override fun rowCount() = rowCount }
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, productWorkCostJustificationService.findTableData(input, form, Item::class))
    }

    // Загрузка данных для окна расчета трудоемкости
    @GetMapping("/edit-form9/labour-intensity/load")
    fun editForm9LabourIntensityLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var name: String = "",
            var comment: String? = null,
            var createDate: LocalDate = LocalDate.MIN,
            var lastName: String? = null,
            var firstName: String? = null,
            var middleName: String? = null,
            var approvedCount: Int = 0,
            var totalCount: Int = 0,
            var rowCount: Long = 0,
            var createdBy: String? = null,
            var approved: String? = null
        ) : RowCountable { override fun rowCount() = rowCount }
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, labourIntensityService.findTableData(input, form, Item::class)) {
            it.createdBy = userFullName(it.lastName, it.firstName, it.middleName)
            it.approved = "${it.approvedCount}/${it.totalCount}"
            it
        }
    }

    // Загрузка данных обоснований СП для формы 18
    @GetMapping("/edit-form18/review-justification/load")
    fun editForm18ReviewJustificationLoad(
        request: HttpServletRequest,
        deciphermentId: Long,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var name: String = "", // наименование
            var price: Double = .0, // цена для группы
            var approvalDate: LocalDate = LocalDate.MIN, // дата утверждения
            var createDate: LocalDate = LocalDate.MIN, // дата создания
            var comment: String? = "", // комментарий
            var rowCount: Long = 0, // номер строки для пангинации
        ) : RowCountable { override fun rowCount() = rowCount }
        val decipherment: ProductDecipherment? = productDeciphermentService.read(deciphermentId)
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, productSpecReviewJustificationService.findDeciphermentTableData(input, form, decipherment?.period?.product?.classificationGroup?.id, Item::class))
    }

    // Загрузка данных обоснований СИ для формы 18
    @GetMapping("/edit-form18/research-justification/load")
    fun editForm18ResearchJustificationLoad(
        request: HttpServletRequest,
        deciphermentId: Long,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var name: String = "", // наименование
            var price: Double = .0, // цена для группы
            var approvalDate: LocalDate = LocalDate.MIN, // дата утверждения
            var createDate: LocalDate = LocalDate.MIN, // дата создания
            var comment: String? = "", // комментарий
            var rowCount: Long = 0, // номер строки для пангинации
        ) : RowCountable { override fun rowCount() = rowCount }
        val decipherment: ProductDecipherment? = productDeciphermentService.read(deciphermentId)
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, productSpecResearchJustificationService.findDeciphermentTableData(input, form, decipherment?.period?.product?.classificationGroup?.id, Item::class))
    }

    // Расчет стоимости работ для формы 9
    @GetMapping("/edit-form9/work-cost")
    fun editForm9WorkCost(
        productId: Long,
        justificationId: Long,
        labourIntensityId: Long
    ): List<*> {
        data class Item(
            var id: Long? = null,
            var name: String = "",
            var separateDelivery: Boolean = false,
            var paymentRate: Double = .0,
            var labourIntensity: Double = .0,
            var cost: Double = .0,
            var totalCost: Double = .0,
            var totalCostWoPack: Double = .0,
            var totalLabourIntensity: Double = .0,
            var totalLabourIntensityWoPack: Double = .0
        )
        val workCostList = productWorkCostService.getAllByJustificationId(justificationId)
        val labourIntensityList = labourIntensityOperationService.getAllByLabourIntensityIdAndProductId(labourIntensityId, productId)
        val resultList = labourIntensityList.map { li ->
            val item = Item()
            item.id = li.id
            item.name = li.operation?.name ?: ""
            item.separateDelivery = li.operation?.separateDelivery ?: false
            item.paymentRate = workCostList.find { li.operation?.id == it.workType?.id }?.cost ?: .0
            item.labourIntensity = li.value
            item.cost = (item.paymentRate.toBigDecimal() * item.labourIntensity.toBigDecimal()).setScale(2, HALF_UP).toDouble()
            item
        }
        val resultWoPackList = resultList.filterNot { it.separateDelivery }
        resultList.firstOrNull()?.let { rs ->
            rs.totalCost = resultList.sumOf { it.cost.toBigDecimal() }.toDouble()
            rs.totalCostWoPack = resultWoPackList.sumOf { it.cost.toBigDecimal() }.toDouble()
            rs.totalLabourIntensity = resultList.sumOf { it.labourIntensity.toBigDecimal() }.toDouble()
            rs.totalLabourIntensityWoPack = resultWoPackList.sumOf { it.labourIntensity.toBigDecimal() }.toDouble()
        }
        return resultList
    }

    @GetMapping("/edit-form2/eco-indicator/load")
    fun editForm2EcoIndicatorLoad(): List<*> {
        data class Item (
            var id: Long?,
            var name: String,
            var approveDate: LocalDate
        )
        return basicEconomicIndicatorService.all.map { Item(
            it.id,
            it.name,
            it.approvalDate
        ) }
    }

    // Загрузка списка заказчиков
    @GetMapping("/edit-form1/customer/load")
    fun listEditForm1CustomerLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val name: String,
            val location: String?
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, companyService.findDeciphermentForm1CustomerTableData(input, form)) { Item(
            it.id,
            it.name,
            it.location
        ) }
    }

    // Загрузка полей для анализа
    @GetMapping("/analysis/load")
    fun analysisLoad(
        request: HttpServletRequest,
        productId: Long?,
        periodId: Long,
        additionalSalary: BigDecimal,
        socialSecurityContribution: BigDecimal,
        generalProductionCost: BigDecimal,
        generalOperationCost: BigDecimal,
        deflatorCoefficient: BigDecimal
    ): List<*> {
        data class Item(
            var name: String = "",
            var becameStandart: BigDecimal? = null,
            var becamePrice: BigDecimal? = null,
            var wasStandart: BigDecimal? = null,
            var wasPrice: BigDecimal? = null,
            var absoluteWPack: BigDecimal? = null,
            var relativeWPack: BigDecimal? = null,
            var secondProfit: BigDecimal? = null,
            var secondProfitPrev: BigDecimal? = null
        )
        val period = productDeciphermentPeriodService.read(periodId) ?: throw AlertUIException("Период не найден")

        //Формы планового периода
        val form4 = productDeciphermentService.getFirstByPeriodIdAndType(periodId, FORM_4)
        val form6 = productDeciphermentService.getFirstByPeriodIdAndType(periodId, FORM_6_1)
        val form6_2 = productDeciphermentService.getFirstByPeriodIdAndType(periodId, FORM_6_2)
        val form6_3 = productDeciphermentService.getFirstByPeriodIdAndType(periodId, FORM_6_3)
        val form9 = productDeciphermentService.getFirstByPeriodIdAndType(periodId, FORM_9)
        val form18 = productDeciphermentService.getFirstByPeriodIdAndType(periodId, FORM_18)
        val form20 = productDeciphermentService.getFirstByPeriodIdAndType(periodId, FORM_20)
        val profitFirst: BigDecimal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(form20, PROFITABILITY_FIRST)?.decimalVal ?: BigDecimal.ZERO
        val profitSecond: BigDecimal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(form20, PROFITABILITY_SECOND)?.decimalVal ?: BigDecimal.ZERO

        //Формы отчетного периода
        val form2Prev = productDeciphermentService.getFirstByPeriodIdAndType(period.prevPeriod?.id, FORM_2)
        val form4Prev = productDeciphermentService.getFirstByPeriodIdAndType(period.prevPeriod?.id, FORM_4)
        val form6Prev = productDeciphermentService.getFirstByPeriodIdAndType(period.prevPeriod?.id, FORM_6_1)
        val form6_2Prev = productDeciphermentService.getFirstByPeriodIdAndType(period.prevPeriod?.id, FORM_6_2)
        val form6_3Prev = productDeciphermentService.getFirstByPeriodIdAndType(period.prevPeriod?.id, FORM_6_3)
        val form9Prev = productDeciphermentService.getFirstByPeriodIdAndType(period.prevPeriod?.id, FORM_9)
        val form18Prev = productDeciphermentService.getFirstByPeriodIdAndType(period.prevPeriod?.id, FORM_18)
        val form20Prev = productDeciphermentService.getFirstByPeriodIdAndType(period.prevPeriod?.id, FORM_20)
        val basicEconomicIndicator = form2Prev?.let { basicEconomicIndicatorService.read(productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(it, BASIC_PLAN_ECO_INDICATOR)?.longVal ?: 0) }

        //Значения из расшифровок
        val purchaseRawMaterials: BigDecimal = if (form4 != null) manager.total6Form(form4) else BigDecimal.ZERO // приобретение сырья, материалов и вспомогательных материалов
        val purchaseRawMaterialsPrev: BigDecimal = if (form4Prev != null) manager.total6Form(form4Prev) else BigDecimal.ZERO
        val purchaseComponent: BigDecimal = if (form6 != null) manager.total6Form(form6) else BigDecimal.ZERO // приобретение комплектующих изделий
        val purchaseComponentPrev: BigDecimal = if (form6Prev != null) manager.total6Form(form6Prev) else BigDecimal.ZERO
        val containerPacking: BigDecimal = if (form6_2 != null) manager.total6Form(form6_2) else BigDecimal.ZERO // тара и упаковка
        val containerPackingPrev: BigDecimal = if (form6_2Prev != null) manager.total6Form(form6_2Prev) else BigDecimal.ZERO
        val costsOfOwnProduct: BigDecimal = if (form6_3 != null) manager.total6Form(form6_3) else BigDecimal.ZERO // затраты на изделия собственного производства
        val costsOfOwnProductPrev: BigDecimal = if (form6_3Prev != null) manager.total6Form(form6_3Prev) else BigDecimal.ZERO
        val sumForm9: BigDecimal = if (form9 != null) manager.total9Form(form9).first else BigDecimal.ZERO // основная заработная плата
        val sumForm9Prev: BigDecimal = if (form9Prev != null) manager.total9Form(form9Prev).first else BigDecimal.ZERO
        val otherDirestCosts: BigDecimal = if (form18 != null) manager.total18Form(form18).first else BigDecimal.ZERO // прочие прямые затраты
        val otherDirestCostsPrev: BigDecimal = if (form18Prev != null) manager.total18Form(form18Prev).first else BigDecimal.ZERO

        val oneHundred = BigDecimal.valueOf(100)
        val isZero = { value: BigDecimal -> value.compareTo(BigDecimal.ZERO) == 0 }
        val absoluteValue = { firstValue: BigDecimal, secondValue: BigDecimal -> firstValue - secondValue } // абсолютное отклонение
        val relativeValue = { firstValue: BigDecimal, secondValue: BigDecimal -> if (isZero(secondValue)) BigDecimal.ZERO else ((firstValue - secondValue) / secondValue) } // относительное отклонение

        val resultList = mutableListOf<Item>()

        // Материальные затраты
        val materialCosts = purchaseRawMaterials + purchaseComponent + containerPacking + costsOfOwnProduct
        val materialCostsPrev = purchaseRawMaterialsPrev + purchaseComponentPrev + containerPackingPrev + costsOfOwnProductPrev
        resultList.add(Item("Материальные затраты - всего:", null, materialCosts, null, materialCostsPrev))
        resultList.add(Item("- приобретение сырья, материалов и вспомогательных материалов", null, purchaseRawMaterials, null, purchaseRawMaterialsPrev))
        resultList.add(Item("- приобретение полуфабрикатов"))
        resultList.add(Item("- возвратные отходы"))
        resultList.add(Item("- приобретение комплектующих изделий", null, purchaseComponent, null, purchaseComponentPrev))
        resultList.add(Item("- оплата работ и услуг сторонних организаций производственного характера"))
        resultList.add(Item("- транспортно-заготовительные затраты"))
        resultList.add(Item("- топливо на технологические цели"))
        resultList.add(Item("- энергия на технологические цели"))

        resultList.add(Item("- тара и упаковка", null, containerPacking, null, containerPackingPrev, absoluteValue(containerPacking, containerPackingPrev), relativeValue(containerPacking, containerPackingPrev)))
        resultList.add(Item("- затраты на изделия собственного производства", null, costsOfOwnProduct, null, costsOfOwnProductPrev, absoluteValue(costsOfOwnProduct, costsOfOwnProductPrev), relativeValue(costsOfOwnProduct, costsOfOwnProductPrev)))

        // Дополнительная заработная плата
        val addSalary = sumForm9 * additionalSalary / oneHundred
        val addSalaryPrev = if (form9Prev != null) sumForm9Prev * BigDecimal.valueOf(basicEconomicIndicator?.additionalSalary ?: 0.0) / oneHundred else BigDecimal.ZERO
        resultList.add(Item("Затраты на оплату труда - всего:"))
        resultList.add(Item("- Основная заработная плата", null, sumForm9, null, sumForm9Prev, absoluteValue(sumForm9, sumForm9Prev), relativeValue(sumForm9, sumForm9Prev)))
        resultList.add(Item("- Дополнительная заработная плата", additionalSalary, addSalary, BigDecimal.valueOf(basicEconomicIndicator?.additionalSalary ?: 0.0), addSalaryPrev, absoluteValue(addSalary, addSalaryPrev), relativeValue(addSalary, addSalaryPrev)))

        // Страховые взносы на обязательное социальное страхование
        val insurance = sumForm9 * socialSecurityContribution / oneHundred
        val insurancePrev = if (form9Prev != null) sumForm9Prev * BigDecimal.valueOf(basicEconomicIndicator?.socialInsurance ?: 0.0) / oneHundred else BigDecimal.ZERO
        resultList.add(Item("Страховые взносы на обязательное социальное страхование", socialSecurityContribution, insurance, BigDecimal.valueOf(basicEconomicIndicator?.socialInsurance ?: 0.0) , insurancePrev, absoluteValue(insurance, insurancePrev), relativeValue(insurance, insurancePrev)))
        resultList.add(Item("Затраты на подготовку и освоение производства - всего:"))
        resultList.add(Item("пусковые затраты"))
        resultList.add(Item("затраты на подготовку и освоение новых видов продукции"))
        resultList.add(Item("Затраты на специальную технологическую оснастку"))
        resultList.add(Item("Затраты на специальное оборудование для научных (экспериментальных) работ"))
        resultList.add(Item("Специальные затраты"))

        // Общепроизводственные затраты
        val generalProductionCostPlan = sumForm9 * generalProductionCost / oneHundred
        val generalProductionCostPlanPrev = if (form9Prev != null) sumForm9Prev * BigDecimal.valueOf(basicEconomicIndicator?.productionCosts ?: 0.0)  / oneHundred else BigDecimal.ZERO
        resultList.add(Item("- Общепроизводственные затраты", generalProductionCost, generalProductionCostPlan, BigDecimal.valueOf(basicEconomicIndicator?.productionCosts ?: 0.0), generalProductionCostPlanPrev, absoluteValue(generalProductionCostPlan, generalProductionCostPlanPrev), relativeValue(generalProductionCostPlan, generalProductionCostPlanPrev)))

        // Общехозяйственные затраты
        val generalOperationCostPlan = sumForm9 * generalOperationCost / oneHundred
        val generalOperationCostPlanPrev = if (form9Prev != null) sumForm9Prev * BigDecimal.valueOf(basicEconomicIndicator?.householdExpenses ?: 0.0) / oneHundred else BigDecimal.ZERO
        resultList.add(Item("- Общехозяйственные затраты", generalOperationCost, generalOperationCostPlan, BigDecimal.valueOf(basicEconomicIndicator?.householdExpenses ?: 0.0), generalOperationCostPlanPrev, absoluteValue(generalOperationCostPlan, generalOperationCostPlanPrev), relativeValue(generalOperationCostPlan, generalOperationCostPlanPrev)))
        resultList.add(Item("Затраты на командировки"))
        resultList.add(Item("Прочие прямые затраты", null, otherDirestCosts, null, otherDirestCostsPrev))
        resultList.add(Item("Затраты по работам (услугам), выполняемым (оказываемым) сторонними организациями"))
        resultList.add(Item("Производственная себестоимость\n (сумма строк 0100, 0200, 0300, 0400, 0500 – 1200)"))
        resultList.add(Item("Коммерческие (внепроизводственные) затраты"))
        resultList.add(Item("Проценты по кредитам"))
        resultList.add(Item("Административно-управленческие расходы"))

        // Собственные затраты
        val ownCosts = sumForm9 + addSalary + insurance + generalOperationCostPlan + generalProductionCostPlan
        val ownCostsPrev = sumForm9Prev + addSalaryPrev + insurancePrev + generalOperationCostPlanPrev + generalProductionCostPlanPrev
        resultList.add(Item("Собственные затраты", null, ownCosts, null, ownCostsPrev))

        // Полная себестоимость
        val productionCost = materialCosts - purchaseRawMaterials + ownCosts + otherDirestCosts
        val productionCostPrev = materialCostsPrev - purchaseRawMaterialsPrev + ownCostsPrev + otherDirestCostsPrev
        resultList.add(Item("Полная себестоимость", null, productionCost, null, productionCostPrev, absoluteValue(productionCost, productionCostPrev), relativeValue(productionCost, productionCostPrev)))

        // Прибыль
        val profit = if (form20 != null) manager.total20Form(form20) else BigDecimal.ZERO
        val profitPrev = if (form20Prev != null) manager.total20Form(form20Prev) else BigDecimal.ZERO
        // Нормативы за отчетный период
        val profitValuePrev = when {
            (purchaseComponentPrev + otherDirestCostsPrev) * 0.01.toBigDecimal() > profitPrev -> if (isZero(purchaseComponentPrev + otherDirestCostsPrev)) BigDecimal.ZERO else profitPrev / (purchaseComponentPrev + otherDirestCostsPrev)
            else -> BigDecimal.ONE
        }
        val profitValueSecondPrev = when {
            (purchaseComponentPrev + otherDirestCostsPrev) * 0.01.toBigDecimal() > profitPrev -> null
            isZero(ownCostsPrev) -> BigDecimal.ZERO
            else -> (profitPrev - ((purchaseComponentPrev + otherDirestCostsPrev) * profitValuePrev / oneHundred) / ownCostsPrev)
        }
        resultList.add(Item("Прибыль", profitSecond, profit, profitValuePrev, profitPrev, absoluteValue(profit, profitPrev), relativeValue(profit, profitPrev), profitFirst, profitValueSecondPrev))

        // Цена продукции (без НДС)
        val productPrice = productionCost + profit
        val productPricePrev = productionCostPrev + profitPrev
        resultList.add(Item("Цена продукции (без НДС)", null, productPrice, null, productPricePrev, absoluteValue(productPrice, productPricePrev), relativeValue(productPrice, productPricePrev)))
        resultList.add(Item("ЦЕНА согл. с ПЗ", null, productPrice, null, productPricePrev, absoluteValue(productPrice, productPricePrev), relativeValue(productPrice, productPricePrev)))

        // Рентабельность
        val profitability = if (!isZero(productPrice)) profit / productPrice * oneHundred else BigDecimal.ZERO
        val profitabilityPrev = if (!isZero(productPricePrev)) profitPrev / productPricePrev * oneHundred else BigDecimal.ZERO
        resultList.add(Item("Рентабельность,%", null, profitability, null, profitabilityPrev))

        // Трудоемкость
        val laborIntensity = if (form9 != null) manager.total9Form(form9).second else BigDecimal.ZERO
        val laborIntensityPrev = if (form9Prev != null) manager.total9Form(form9Prev).second else BigDecimal.ZERO
        resultList.add(Item("Трудоемкость, н/ч", null, laborIntensity, null, laborIntensityPrev, absoluteValue(laborIntensity, laborIntensityPrev), relativeValue(laborIntensity, laborIntensityPrev)))

        // Средняя стоимость
        val overageCost = if (!isZero(laborIntensity)) sumForm9 / laborIntensity else BigDecimal.ZERO
        val overageCostPrev = if (!isZero(laborIntensityPrev)) sumForm9Prev / laborIntensityPrev else BigDecimal.ZERO
        resultList.add(Item("Средняя стоимость 1 н/ч (руб.)", null, overageCost, null, overageCostPrev, absoluteValue(overageCost, overageCostPrev), relativeValue(overageCost, overageCostPrev)))

        val costContributed = if (!isZero(productionCost)) (materialCosts + otherDirestCosts) / productionCost else BigDecimal.ZERO
        val costContributedPrev = if (!isZero(productionCostPrev)) (materialCostsPrev + otherDirestCostsPrev) / productionCostPrev else BigDecimal.ZERO
        resultList.add(Item("Доля привнесенных затрат в с/с (%)", null, costContributed, null, costContributedPrev, absoluteValue(costContributed, costContributedPrev), relativeValue(costContributed, costContributedPrev)))

        //Доля привнесенных затрат в цене
        val costContributedInPrice = if (!isZero(productPrice)) (materialCosts + otherDirestCosts) / productPrice else BigDecimal.ZERO
        val costContributedInPricePrev = if (!isZero(productPricePrev)) (materialCostsPrev + otherDirestCostsPrev) / productPricePrev else BigDecimal.ZERO
        resultList.add(Item("Доля привнесенных затрат в цене (%)", null, costContributedInPrice, null, costContributedInPricePrev, absoluteValue(costContributedInPrice, costContributedInPricePrev), relativeValue(costContributedInPrice, costContributedInPricePrev)))

        //Рентабельность для заключения
        val profitabilityResult = if (isZero(profit)) BigDecimal.ZERO else (ownCosts + purchaseComponent + containerPacking + otherDirestCosts) / profit
        val profitabilityResultPrev = if (isZero(profitPrev)) BigDecimal.ZERO else (ownCostsPrev + purchaseComponentPrev + containerPackingPrev + otherDirestCostsPrev) / profitPrev
        resultList.add(Item("Рентабельность для заключения %", null, profitabilityResult, null, profitabilityResultPrev))

        // Цена с КД
        val priceKdPrev = productPricePrev * 1.039.toBigDecimal()
        resultList.add(Item("Цена с КД (2019/2020) 1,039", null, productPrice, null, priceKdPrev, absoluteValue(productPrice, priceKdPrev)))

        // Расчет прибыли
        val profitCalc = ((materialCosts - purchaseRawMaterials + otherDirestCosts) * profitSecond / oneHundred + ownCosts * profitFirst / oneHundred)
        resultList.add(Item("", null, profitCalc))

        // Цена из калькуляции 2ой формы
        val total = productionCost + profit
        resultList.add(Item("Проверка расчета Прибыли", null, profit - profitCalc))
        resultList.add(Item("Цена б/НДС из Калькуляции", null, total))
        resultList.add(Item("Цена для обратного счета", null, total))

        // Величина для СЗ
        val valueSZ = if (total > productionCost && !isZero(ownCosts)) (total - productionCost - (materialCosts - purchaseComponent + otherDirestCosts) * profitSecond) / ownCosts else null
        resultList.add(Item("Величина % для СЗ", null, valueSZ))

        return resultList
    }

    @PostMapping("/analysis/save")
    fun analysisSave(@RequestPart form: DynamicObject) {
        val periodId = form.longNotNull(ObjAttr.PERIOD)
        val period = productDeciphermentPeriodService.read(periodId) ?: throw AlertUIException("Период не найден")
        baseService.exec {
            val formId = form.long(ObjAttr.ID)
            val analysisVal = formId?.let { productPriceAnalysisValService.read(formId) ?: throw AlertUIException("Расшифровка цены не найдена") } ?: ProductPriceAnalysisVal()
            analysisVal.apply {
                additionalSalary = form.bigDecimalNotNull(ObjAttr.ADDITIONAL_SALARY)
                socialSecurityContribution = form.bigDecimalNotNull(ObjAttr.SOCIAL_SECURITY_CONTRIBUTION)
                generalProductionCost = form.bigDecimalNotNull(ObjAttr.GENERAL_PRODUCTION_COST)
                generalOperationCost = form.bigDecimalNotNull(ObjAttr.GENERAL_OPERATION_COST)
                deflatorCoefficient = form.bigDecimalNotNull(ObjAttr.DEFLATOR_COEFFICIENT)
                this.period = period
            }
            productPriceAnalysisValService.save(analysisVal)
        }
    }

    @PostMapping("/edit/save")
    fun editSave(
        @RequestPart form: DynamicObject,
        @RequestPart(required = false) file: MultipartFile?
    ): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)

        val formId = form.longNotNull(ObjAttr.ID)
        val decipherment = productDeciphermentService.read(formId) ?: throw AlertUIException("Расшифровка цены не найдена")
        val deciphermentType = decipherment.type?.enum
        val product = decipherment.period?.product ?: throw AlertUIException("Изделие не найдено")
        val productId = product.id

        if (deciphermentType != FORM_1 && decipherment.approved) throw AlertUIException("Форма утверждена. Сохранение невозможно")

        // Валидация
        val existsFile = FileStorage<ProductDecipherment, SingularFileStorableType>(form.long(ObjAttr.FILE_ID))
        val formComment = form.stringNotNull(ObjAttr.COMMENT).trim()
        if (decipherment.approved.not()) {
            validateFile(errors, existsFile, file, ObjAttr.FILE)
            if (formComment.length > 256) errors.putError(ObjAttr.COMMENT, ValidatorMsg.RANGE_LENGTH, 0, 256)
        }
        when (deciphermentType) {
            FORM_1 -> {
                if (form.long(ObjAttr.CUSTOMER_ID) == null) errors.putError(ObjAttr.CUSTOMER_ID, ValidatorMsg.REQUIRED)
                if (decipherment.approved.not()) {
                    if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
                    if (form.long(ObjAttr.DIRECTOR_ECO_ID) == null) errors.putError(ObjAttr.DIRECTOR_ECO_ID, ValidatorMsg.REQUIRED)
                    val procedure = form.stringNotNull(ObjAttr.PROCEDURE).trim()
                    if (procedure.isBlank() || procedure.length > 1024) errors.putError(ObjAttr.PROCEDURE, ValidatorMsg.RANGE_LENGTH, 1, 1024)
                    val priceDetermination = form.stringNotNull(ObjAttr.PRICE_DETERMINATION).trim()
                    if (priceDetermination.isBlank() || priceDetermination.length > 1024) errors.putError(ObjAttr.PRICE_DETERMINATION, ValidatorMsg.RANGE_LENGTH, 1, 1024)
                    val note = form.stringNotNull(ObjAttr.NOTE).trim()
                    if (note.isBlank() || note.length > 1024) errors.putError(ObjAttr.NOTE, ValidatorMsg.RANGE_LENGTH, 1, 1024)
                    //val techDoc = form.stringNotNull(ObjAttr.TECH_DOC).trim()
                    //if (techDoc.isBlank() || techDoc.length > 1024) errors.putError(ObjAttr.TECH_DOC, ValidatorMsg.RANGE_LENGTH, 1, 1024)
                    val startDate = form.stringNotNull(ObjAttr.START_DATE).trim()
                    if (startDate.isBlank() || startDate.length > 1024) errors.putError(ObjAttr.START_DATE, ValidatorMsg.RANGE_LENGTH, 1, 1024)
                    val endDate = form.stringNotNull(ObjAttr.END_DATE).trim()
                    if (endDate.isBlank() || endDate.length > 1024) errors.putError(ObjAttr.END_DATE, ValidatorMsg.RANGE_LENGTH, 1, 1024)
                    val priceType = form.stringNotNull(ObjAttr.PRICE_TYPE).trim()
                    if (priceType.isBlank() || priceType.length > 1024) errors.putError(ObjAttr.PRICE_TYPE, ValidatorMsg.RANGE_LENGTH, 1, 1024)
                }
            }
            FORM_2 -> {
                if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.ECO_INDICATOR_ID) == null) errors.putError(ObjAttr.ECO_INDICATOR_ID, ValidatorMsg.REQUIRED)
                //val techDoc = form.stringNotNull(ObjAttr.TECH_DOC).trim()
                //if (techDoc.isBlank() || techDoc.length > 1024) errors.putError(ObjAttr.TECH_DOC, ValidatorMsg.RANGE_LENGTH, 1, 1024)
            }
            FORM_3 -> {
                if (form.long(ObjAttr.DIRECTOR_ECO_ID) == null) errors.putError(ObjAttr.DIRECTOR_ECO_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.ACCOUNTANT_USER_ID) == null) errors.putError(ObjAttr.ACCOUNTANT_USER_ID, ValidatorMsg.REQUIRED)
                //val techDoc = form.stringNotNull(ObjAttr.TECH_DOC).trim()
                //if (techDoc.isBlank() || techDoc.length > 1024) errors.putError(ObjAttr.TECH_DOC, ValidatorMsg.RANGE_LENGTH, 1, 1024)
            }
            FORM_9 -> {
                if (form.long(ObjAttr.JUSTIFICATION_ID) == null) errors.putError(ObjAttr.JUSTIFICATION_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.LABOUR_INTENSITY_ID) == null) errors.putError(ObjAttr.LABOUR_INTENSITY_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.CHIEF_TECH_ID) == null) errors.putError(ObjAttr.CHIEF_TECH_ID, ValidatorMsg.REQUIRED)
            }
            FORM_18 -> {
                if (form.long(ObjAttr.REVIEW_JUSTIFICATION_ID) == null) errors.putError(ObjAttr.REVIEW_JUSTIFICATION_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.HEAD_PROD_ID) == null) errors.putError(ObjAttr.HEAD_PROD_ID, ValidatorMsg.REQUIRED)
            }
            FORM_5 -> if (form.long(ObjAttr.HEAD_CONSTRUCT_ID) == null) errors.putError(ObjAttr.HEAD_CONSTRUCT_ID, ValidatorMsg.REQUIRED)
            FORM_7 -> {
                if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.HEAD_PROD_ID) == null) errors.putError(ObjAttr.HEAD_PROD_ID, ValidatorMsg.REQUIRED)
            }
            FORM_8 -> if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
            FORM_7_1, FORM_14, FORM_14_1, FORM_16, FORM_17 -> {
                if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.HEAD_CONSTRUCT_ID) == null) errors.putError(ObjAttr.HEAD_CONSTRUCT_ID, ValidatorMsg.REQUIRED)
            }
            FORM_10 -> {
                if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.CHIEF_TECH_ID) == null) errors.putError(ObjAttr.CHIEF_TECH_ID, ValidatorMsg.REQUIRED)
            }
            FORM_11, FORM_12, FORM_13, FORM_21 -> {
                if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.ACCOUNTANT_USER_ID) == null) errors.putError(ObjAttr.ACCOUNTANT_USER_ID, ValidatorMsg.REQUIRED)
            }
            FORM_19, FORM_22, FORM_23 -> {
                if (form.long(ObjAttr.HEAD_ECO_ID) == null) errors.putError(ObjAttr.HEAD_ECO_ID, ValidatorMsg.REQUIRED)
                if (form.long(ObjAttr.DIRECTOR_ECO_ID) == null) errors.putError(ObjAttr.DIRECTOR_ECO_ID, ValidatorMsg.REQUIRED)
            }
            else -> Unit
        }

        // Сохранение данных
        if (response.isValid) baseService.exec {
            when (deciphermentType) {
                FORM_1 -> {
                    if (okpdCodeService.existsByProductTypeId(decipherment.period?.product?.type?.id).not()) throw AlertUIException("Код ОКП/ОКПД2 не найден в справочнике")
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)
                    val directorEcoId = form.long(ObjAttr.DIRECTOR_ECO_ID)
                    val customerId = form.long(ObjAttr.CUSTOMER_ID)
                    val procedure = form.string(ObjAttr.PROCEDURE)
                    val priceDetermination = form.string(ObjAttr.PRICE_DETERMINATION)
                    val note = form.string(ObjAttr.NOTE)
                    //val techDoc = form.string(ObjAttr.TECH_DOC)
                    val startDate = form.string(ObjAttr.START_DATE)
                    val endDate = form.string(ObjAttr.END_DATE)
                    val priceType = form.string(ObjAttr.PRICE_TYPE)

                    customerId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CUSTOMER) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = CUSTOMER
                        attrVal.company = Company(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }

                    if (decipherment.approved.not()) {
                        headEcoId?.let {
                            val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                            attrVal.user = User(it)
                            productDeciphermentAttrValService.save(attrVal)
                        }
                        directorEcoId?.let {
                            val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO) ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = DIRECTOR_ECO
                            attrVal.user = User(it)
                            productDeciphermentAttrValService.save(attrVal)
                        }
                        procedure?.let {
                            val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROCEDURE) ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = PROCEDURE
                            attrVal.stringVal = it
                            productDeciphermentAttrValService.save(attrVal)
                        }
                        priceDetermination?.let {
                            val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRICE_DETERMINATION) ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = PRICE_DETERMINATION
                            attrVal.stringVal = it
                            productDeciphermentAttrValService.save(attrVal)
                        }
                        note?.let {
                            val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, NOTE) ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = NOTE
                            attrVal.stringVal = it
                            productDeciphermentAttrValService.save(attrVal)
                        }
                        run {
                            val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, TECH_DOC) ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = TECH_DOC
                            attrVal.stringVal = product.decimalNumber?.let { "$it ТУ" }
                            productDeciphermentAttrValService.save(attrVal)
                        }
                        startDate?.let {
                            val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, START_DATE) ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = START_DATE
                            attrVal.stringVal = it
                            productDeciphermentAttrValService.save(attrVal)
                        }
                        endDate?.let {
                            val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, END_DATE) ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = END_DATE
                            attrVal.stringVal = it
                            productDeciphermentAttrValService.save(attrVal)
                        }
                        priceType?.let {
                            val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRICE_TYPE) ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = PRICE_TYPE
                            attrVal.stringVal = it
                            productDeciphermentAttrValService.save(attrVal)
                        }
                    }
                }
                FORM_2 -> {
                    if (okpdCodeService.existsByProductTypeId(decipherment.period?.product?.type?.id).not()) throw AlertUIException("Код ОКП/ОКПД2 не найден в справочнике")
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)
                    //val techDoc = form.string(ObjAttr.TECH_DOC)
                    val ecoIndicatorId = form.long(ObjAttr.ECO_INDICATOR_ID)
                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    run {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, TECH_DOC) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = TECH_DOC
                        attrVal.stringVal = product.decimalNumber?.let { "$it ТУ" }
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    ecoIndicatorId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, BASIC_PLAN_ECO_INDICATOR) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = BASIC_PLAN_ECO_INDICATOR
                        attrVal.longVal = it
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_3 -> {
                    if (okpdCodeService.existsByProductTypeId(decipherment.period?.product?.type?.id).not()) throw AlertUIException("Код ОКП/ОКПД2 не найден в справочнике")
                    val directorEcoId = form.long(ObjAttr.DIRECTOR_ECO_ID)
                    val accountantUserId = form.long(ObjAttr.ACCOUNTANT_USER_ID)
                    //val techDoc = form.string(ObjAttr.TECH_DOC)

                    directorEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = DIRECTOR_ECO
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    accountantUserId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = ACCOUNTANT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    run {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, TECH_DOC) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = TECH_DOC
                        attrVal.stringVal = product.decimalNumber?.let { "$it ТУ" }
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_5 -> {
                    val headConstructId = form.long(ObjAttr.HEAD_CONSTRUCT_ID)

                    headConstructId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_CONSTRUCT_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_7 -> {
                    val headProdId = form.long(ObjAttr.HEAD_PROD_ID)
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)

                    headProdId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PRODUCTION) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PRODUCTION
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }

                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_8 -> {
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)
                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_7_1, FORM_14, FORM_14_1, FORM_16, FORM_17 -> {
                    val headConstructId = form.long(ObjAttr.HEAD_CONSTRUCT_ID)
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)

                    headConstructId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_CONSTRUCT_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }

                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_9 -> {
                    val justificationId = form.long(ObjAttr.JUSTIFICATION_ID)
                    val labourIntensityId = form.long(ObjAttr.LABOUR_INTENSITY_ID)
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)
                    val chiefTechId = form.long(ObjAttr.CHIEF_TECH_ID)

                    // Проверка трудоемкости на дату утверждения
                    val labourIntensityEntry = labourIntensityEntryService.getByLabourIntensityIdAndProductId(labourIntensityId, productId) ?: throw AlertUIException("Трудоемкость изделия не найдена")
                    if (labourIntensityEntry.approvalDate == null) throw AlertUIException("Невозможно использовать неутвержденную трудоемкость")
                    // Проверка значений трудоемкости на нулевые значения
                    val workCostList = productWorkCostService.getAllByJustificationId(justificationId)
                    val labourIntensityList = labourIntensityOperationService.getAllByLabourIntensityIdAndProductId(labourIntensityId, decipherment.period?.product?.id)
                    if (labourIntensityList.any { li -> workCostList.find { li.operation?.id == it.workType?.id }?.cost == null }) throw AlertUIException("Стоимость работ не может содержать нулевые значения")

                    justificationId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_WORK_COST_JUSTIFICATION) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = PRODUCT_WORK_COST_JUSTIFICATION
                        attrVal.productWorkCostJustification = ProductWorkCostJustification(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    labourIntensityId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_LABOUR_INTENSITY) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = PRODUCT_LABOUR_INTENSITY
                        attrVal.productLabourIntensity = ProductLabourIntensity(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    chiefTechId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CHIEF_TECHNOLOGIST) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = CHIEF_TECHNOLOGIST
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_10 -> {
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)
                    val chiefTechId = form.long(ObjAttr.CHIEF_TECH_ID)

                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    chiefTechId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CHIEF_TECHNOLOGIST) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = CHIEF_TECHNOLOGIST
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_11, FORM_12, FORM_13, FORM_21-> {
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)
                    val accountant = form.long(ObjAttr.ACCOUNTANT_USER_ID)

                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    accountant?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = ACCOUNTANT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_18 -> {
                    val reviewJustificationId = form.long(ObjAttr.REVIEW_JUSTIFICATION_ID)
                    val researchJustificationId = form.long(ObjAttr.RESEARCH_JUSTIFICATION_ID)
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)
                    val headProdId = form.long(ObjAttr.HEAD_PROD_ID)

                    val groupId = decipherment.period?.product?.classificationGroup?.id
                    val reviewPrice = productSpecReviewPriceService.getByJustificationIdAndGroupId(reviewJustificationId, groupId)
                    val researchPrice = productSpecResearchPriceService.getByJustificationIdAndGroupId(researchJustificationId, groupId)
                    if ((reviewJustificationId != null && reviewPrice == null) || (researchJustificationId != null && researchPrice == null)) throw AlertUIException("Цены на СП или СИ не могут быть пустыми")

                    reviewJustificationId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_SPEC_REVIEW_JUSTIFICATION) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = PRODUCT_SPEC_REVIEW_JUSTIFICATION
                        attrVal.productSpecReviewJustification = ProductSpecReviewJustification(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    run {
                        var attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_SPEC_RESEARCH_JUSTIFICATION)
                        if (researchJustificationId == null) {
                            if (attrVal != null) productDeciphermentAttrValService.delete(attrVal) else Unit
                        } else {
                            attrVal = attrVal ?: ProductDeciphermentAttrVal()
                            attrVal.decipherment = decipherment
                            attrVal.attribute = PRODUCT_SPEC_RESEARCH_JUSTIFICATION
                            attrVal.productSpecResearchJustification = ProductSpecResearchJustification(researchJustificationId)
                            productDeciphermentAttrValService.save(attrVal)
                        }
                    }
                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    headProdId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PRODUCTION) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PRODUCTION
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_19, FORM_22, FORM_23 -> {
                    val directorEcoId = form.long(ObjAttr.DIRECTOR_ECO_ID)
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)

                    directorEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = DIRECTOR_ECO
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }

                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                FORM_20 -> {
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)
                    val profitabilityFirst = form.bigDecimal(ObjAttr.PROFITABILITY_FIRST)
                    val profitabilitySecond = form.bigDecimal(ObjAttr.PROFITABILITY_SECOND)

                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    profitabilityFirst?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROFITABILITY_FIRST) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = PROFITABILITY_FIRST
                        attrVal.decimalVal = it
                        productDeciphermentAttrValService.save(attrVal)
                    }
                    profitabilitySecond.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROFITABILITY_SECOND) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = PROFITABILITY_SECOND
                        attrVal.decimalVal = it
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                EXPLANATION_NOTE -> {
                    val headEcoId = form.long(ObjAttr.HEAD_ECO_ID)

                    headEcoId?.let {
                        val attrVal = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) ?: ProductDeciphermentAttrVal()
                        attrVal.decipherment = decipherment
                        attrVal.attribute = HEAD_PL_EC_DEPARTMENT
                        attrVal.user = User(it)
                        productDeciphermentAttrValService.save(attrVal)
                    }
                }
                else -> Unit
            }

            // Сохранение общих данных расшифровки
            decipherment.apply {
                version = form.longNotNull(ObjAttr.VERSION)
                comment = if (decipherment.approved) comment else formComment
                if (decipherment.ready.not() && decipherment.approved.not()) decipherment.ready = true
                productDeciphermentService.save(this)
            }
            if (existsFile.id == null && decipherment.approved.not()) fileStorageService.saveEntityFile(decipherment, ProductDeciphermentFile, file)
        }
        return response
    }

    @PostMapping("/approve")
    fun approve(
        id: Long,
        toApprove: Boolean
    ) = baseService.exec {
        val decipherment = productDeciphermentService.read(id) ?: throw AlertUIException("Форма не найдена")
        val deciphermentType = decipherment.type?.enum
        if (toApprove && decipherment.ready.not()) throw AlertUIException("Форма не заполнена для утверждения")
        if (toApprove && decipherment.approved) throw AlertUIException("Форма была утверждена")
        if (toApprove.not() && decipherment.approved.not()) throw AlertUIException("Утверждение было снято с формы")

        val periodFormList = productDeciphermentService.getAllByPeriodId(decipherment.period?.id)
        val formList = listOf(FORM_1, FORM_2, FORM_3, FORM_20)
        val otherFormList = ProductDeciphermentTypeEnum.values().filterNot { formList.contains(it) }
        var isOtherAllApproved = true
        otherFormList.forEach { oit ->
            if (periodFormList.firstOrNull { it.type?.enum == oit }?.approved != true) {
                isOtherAllApproved = false
                return@forEach
            }
        }
        val isForm1Approved = periodFormList.firstOrNull { it.type?.enum == FORM_1 }?.approved ?: false
        val isForm2Approved = periodFormList.firstOrNull { it.type?.enum == FORM_2 }?.approved ?: false
        val isForm3Approved = periodFormList.firstOrNull { it.type?.enum == FORM_3 }?.approved ?: false
        val isForm20Approved = periodFormList.firstOrNull { it.type?.enum == FORM_20 }?.approved ?: false

        if (toApprove) {
            if (deciphermentType == FORM_20 && isOtherAllApproved.not()) {
                throw AlertUIException("Невозможно утвердить. Сначала утвердите все формы кроме №1, №2, №3, №20")
            } else if (deciphermentType == FORM_2 && (isOtherAllApproved.not() || isForm20Approved.not())) {
                throw AlertUIException("Невозможно утвердить. Сначала утвердите все формы кроме №1, №2, №3")
            } else if (deciphermentType == FORM_3 && (isOtherAllApproved.not() || isForm20Approved.not() || isForm2Approved.not())) {
                throw AlertUIException("Невозможно утвердить. Сначала утвердите все формы кроме №1, №3")
            } else if (deciphermentType == FORM_1 && (isOtherAllApproved.not() || isForm20Approved.not() || isForm2Approved.not() || isForm3Approved.not())) {
                throw AlertUIException("Невозможно утвердить. Сначала утвердите все формы кроме №1")
            }
            decipherment.ready = false
        } else {
            val form1 = periodFormList.firstOrNull { it.type?.enum == FORM_1 }
            if (lotService.existsByPriceProtocolId(form1?.id)) throw AlertUIException("Невозможно снять утверждение. Протокол цены используется в ведомости поставки договора")
            if (deciphermentType == FORM_3 && isForm1Approved) {
                throw AlertUIException("Невозможно снять утверждение. Сначала снимите утверждение с формы №1")
            } else if (deciphermentType == FORM_2 && (isForm1Approved || isForm3Approved)) {
                throw AlertUIException("Невозможно снять утверждение. Сначала снимите утверждение с форм №1, №3")
            } else if (deciphermentType == FORM_20 && (isForm1Approved || isForm2Approved || isForm3Approved)) {
                throw AlertUIException("Невозможно снять утверждение. Сначала снимите утверждение с форм №1, №2, №3")
            } else if (isForm1Approved || isForm2Approved || isForm3Approved || isForm20Approved) {
                throw AlertUIException("Невозможно снять утверждение. Сначала снимите утверждение с форм №1, №2, №3, №20")
            }
            decipherment.ready = true
        }
        decipherment.approved = toApprove
    }
}