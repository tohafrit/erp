package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.SCALE
import ru.korundm.constant.BaseConstant.ZERO_INT
import ru.korundm.constant.BaseConstant.ZERO_LONG
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.dto.letter.LetterAllotmentItemDto
import ru.korundm.entity.Allotment
import ru.korundm.entity.MatValue
import ru.korundm.entity.ProductionShipmentLetter
import ru.korundm.enumeration.ProductAcceptType
import ru.korundm.enumeration.SpecialTestType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.RowCountable
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.contractFullNumber
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import ru.korundm.util.KtCommonUtil.safetyReadListValue
import java.math.BigDecimal
import java.math.RoundingMode.HALF_DOWN
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import ru.korundm.form.ProductionShipmentLetterListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.PRODUCTION_SHIPMENT_LETTER])
class ProductionShipmentLetterActionProdController(
    private val jsonMapper: ObjectMapper,
    private val productionShipmentLetterService: ProductionShipmentLetterService,
    private val allotmentService: AllotmentService,
    private val contractSectionService: ContractSectionService,
    private val matValueService: MatValueService,
    private val baseService: BaseService
) {

    // Загрузка писем на производство
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        selectedId: Long?,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val number: String, // номер письма
            val createDate: LocalDate?, // дата создания
            val contractNumber: String?, // номер договора или доп. соглашения
            val sendToWarehouseDate: LocalDate?, // дата отправки на склад
            val sendToProductionDate: LocalDate?, // дата отправки в производство
            val comment: String? // комментарий
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, productionShipmentLetterService.findListTableData(input, selectedId, form)) { letter ->
            val sectionList = letter.matValueList.map { it.allotment?.lot?.lotGroup?.contractSection }.distinct()
            Item(
                letter.id,
                letter.fullNumber,
                letter.createDate,
                sectionList.map { it?.fullNumber }.joinToString(", "),
                letter.sendToWarehouseDate,
                letter.sendToProductionDate,
                letter.comment
            )
        }
    }

    // Загрузка распределений изделий по договорам для письма на производство
    @GetMapping("/list/distribution/load")
    fun listDistributionLoad(
        letterId: Long,
        request: HttpServletRequest
    ): List<*> {
        return contractSectionService.findTableData(letterId, LetterAllotmentItemDto::class).map {
            it.groupMain = "Договор № " + contractFullNumber(it.contractNumber, it.performer, it.type, it.year, it.number) + ", " + "Заказчик: " + it.name
            it.acceptTypeCode = ProductAcceptType.getById(it.acceptType).code
            it.specialTestTypeCode = it.specialTestType?.let { specType -> SpecialTestType.getById(specType).code } ?: SpecialTestType.WITHOUT_CHECKS.code
            it
        }
    }

    // Загрузка договоров
    @GetMapping("/list/edit/contract/load")
    fun listEditContractLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var contractNumber: Int = 0, // порядковый номер основного договора
            var type: Long = 0, // тип договора
            var performer: Long = 0, // исполнитель договора
            var createDate: LocalDate = LocalDate.MIN, // дата создания
            var year: Int = 0, // год создания
            var number: Int = 0, // номер секции договора
            var pzCopyDate: LocalDate? = null, // дата передачи в ПЗ
            var pzCopy: Boolean = false, // передано в ПЗ или нет
            var name: String = "", // заказчик
            var location: String? = null, // местонахождение заказчика
            var rowCount: Long = 0, // номер строки для пагинации
            //
            var fullNumber: String = "", // номер договора или доп. соглашения
            var customer: String = "" // Заказчик
        ) : RowCountable { override fun rowCount() = rowCount }

        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, contractSectionService.findTableData(input, form, Item::class)) {
            it.fullNumber = contractFullNumber(it.contractNumber, it.performer, it.type, it.year, it.number)
            it.customer = it.name + if (it.location != null) it.location else ""
            it
        }
    }

    // Загрузка изделий договора
    @GetMapping("/list/edit/contract/product/load")
    fun listEditContractProductLoad(
        model: ModelMap,
        filterData: String,
        contractSectionId: Long
    ): List<*> {
        data class Item(
            val id: Long?,
            val productName: String, // изделие
            val groupMain: String?, // название главной группы группировки столбцов
            val groupSubMain: String?, // название sub группы группировки столбцов
            val deliveryDate: LocalDate?, // дата поставки
            val allotmentAmount: Long, // количество изделий в части поставки
            val cost: BigDecimal, // стоимость
            val paid: BigDecimal, // оплачено (руб.)
            val percentPaid: BigDecimal, // оплачено (%)
            val finalPrice: BigDecimal?, // окончательная цена
            val launchNumber: String?, // запуск
            val launchAmount: Long, // количество запущенных изделий у allotment-a
            val letterNumber: String?, // письмо на производство
            val letterCreationDate: LocalDate?, // дата создания письма
            val shipmentPermitDate: LocalDate?, // дата разрешения на отгрузку
            val transferForWrappingDate: LocalDate?, // дата передачи на упаковку
            val readyForShipmentDate: LocalDate?, // дата готовности к отгрузке
            val shipmentDate: LocalDate?, // дата отгрузки
            val allotmentShippedAmount: Long, // количество отгруженных изделий в части поставки
            val comment: String?, // комментарий
            val isReceivedByMSN: Boolean, // получено по МСН или нет
            val cannotAddedLetter: Boolean, // возможность добавления allotment-та в письмо
            //
            val editGroupMain: String?, // название главной группы группировки столбцов для edit-таблицы
            val editGroupSubMain: String? // название sub группы группировки столбцов для edit-таблицы
        )
        val hundred = BigDecimal.valueOf(100)
        val tableItemOutList = mutableListOf<Item>()
        val form = jsonMapper.readDynamic(filterData)
        val section = contractSectionService.read(contractSectionId)
        val lotGroupList = section.lotGroupList
        val allotmentList = lotGroupList.flatMap { it.lotList }.flatMap { it.allotmentList }.toList()

        allotmentList.map { allotment ->
            val lotGroupAllotmentList = lotGroupList.filter { it.id == allotment.lot?.lotGroup?.id }.flatMap { it.lotList }
                .flatMap { it.allotmentList }.toList()

            // Общее кол-во изделий в группе
            val totalNumberProducts = lotGroupList.filter { it.id == allotment.lot?.lotGroup?.id }.flatMap { it.lotList }.sumOf { it.amount }

            // Общее кол-во запущенных изделий в группе
            val totalNumberProductsLaunched = lotGroupAllotmentList.filter { it.launchProduct != null }.sumOf { it.amount }

            // Общее кол-во отгруженных изделий в группе
            val totalNumberProductsShipped = lotGroupAllotmentList.filter { it.shipmentDate != null }.sumOf { it.amount }

            val lot = allotment.lot
            val shipmentDate = allotment.shipmentDate
            val lotGroup = lot?.lotGroup
            val orderIndex = lotGroup?.orderIndex
            val productConditionalName = lotGroup?.product?.conditionalName ?: ""
            val groupMain =
                "$productConditionalName. Пункт ведомости: $orderIndex. Количество изделий: $totalNumberProducts. Запущено: $totalNumberProductsLaunched. Отгружено: $totalNumberProductsShipped"
            val deliveryDate = lot?.deliveryDate
            val formattedDate = deliveryDate?.format(DATE_FORMATTER)
            val valueAddedTax = lot?.vat

            // Величина процентной ставки НДС
            val vat = valueAddedTax?.value?.toBigDecimal() ?: BigDecimal.ZERO

            // Коэффициент НДС
            val vatRatio = vat / hundred + BigDecimal.ONE
            var costWithVAT = vatRatio * allotment.amount.toBigDecimal() * allotment.neededPrice
            costWithVAT = costWithVAT.setScale(SCALE, HALF_DOWN)
            val paid = allotment.paid.setScale(SCALE, HALF_DOWN)

            // Получение процентного соотношения оплаченной суммы к стоимости allotment-a
            val percentPaid = if (costWithVAT.compareTo(BigDecimal.ZERO) != 0) {
                paid * hundred / costWithVAT.setScale(SCALE, HALF_UP)
            } else {
                BigDecimal.ZERO
            }

            val launchProduct = allotment.launchProduct
            val launchAmount = if (launchProduct != null) allotment.amount else ZERO_LONG
            val launchNumber = launchProduct?.launch?.numberInYear ?: ""

            // Общее количество изделий в lotGroup
            val totalAmountProducts = lot?.allotmentList?.sumOf { it.amount } ?: ZERO_LONG

            // Кол-во запущенных изделий в lotGroup
            val numberProductsLaunched = lot?.allotmentList?.filter { it.launchProduct != null }?.sumOf { it.amount } ?: ZERO_LONG

            // Кол-во отгруженных изделий в lotGroup
            val numberProductsShipped = lot?.allotmentList?.filter { it.shipmentDate != null }?.sumOf { it.amount } ?: ZERO_LONG

            val groupSubMain =
                "Дата поставки: $formattedDate. Количество изделий: $totalAmountProducts. Запущено: $numberProductsLaunched. Отгружено: $numberProductsShipped"

            var letterNumber: String? = ""
            var letterCreationDate: LocalDate? = null
            val matValueList = allotment.matValueList
            if (matValueList.isNotEmpty()) {
                letterNumber = matValueList.map { it.letter?.fullNumber }.firstOrNull()
                letterCreationDate = matValueList.map { it.letter?.createDate }.firstOrNull()
            }

            tableItemOutList += Item(
                allotment.id,
                productConditionalName,
                groupMain,
                groupSubMain,
                deliveryDate,
                allotment.amount,
                costWithVAT,
                paid,
                percentPaid,
                allotment.finalPrice,
                launchNumber,
                launchAmount,
                letterNumber,
                letterCreationDate,
                allotment.shipmentPermitDate?.toLocalDate(),
                allotment.transferForWrappingDate?.toLocalDate(),
                allotment.readyForShipmentDate?.toLocalDate(),
                shipmentDate?.toLocalDate(),
                shipmentDate?.let { allotment.amount } ?: ZERO_LONG,
                allotment.note,
                false, // TODO вернуться и переделать на правильную логику после реализации СКЛАДА
                allotment.matValueList.isEmpty(),
                "Договор № " + section.fullNumber,
                "Дата поставки: $formattedDate"
            )

            // Фильтр по тексту поля "Изделие"
            form.string(ObjAttr.PRODUCT_NAME)?.let {
                tableItemOutList.removeIf { item -> !item.productName.contains(it, ignoreCase = true) }
            }

            // Фильтр по дате поставки c
            form.date(ObjAttr.DELIVERY_DATE_FROM)?.let {
                tableItemOutList.removeIf { item -> item.deliveryDate?.isBefore(it) == true }
            }

            // Фильтр по дате поставки по
            form.date(ObjAttr.DELIVERY_DATE_TO)?.let {
                tableItemOutList.removeIf { item -> item.deliveryDate?.isAfter(it) == true }
            }
        }
        return tableItemOutList
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        val formId = form.id
        val allotmentIdFormList = jsonMapper.safetyReadListValue(form.allotmentIdList, Long::class)
        baseService.exec {
            val formAllotmentList = allotmentService.getAllById(allotmentIdFormList)
            //
            val isSendToClientDate = formAllotmentList.map { it.lot?.lotGroup?.contractSection }
                .any { section -> section?.sendToClientDate == null }
            if (isSendToClientDate) throw AlertUIException("Выбранный договор должен быть передан в ПЗ")
            if (formId == null && formAllotmentList.any { it.matValueList.isNotEmpty() }) throw AlertUIException("Среди выбранных изделий есть уже добавленные в письма на производство")
            if (response.isValid) {
                val letter = formId?.let { productionShipmentLetterService.read(it) ?: throw AlertUIException("Письмо не найдено") } ?: ProductionShipmentLetter()
                letter.apply {
                    // Находим последнее письмо на производство
                    val lastLetter = productionShipmentLetterService.findLastLetter()
                    var letterNumber = lastLetter?.number ?: ONE_INT
                    number = formId?.let { this.number } ?: lastLetter?.let { ++letterNumber } ?: letterNumber
                    createDate = form.createDate
                    year = form.createDate?.year ?: ZERO_INT
                    sendToProductionDate = form.sendToProductionDate
                    sendToWarehouseDate = form.sendToWarehouseDate
                    comment = form.comment.nullIfBlank()
                    productionShipmentLetterService.save(this)
                }
                if (formId == null) {
                    createRequiredMatValues(formAllotmentList, letter)
                    response.putAttribute(ObjAttr.ID, letter.id)
                } else {
                    // Список allotment-ов письма на производство
                    val letterAllotmentList = allotmentService.getAllByMatValueListLetterId(letter.id)

                    // Список allotment-ов для добавления в письмо
                    val addMatValueForAllotmentList = formAllotmentList.filterNot { letterAllotmentList.contains(it) }

                    // Список allotment-ов для удаления из письма
                    val deleteMatValueForAllotmentList = letterAllotmentList.filterNot { formAllotmentList.contains(it) }

                    if (addMatValueForAllotmentList.isNotEmpty()) {
                        if (addMatValueForAllotmentList.any { it.matValueList.isNotEmpty() }) throw AlertUIException("Среди выбранных изделий есть уже добавленные в письма на производство")
                        createRequiredMatValues(addMatValueForAllotmentList, letter)
                    }
                    if (deleteMatValueForAllotmentList.isNotEmpty()) {
                        val deleteMatValueList = deleteMatValueForAllotmentList.flatMap { it.matValueList }
                        matValueService.deleteAll(deleteMatValueList)
                    }
                }
            }
        }
        return response
    }

    // Метод создания необходимого количества материальных ценностей
    private fun createRequiredMatValues(allotmentList: List<Allotment>, letter: ProductionShipmentLetter) {
        allotmentList.forEach { item ->
            // Создаем столько раз matValue сколько у allotment-a в поле amount
            repeat(item.amount.toInt()) {
                val matValue = MatValue()
                matValue.letter = letter
                matValue.allotment = item
                matValueService.save(matValue)
            }
        }
    }

    @DeleteMapping("/list/delete/{id}")
    fun letterDelete(@PathVariable id: Long) = baseService.exec {
        productionShipmentLetterService.read(id)?.apply {
            if (this.matValueList.any { it.presentLogRecord != null }) throw AlertUIException("Одно или несколько изделий уже прошли ОТК. Удаление невозможно.")
            matValueService.deleteAll(this.matValueList)
            productionShipmentLetterService.delete(this)
        }
    }
}