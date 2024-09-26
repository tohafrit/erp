package ru.korundm.controller.view.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.dto.DropdownOption
import ru.korundm.enumeration.ProductDeciphermentAttr.*
import ru.korundm.enumeration.ProductDeciphermentTypeEnum.*
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.FileStorageType.ProductDeciphermentFile
import ru.korundm.util.KtCommonUtil.currencyFormat
import java.math.BigDecimal

@ViewController([RequestPath.View.Prod.PRODUCT + "/detail/decipherment"])
class ProductDetailDeciphermentViewProdController(
    private val jsonMapper: ObjectMapper,
    private val productDeciphermentService: ProductDeciphermentService,
    private val productDeciphermentPeriodService: ProductDeciphermentPeriodService,
    private val productService: ProductService,
    private val productDeciphermentTypeService: ProductDeciphermentTypeService,
    private val fileStorageService: FileStorageService,
    private val productDeciphermentAttrValService: ProductDeciphermentAttrValService,
    private val userService: UserService,
    private val productWorkCostJustificationService: ProductWorkCostJustificationService,
    private val labourIntensityService: ProductLabourIntensityService,
    private val productSpecReviewJustificationService: ProductSpecReviewJustificationService,
    private val productSpecReviewPriceService: ProductSpecReviewPriceService,
    private val productSpecResearchPriceService: ProductSpecResearchPriceService,
    private val basicEconomicIndicatorService: BasicEconomicIndicatorService,
    private val okpdCodeService: OkpdCodeService,
    private val productPriceAnalysisValService: ProductPriceAnalysisValService
) {

    // Страница расчета цены
    @GetMapping
    fun main(
        model: ModelMap,
        productId: Long,
        @RequestParam(required = false) periodId: Long?
    ): String {
        val product = productService.read(productId) ?: throw AlertUIException("Изделие не найдено")
        val period = if (periodId == null) productDeciphermentPeriodService.getLastByProductId(productId) else productDeciphermentPeriodService.read(periodId)
        val prevPeriod = period?.prevPeriod
        model.addAttribute("productId", product.id)
        model.addAttribute("periodId", period?.id)
        model.addAttribute("periodName", period?.pricePeriod?.name)
        model.addAttribute("prevPeriodName", prevPeriod?.pricePeriod?.name)
        model.addAttribute("periodStartDate", period?.pricePeriod?.startDate?.format(DATE_FORMATTER))
        model.addAttribute("prevPeriodStartDate", prevPeriod?.pricePeriod?.startDate?.format(DATE_FORMATTER))
        model.addAttribute("periodEndDate", if (period?.endDate == null) "н.в." else period.endDate?.format(DATE_FORMATTER))
        model.addAttribute("prevPeriodEndDate", prevPeriod?.let { if (it.endDate == null) "н.в." else it.endDate?.format(DATE_FORMATTER) })
        model.addAttribute("periodPriceWoPack", period?.priceWoPack.currencyFormat())
        model.addAttribute("prevPeriodPriceWoPack", prevPeriod?.priceWoPack.currencyFormat())
        model.addAttribute("periodPricePack", period?.pricePack.currencyFormat())
        model.addAttribute("prevPeriodPricePack", prevPeriod?.pricePack.currencyFormat())
        model.addAttribute("periodPricePackResearch", period?.pricePackResearch.currencyFormat())
        model.addAttribute("prevPeriodPricePackResearch", prevPeriod?.pricePackResearch.currencyFormat())
        return "prod/include/product/detail/decipherment"
    }

    // Страница списка периодов изделия
    @GetMapping("/period")
    fun period(
        model: ModelMap,
        productId: Long,
        periodId: Long?
    ): String {
        productService.read(productId) ?: throw AlertUIException("Изделие не найдено")
        model.addAttribute("productId", productId)
        model.addAttribute("periodId", periodId)
        return "prod/include/product/detail/decipherment/period"
    }

    // Страница списка периодов изделия
    @GetMapping("/period/edit")
    fun periodEdit(model: ModelMap, id: Long?, productId: Long): String {
        val period = id?.let { productDeciphermentPeriodService.read(it) ?: throw AlertUIException("Период не найден") }
        model.addAttribute("productId", productId)
        model.addAttribute("id", period?.id)
        model.addAttribute("version", period?.version)
        model.addAttribute("planPeriodId", period?.pricePeriod?.id)
        model.addAttribute("planPeriodName", period?.pricePeriod?.let { "${it.name} от ${it.startDate?.format(DATE_FORMATTER)}" })
        model.addAttribute("reportPeriodId", period?.prevPeriod?.pricePeriod?.id)
        model.addAttribute("reportPeriodName", period?.prevPeriod?.pricePeriod?.let { "${it.name} от ${it.startDate?.format(DATE_FORMATTER)}" })
        return "prod/include/product/detail/decipherment/period/edit"
    }

    // Страница списка периодов действия цен
    @GetMapping("/period/edit/plan-period")
    fun periodEditPlanPeriod(model: ModelMap, productId: Long): String {
        model.addAttribute("productId", productId)
        return "prod/include/product/detail/decipherment/period/edit/planPeriod"
    }

    // Страница фильтра списка периодов действия цен
    @GetMapping("/period/edit/plan-period/filter")
    fun periodEditPlanPeriodFilter() = "prod/include/product/detail/decipherment/period/edit/plan-period/filter"

    // Страница списка отчетных периодов
    @GetMapping("/period/edit/report-period")
    fun periodEditReportPeriod(
        model: ModelMap,
        productId: Long,
        planPeriodId: Long,
        excludePeriodId: Long?
    ): String {
        model.addAttribute("productId", productId)
        model.addAttribute("planPeriodId", planPeriodId)
        model.addAttribute("excludePeriodId", excludePeriodId)
        return "prod/include/product/detail/decipherment/period/edit/reportPeriod"
    }

    // Список расшифровок для добавления
    @GetMapping("/add")
    fun add(model: ModelMap, periodId: Long): String {
        val typeList = productDeciphermentTypeService.all.toMutableList()
        val existTypeIdList = productDeciphermentService.getTypeIdListByPeriodId(periodId)
        typeList.removeIf { existTypeIdList.contains(it.id) }
        model.addAttribute("data", jsonMapper.writeValueAsString(typeList))
        model.addAttribute("periodId", periodId)
        return "prod/include/product/detail/decipherment/add"
    }

    // Редактирование расшифровки
    @GetMapping("/edit")
    fun edit(model: ModelMap, id: Long): String {
        val decipherment = productDeciphermentService.read(id) ?: throw AlertUIException("Форма не найдена")
        val file = fileStorageService.readOneSingular(decipherment, ProductDeciphermentFile)
        if (decipherment.type?.enum != FORM_1 && decipherment.approved) throw AlertUIException("Форма утверждена. Редактирование невозможно")
        val product = decipherment.period?.product ?: throw AlertUIException("Изделие не найдено")
        model.addAttribute("id", id)
        model.addAttribute("version", decipherment.version)
        model.addAttribute("productId", product.id)
        model.addAttribute("formName", decipherment.type?.name)
        model.addAttribute("createDate", decipherment.createDate.format(DATE_FORMATTER))
        model.addAttribute("createdBy", decipherment.createdBy?.userOfficialName)
        model.addAttribute("fileId", file?.id)
        model.addAttribute("fileName", file?.name)
        model.addAttribute("fileUrlHash", file?.urlHash)
        model.addAttribute("comment", decipherment.comment)
        model.addAttribute("isApproved", decipherment.approved)
        when (decipherment.type?.enum) {
            FORM_9 -> {
                val headEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                val chiefTech = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CHIEF_TECHNOLOGIST)?.user
                model.addAttribute("headEcoId", headEco?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("chiefTechId", chiefTech?.id ?: userService.findByUserName("rudenko")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })

                val labourIntensity = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_LABOUR_INTENSITY)?.productLabourIntensity
                    ?: labourIntensityService.getLastApprovedByProductId(decipherment.period?.product?.id)
                model.addAttribute("labourIntensityId", labourIntensity?.id)
                model.addAttribute("labourIntensityName", labourIntensity?.name)

                val justification = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_WORK_COST_JUSTIFICATION)?.productWorkCostJustification
                    ?: productWorkCostJustificationService.getLastApproved()
                model.addAttribute("justificationId", justification?.id)
                model.addAttribute("justificationName", justification?.name)
                return "prod/include/product/detail/decipherment/editForm9"
            }
            FORM_18 -> {
                val headEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                val headProd = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PRODUCTION)?.user
                model.addAttribute("headEcoId", headEco?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("headProdId", headProd?.id ?: userService.findByUserName("tsarakhov_vu")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })

                val groupId = decipherment.period?.product?.classificationGroup?.id

                val reviewJustification = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_SPEC_REVIEW_JUSTIFICATION)?.productSpecReviewJustification
                    ?: productSpecReviewJustificationService.getLastApproved()
                val reviewPrice = productSpecReviewPriceService.getByJustificationIdAndGroupId(reviewJustification?.id, groupId)
                model.addAttribute("reviewJustificationId", reviewJustification?.id)
                model.addAttribute("reviewJustificationName", reviewJustification?.let { "${(reviewPrice?.price ?: .0).currencyFormat()} в ${it.name}" })

                val researchJustification = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_SPEC_RESEARCH_JUSTIFICATION)?.productSpecResearchJustification
                val researchPrice = productSpecResearchPriceService.getByJustificationIdAndGroupId(reviewJustification?.id, groupId)
                model.addAttribute("researchJustificationId", researchJustification?.id)
                model.addAttribute("researchJustificationName", researchJustification?.let { "${(researchPrice?.price ?: .0).currencyFormat()} в ${it.name}" })

                val classGroup = decipherment.period?.product?.classificationGroup
                model.addAttribute("classGroupName", "${classGroup?.number ?: ""} ${classGroup?.characteristic ?: ""}")
                return "prod/include/product/detail/decipherment/editForm18"
            }
            FORM_5 -> {
                val headConstruct = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT)?.user
                model.addAttribute("headConstructId", headConstruct?.id ?: userService.findByUserName("lepekhin_ep")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm5"
            }
            FORM_7 -> {
                val headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                val headProduction = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PRODUCTION)?.user
                model.addAttribute("headProdId", headProduction?.id ?: userService.findByUserName("tsarakhov_vu")?.id)
                model.addAttribute("headEcoId", headPlEc?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm7"
            }
            FORM_8 -> {
                val headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                model.addAttribute("headEcoId", headPlEc?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm8"
            }
            FORM_7_1, FORM_14, FORM_14_1, FORM_16, FORM_17 -> {
                val headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                val headConstruct = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT)?.user
                model.addAttribute("headConstructId", headConstruct?.id ?: userService.findByUserName("lepekhin_ep")?.id)
                model.addAttribute("headEcoId", headPlEc?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm7_1or14or14_1or16or17"
            }
            FORM_10 -> {
                val headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                val chiefTech = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CHIEF_TECHNOLOGIST)?.user
                model.addAttribute("chiefTechId", chiefTech?.id ?: userService.findByUserName("rudenko")?.id)
                model.addAttribute("headEcoId", headPlEc?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm10"
            }
            FORM_11, FORM_12, FORM_13, FORM_21 -> {
                val headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                val accountant = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT)?.user
                model.addAttribute("accountantUserId", accountant?.id ?: userService.findByUserName("zotova_ee")?.id)
                model.addAttribute("headEcoId", headPlEc?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm11or12or13or21"
            }
            FORM_19, FORM_22, FORM_23 -> {
                val headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                val directorEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO)?.user
                model.addAttribute("directorEcoId", directorEco?.id ?: userService.findByUserName("gorbenko_ag")?.id)
                model.addAttribute("headEcoId", headPlEc?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm19or22or23"
            }
            FORM_1 -> {
                val headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                val directorEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO)?.user
                val customer = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CUSTOMER)?.company

                model.addAttribute("okpdCode", okpdCodeService.getFirstByProductTypeId(decipherment.period?.product?.type?.id)?.code)
                model.addAttribute("customerId", customer?.id)
                model.addAttribute("customerName", customer?.name)
                model.addAttribute("customerLocation", customer?.location)
                model.addAttribute("directorEcoId", directorEco?.id ?: userService.findByUserName("gorbenko_ag")?.id)
                model.addAttribute("headEcoId", headPlEc?.id ?: userService.findByUserName("mochalov_ap")?.id)

                model.addAttribute("procedure", productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROCEDURE)?.stringVal)
                model.addAttribute("priceDetermination", productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRICE_DETERMINATION)?.stringVal)
                model.addAttribute("note", productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, NOTE)?.stringVal)
                model.addAttribute("techDoc", product.decimalNumber?.let { "$it ТУ" })
                model.addAttribute("startDate", productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, START_DATE)?.stringVal)
                model.addAttribute("endDate", productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, END_DATE)?.stringVal ?: "До пересмотра")
                model.addAttribute("priceType", productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRICE_TYPE)?.stringVal ?: "фиксированная цена")

                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm1"
            }
            FORM_2 -> {
                val headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user
                val ecoIndicator = basicEconomicIndicatorService.read(productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, BASIC_PLAN_ECO_INDICATOR)?.longVal ?: 0) ?: basicEconomicIndicatorService.findLastByApproveDate()

                model.addAttribute("ecoIndicatorId", ecoIndicator?.id)
                model.addAttribute("ecoIndicatorName", ecoIndicator?.name)
                model.addAttribute("ecoIndicatorApproveDate", ecoIndicator?.approvalDate?.format(DATE_FORMATTER))
                model.addAttribute("headEcoId", headPlEc?.id ?: userService.findByUserName("mochalov_ap")?.id)

                model.addAttribute("okpdCode", okpdCodeService.getFirstByProductTypeId(decipherment.period?.product?.type?.id)?.code)
                model.addAttribute("techDoc", product.decimalNumber?.let { "$it ТУ" })

                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm2"
            }
            FORM_3 -> {
                val dirEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO)?.user
                val accountant = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT)?.user

                model.addAttribute("okpdCode", okpdCodeService.getFirstByProductTypeId(decipherment.period?.product?.type?.id)?.code)
                model.addAttribute("directorEcoId", dirEco?.id ?: userService.findByUserName("gorbenko_ag")?.id)
                model.addAttribute("accountantUserId", accountant?.id ?: userService.findByUserName("zotova_ee")?.id)
                model.addAttribute("techDoc", product.decimalNumber?.let { "$it ТУ" })

                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/editForm3"
            }
            FORM_20 -> {
                val headEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user

                model.addAttribute("headEcoId", headEco?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                model.addAttribute("profitabilityFirst", productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROFITABILITY_FIRST)?.decimalVal ?: "")
                model.addAttribute("profitabilitySecond", productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROFITABILITY_SECOND)?.decimalVal ?: "")

                return "prod/include/product/detail/decipherment/editForm20"
            }
            EXPLANATION_NOTE -> {
                val headEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user

                model.addAttribute("headEcoId", headEco?.id ?: userService.findByUserName("mochalov_ap")?.id)
                model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
                return "prod/include/product/detail/decipherment/explanationNote"

            }
            else -> return "notFound"
        }
    }

    // Выбор базового планового экономического показателя
    @GetMapping("/edit-form2/eco-indicator")
    fun editForm2EcoIndicator() = "prod/include/product/detail/decipherment/edit-form2/ecoIndicator"

    // Выбор заказчика
    @GetMapping("/edit-form1/customer")
    fun editForm1Customer() = "prod/include/product/detail/decipherment/edit-form1/customer"

    // Выбор обоснования стоимости работ для формы 9
    @GetMapping("/edit-form9/cost-justification")
    fun editForm9CostJustification(model: ModelMap, id: Long?): String {
        model.addAttribute("id", id)
        return "prod/include/product/detail/decipherment/edit-form9/costJustification"
    }

    // Фильтр
    @GetMapping("/edit-form9/cost-justification/filter")
    fun editForm9CostJustificationFilter() = "prod/include/product/detail/decipherment/edit-form9/cost-justification/filter"

    // Выбор трудоемкости для формы 9
    @GetMapping("/edit-form9/labour-intensity")
    fun editForm9LabourIntensity(model: ModelMap, id: Long?, productId: Long): String {
        model.addAttribute("id", id)
        model.addAttribute("productId", productId)
        return "prod/include/product/detail/decipherment/edit-form9/labourIntensity"
    }

    // Фильтр
    @GetMapping("/edit-form9/labour-intensity/filter")
    fun editForm9LabourIntensityFilter() = "prod/include/product/detail/decipherment/edit-form9/labour-intensity/filter"

    // Выбор обоснования СП для формы 18
    @GetMapping("/edit-form18/review-justification")
    fun editForm18ReviewJustification(model: ModelMap, deciphermentId: Long): String {
        model.addAttribute("deciphermentId", deciphermentId)
        return "prod/include/product/detail/decipherment/edit-form18/reviewJustification"
    }

    // Фильтр
    @GetMapping("/edit-form18/review-justification/filter")
    fun editForm18ReviewJustificationFilter() = "prod/include/product/detail/decipherment/edit-form18/review-justification/filter"

    // Выбор обоснования СИ для формы 18
    @GetMapping("/edit-form18/research-justification")
    fun editForm18ResearchJustification(model: ModelMap, deciphermentId: Long): String {
        model.addAttribute("deciphermentId", deciphermentId)
        return "prod/include/product/detail/decipherment/edit-form18/researchJustification"
    }

    // Фильтр
    @GetMapping("/edit-form18/research-justification/filter")
    fun editForm18ResearchJustificationFilter() = "prod/include/product/detail/decipherment/edit-form18/research-justification/filter"

    @GetMapping("/edit-form1/customer/filter")
    fun editForm1CustomerFilter() = "prod/include/product/detail/decipherment/edit-form1/customer/filter"

    // Страница списка периодов изделия
    @GetMapping("/analysis")
    fun analysis(
        model: ModelMap,
        productId: Long,
        periodId: Long
    ): String {
        val product = productService.read(productId) ?: throw AlertUIException("Изделие не найдено")
        val period = productDeciphermentPeriodService.read(periodId) ?: throw AlertUIException("Период не найден")
        val analysisVal = productPriceAnalysisValService.findFirstByPeriodId(periodId)
        model.addAttribute("additionalSalary", analysisVal?.additionalSalary ?: BigDecimal.ZERO)
        model.addAttribute("socialSecurityContribution", analysisVal?.socialSecurityContribution ?: BigDecimal.ZERO)
        model.addAttribute("generalProductionCost", analysisVal?.generalProductionCost ?: BigDecimal.ZERO)
        model.addAttribute("generalOperationCost", analysisVal?.generalOperationCost ?: BigDecimal.ZERO)
        model.addAttribute("deflatorCoefficient", analysisVal?.deflatorCoefficient ?: BigDecimal.ZERO)
        model.addAttribute("id", analysisVal?.id)
        model.addAttribute("productName", product.techSpecName)
        model.addAttribute("periodId", periodId)
        model.addAttribute("planYear", period.pricePeriod?.startDate?.year)
        model.addAttribute("reportYear", period.prevPeriod?.pricePeriod?.startDate?.year)
        return "prod/include/product/detail/decipherment/analysis"
    }
}