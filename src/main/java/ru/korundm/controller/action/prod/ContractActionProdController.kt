package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.SCALE
import ru.korundm.constant.BaseConstant.ZERO_INT
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.view.ContractConstant.EXPORT_MANUFACTURING
import ru.korundm.constant.view.ContractConstant.INTERNAL_MANUFACTURING
import ru.korundm.constant.view.ContractConstant.INTERNAL_TYPE
import ru.korundm.constant.view.ContractConstant.MANUFACTURING
import ru.korundm.constant.view.ContractConstant.ORDER_MANUFACTURING
import ru.korundm.constant.view.ContractConstant.SCIENTIFIC_AND_TECHNICAL_MANUFACTURING
import ru.korundm.dao.*
import ru.korundm.dto.contract.LotGroupLotDTO
import ru.korundm.entity.*
import ru.korundm.enumeration.*
import ru.korundm.enumeration.CompanyTypeEnum.OAO_KORUND_M
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.FileStorageType
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.TabrOut.Companion.instance
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.FileStorageUtil.extractSingular
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import ru.korundm.util.KtCommonUtil.safetyReadListValue
import ru.korundm.util.KtCommonUtil.safetyReadMutableListValue
import java.math.BigDecimal
import java.math.RoundingMode.HALF_DOWN
import java.math.RoundingMode.HALF_UP
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.HttpServletRequest
import ru.korundm.form.ContractDeliveryStatementEditProductForm as DeliveryStatementEditProductForm
import ru.korundm.form.ContractEditDocumentationForm as EditDocumentationForm
import ru.korundm.form.ContractEditForm as ListEditForm
import ru.korundm.form.ContractEditInvoiceForm as EditInvoiceForm
import ru.korundm.form.ContractEditPaymentDistributionForm as DistributionForm
import ru.korundm.form.ContractEditPaymentForm as EditPaymentForm
import ru.korundm.form.ContractEditSectionForm as EditSectionForm
import ru.korundm.form.ContractInvoicesFilterForm as InvoicesFilterForm
import ru.korundm.form.ContractPaidProductsFilterForm as PaidProductsFilterForm

private const val INVOICES_FILTER_FORM_ATTR = "contractInvoicesFilterForm"
private const val PAID_PRODUCTS_FILTER_FORM_ATTR = "contractPaidProductsFilterForm"
@SessionAttributes(
    names = [
        INVOICES_FILTER_FORM_ATTR,
        PAID_PRODUCTS_FILTER_FORM_ATTR
    ],
    types = [
        InvoicesFilterForm::class,
        PaidProductsFilterForm::class
    ]
)
@ActionController([RequestPath.Action.Prod.CONTRACT])
class ContractActionProdController(
    private val jsonMapper: ObjectMapper,
    private val contractService: ContractService,
    private val contractSectionService: ContractSectionService,
    private val companyService: CompanyService,
    private val valueAddedTaxService: ValueAddedTaxService,
    private val productService: ProductService,
    private val lotGroupService: LotGroupService,
    private val lotService: LotService,
    private val allotmentService: AllotmentService,
    private val accountService: AccountService,
    private val invoiceService: InvoiceService,
    private val paymentService: PaymentService,
    private val baseService: BaseService,
    private val sectionDocumentationService: ContractSectionDocumentationService,
    private val fileStorageService: FileStorageService,
    private val launchService: LaunchService,
    private val launchProductService: LaunchProductService,
    private val serviceTypeService: ServiceTypeService,
    private val contractDocumentationParamsService: ContractDocumentationParamsService,
    private val companyTypeService: CompanyTypeService,
    private val objectMapper: ObjectMapper
) {

    // Загрузка договоров
    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val sectionId: Long?, // идентификатор секции
            val fullNumber: String, // полный номер договора
            val customer: String?, //заказчик
            val city: String? // город
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, contractService.findTableData(input, form)) { c ->
            val section = contractSectionService.getFirstSection(c)
            Item(
                c.id,
                section.id,
                c.fullNumber,
                c.customer?.name,
                c.customer?.location
            )
        }
    }

    // Загрузка информации о договоре и его доп. соглашений
    @GetMapping("/list/structure/load")
    fun listStructureLoad(
        contractId: Long,
        request: HttpServletRequest
    ): List<*> {
        data class Item(
            val sectionId: Long?,
            val contractId: Long?,
            val structure: String, // описание секции
            val sectionNumber: Int, // номер секции
            val isSectionNumberZero: Boolean, // true - договор, false - доп. соглашение
            val createDate: LocalDate?, // дата создания секции
            val status: Boolean, // статус секции
            val identifier: String?, // идентификатор
            val separateAccount: String?, // ОБС - отдельный банковский счет
            val manager: String?, // ведущий договор или доп. соглашение
            val sendToClientDate: LocalDate?, // дата передачи в ПЗ
            val comment: String? // комментарий
        )
        val contract = contractService.read(contractId)
        return contract.sectionList.map { Item(
            it.id,
            contractId,
            if (it.number == ZERO_INT) "Основной договор" else "Доп. соглашение № ${it.number}",
            it.number,
            it.number == ZERO_INT,
            it.createDate,
            it.archiveDate == null,
            it.identifier,
            it.separateAccount?.account,
            it.manager?.userOfficialName,
            it.sendToClientDate,
            it.comment
        ) }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(
        form: ListEditForm
    ): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId: Long? = form.id
            lateinit var contract: Contract
            baseService.exec {
                contract = formId?.let { contractService.read(it) ?: throw AlertUIException("Договор не найден") } ?: Contract()
                contract.apply {
                    val now = LocalDate.now()
                    // Находим последний договор
                    val lastContract = contractService.findLastContract()
                    var contractNumber = lastContract?.number ?: ONE_INT
                    number = formId?.let { this.number } ?: lastContract?.let { ++contractNumber } ?: contractNumber
                    type = if (form.customerTypeId == CustomerTypeEnum.INTERNAL.id) INTERNAL_TYPE else form.contractType
                    customer = form.customerId?.let { Company(it) }
                    comment = form.comment.nullIfBlank()
                    performer = Performer.OAOKORUND
                    contractService.save(this)
                    val sectionList = contract.sectionList
                    val section: ContractSection
                    if (sectionList.isNotEmpty()) {
                        section = contractSectionService.getFirstSection(contract)
                        section.createDate = form.createDate
                        section.year = form.createDate!!.year
                        section.externalNumber = form.externalNumber
                        section.sendToClientDate = form.sendToClientDate
                        section.archiveDate = if (form.archive) null else now
                    } else {
                        section = ContractSection()
                        section.contract = contract
                        section.createDate = form.createDate
                        section.number = ZERO_INT
                        section.year = form.createDate!!.year
                        section.archiveDate = if (form.archive) null else now
                    }
                    section.manager = form.manager
                    section.identifier = form.identifier
                    contractSectionService.save(section)
                }
            }
            if (formId == null) response.putAttribute(Contract::id.name, contract.id)
        }
        return response
    }

    // Удаление договора
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        contractService.read(id)?.apply {
            val countSection = contractSectionService.getCountAllByContract(this)
            val section = contractSectionService.getFirstSection(this)
            val notEmpty = section.lotGroupList.isNotEmpty()
            if (countSection == ONE_INT && notEmpty) throw AlertUIException("Ведомость поставки содержит изделия. Удаление невозможно.")
            if (countSection > ONE_INT) throw AlertUIException("Невозможно завершить удаление - договор имеет зависимые доп. соглашения")
            contractService.delete(this)
        }
    }

    // Удаление секции договора
    @DeleteMapping("/list/section-delete/{id}")
    fun listSectionDelete(@PathVariable id: Long) = baseService.exec {
        contractSectionService.read(id)?.apply {
            if (this.number == ZERO_INT) {
                val countSection = this.contract?.let { contractSectionService.getCountAllByContract(it) }
                if (countSection == ONE_INT) {
                    if (this.lotGroupList.isNotEmpty()) throw AlertUIException("Ведомость поставки содержит изделия. Удаление невозможно.")
                    contractService.delete(this.contract)
                } else {
                    throw AlertUIException("Невозможно завершить удаление - договор имеет зависимые доп. соглашения")
                }
            } else {
                if (this.lotGroupList.isNotEmpty()) throw AlertUIException("Ведомость поставки содержит изделия. Удаление невозможно")
                contractSectionService.delete(this)
            }
        }
    }

    // Загрузка заказчиков
    @GetMapping("/list/edit/customer/load")
    fun listEditCustomerLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterData: String,
        customerTypeId: Long
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val name: String, // название заказчика
            val address: String? // адрес
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, companyService.findTableData(customerTypeId, input, form)) { Item(
            it.id,
            it.name,
            it.juridicalAddress
        ) }
    }

    // Загрузка выбранного заказчика при добавлении или редактировании договора
    @GetMapping("/list/edit/load")
    fun listEditSelectedCustomerLoad(
        customerId: Long?
    ): List<*>? {
        data class Item(
            val id: Long?, // идентификатор
            val name: String, // название заказчика
            val address: String? // адрес
        )
        val itemList = mutableListOf<Item>()
        (customerId?.let { companyService.read(it) })?.apply { itemList += Item(id, name, juridicalAddress) }
        return itemList
    }

    @PostMapping("/detail/general/edit/save")
    fun detailGeneralEditSave(
        form: EditSectionForm
    ): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formSectionId = form.id
            val sectionDate = form.createDate
            val externalNumber = form.externalNumber
            val archive = form.archive
            val identifier = form.identifier
            val comment = form.comment
            val section = formSectionId?.let { contractSectionService.read(it) }
            val contract = section?.contract
            val sectionNumber = section?.number
            val isContractSectionNumberZero = sectionNumber == ZERO_INT
            val now = LocalDate.now()
            // Добавление доп. соглашения
            if (isContractSectionNumberZero) {
                val newContractSection = ContractSection()
                newContractSection.contract = contract
                form.sectionNumber?.let { newContractSection.number = it }
                newContractSection.createDate = form.createDate
                newContractSection.externalNumber = externalNumber
                newContractSection.archiveDate = if (archive) null else now
                newContractSection.identifier = identifier
                newContractSection.comment = comment
                newContractSection.manager = form.manager
                contractSectionService.save(newContractSection)
                response.putAttribute(ContractSection::id.name, newContractSection.id)
                // Редактирование доп. соглашения
            } else {
                section?.createDate = sectionDate
                section?.externalNumber = externalNumber
                section?.archiveDate = if (archive) null else now
                section?.identifier = identifier
                section?.comment = comment
                section?.manager = form.manager
                section?.sendToClientDate = form.sendToClientDate
                contractSectionService.save(section)
            }
        }
        return response
    }

    // Загрузка ведомости поставки
    @GetMapping("/detail/delivery-statement/load")
    fun detailDeliveryStatementLoad(
        model: ModelMap,
        filterData: String,
        sectionId: Long
    ): List<LotGroupLotDTO> {
        val form = jsonMapper.readDynamic(filterData)
        val contractSection = contractSectionService.read(sectionId)
        val itemList = mutableListOf<LotGroupLotDTO>()
        contractSection.lotGroupList.forEach { lotGroup ->
            val lotList = lotGroup.lotList
            for (lot in lotList) {
                val contractType = lotGroup.contractSection?.contract?.type
                val totalLotGroupProduct = lotList.sumOf { it.amount }
                val lotGroupCostList = lotList.map { it.lotGroupCost }
                val totalLotGroupCost = lotGroupCostList.fold(BigDecimal.ZERO) { total, next -> total + next }
                val totalLotGroupCostFormat = NumberFormat.getNumberInstance(Locale.FRANCE).format(totalLotGroupCost)
                // Общее кол-во запущенных изделий в группе (lotGroup-e)
                val totalLaunchAmount =
                    lotList.flatMap { it.allotmentList }.filter { it.launchProduct != null }.sumOf { it.amount }
                // Общее кол-во отгруженных изделий в группе (lotGroup-e)
                val totalShippedAmount =
                    lotList.flatMap { it.allotmentList }.filter { it.shipmentDate != null }.sumOf { it.amount }
                val allotmentList = lot.allotmentList
                // Кол-во запущенных изделий в поставке (lot-e)
                val launchAmount = allotmentList.filter { it.launchProduct != null }.sumOf { it.amount }
                // Кол-во отгруженных изделий в поставке (lot-e)
                val shippedAmount = allotmentList.filter { it.shipmentDate != null }.sumOf { it.amount }

                val item = LotGroupLotDTO()
                item.lotGroupId = lotGroup.id
                val product = lotGroup.product
                var conditionalName = product!!.conditionalName
                val serviceType = lotGroup.serviceType
                val serviceTypeId = serviceType?.id
                conditionalName =
                    if (serviceTypeId == MANUFACTURING ||
                        serviceTypeId == EXPORT_MANUFACTURING ||
                        serviceTypeId == SCIENTIFIC_AND_TECHNICAL_MANUFACTURING ||
                        serviceTypeId == INTERNAL_MANUFACTURING ||
                        serviceTypeId == ORDER_MANUFACTURING
                    ) {
                        conditionalName
                    } else {
                        serviceType?.prefix + " " + conditionalName
                    }
                item.groupMain = "$conditionalName. Общее количество: $totalLotGroupProduct. Запущено: $totalLaunchAmount. Отгружено: $totalShippedAmount.Общая стоимость: $totalLotGroupCostFormat"
                item.productId = product.id
                item.productName = conditionalName ?: ""
                item.lotId = lot.id
                item.amount = lot.amount
                item.deliveryDate = lot.deliveryDate
                item.price = lot.neededPrice
                item.cost = lot.lotGroupCost
                item.launchAmount = launchAmount
                item.shippedAmount = shippedAmount

                // TODO lot.getAcceptType() обязать заполнять поле Тип приемки и Спец. проверка
                val acceptType = lot.acceptType
                item.acceptType = acceptType?.code ?: ""
                item.acceptTypeId = acceptType?.id
                val specialTestType = lot.specialTestType
                item.specialTestType = specialTestType?.code ?: SpecialTestType.WITHOUT_CHECKS.code
                item.specialTestTypeId = specialTestType?.id
                val priceKind = lot.priceKind
                val neededPriceKind =
                    if (PriceKindType.FINAL == priceKind) "По протоколу " + lot.protocolNumber.toString() + lot.protocolDate?.let { " от " + it.format(DATE_FORMATTER) }
                    else if (ContractType.SUPPLY_OF_EXPORTED != contractType && PriceKindType.EXPORT == priceKind) "Фиксированная"
                    else if (ContractType.SUPPLY_OF_EXPORTED == contractType && PriceKindType.EXPORT == priceKind) "Экспортная"
                    else priceKind?.description
                item.priceKind = neededPriceKind
                itemList += item
            }
        }

        // Фильтр по тексту поля "условное наименование изделия"
        form.string(ObjAttr.PRODUCT_NAME)?.let {
            itemList.removeIf { item -> !item.productName.contains(it, ignoreCase = true) }
        }

        // Фильтр по дате поставки c
        form.date(ObjAttr.DELIVERY_DATE_FROM)?.let {
            itemList.removeIf { item -> item.deliveryDate?.isBefore(it) == true }
        }

        // Фильтр по дате поставки по
        form.date(ObjAttr.DELIVERY_DATE_TO)?.let {
            itemList.removeIf { item -> item.deliveryDate?.isAfter(it) == true }
        }

        return itemList
    }

    // Загрузка результирующей таблицы ведомости поставки
    @GetMapping("/detail/delivery-statement/result-load")
    fun detailDeliveryStatementResultLoad(contractSectionId: Long): List<*>? {
        data class Item(
            val total: BigDecimal, // итого
            val vat: BigDecimal, // НДС
            val totalWithVat: BigDecimal // итого с НДС
        )

        val contractSection = contractSectionService.read(contractSectionId)
        val itemList = mutableListOf<Item>()
        val totalLotGroupCostList = mutableListOf<BigDecimal>()
        val totalVATList = mutableListOf<BigDecimal>()
        val hundred = BigDecimal.valueOf(100)
        lotService.getLotListBySection(contractSection).forEach { lot ->
            // Определяем в какой период действия ставки НДС попадает дата поставки lot-a
            val valueAddedTax = lot.vat
            // Величина процентной ставки НДС
            val vat = valueAddedTax?.value?.toBigDecimal() ?: BigDecimal.ZERO
            val lotPriceAmountSum = lot.neededPrice * lot.amount.toBigDecimal()
            val totalVAT = lotPriceAmountSum * vat / hundred
            totalLotGroupCostList += lotPriceAmountSum
            totalVATList += totalVAT
        }
        val totalSum = totalLotGroupCostList.fold(BigDecimal.ZERO) { total, next -> total + next }
        val totalVATSum = totalVATList.fold(BigDecimal.ZERO) { total, next -> total + next }
        itemList += Item(
            totalSum,
            totalVATSum,
            totalSum.add(totalVATSum)
        )
        return itemList
    }

    @DeleteMapping("/detail/delivery-statement/delete/{id}")
    fun detailDeliveryStatementDelete(@PathVariable id: Long) {
        val lot = lotService.read(id)
        if (lot.allotmentList.all {
            it.launchProduct == null &&
            it.protocol == null &&
            it.matValueList.isEmpty()
        } ) {
            lotService.deleteById(id)
            lotService.deleteById(id)
        } else throw AlertUIException("Поставка связана с другими объектами системы. Удаление невозможно")
    }

    // Загрузка изделий
    @GetMapping("/detail/delivery-statement/products/load")
    fun detailDeliveryStatementProductsLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val productName: String, // условное наименование
            val decimalNumber: String?, // технические условия изделия
            val typeName: String, // краткая техническая характеристика
            val protocolNumber: String?, // протокол
            val comment: String? // комментарий
        )

        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, productService.findContractTableData(input, form)) { Item(
            it.id,
            it.conditionalName ?: "",
            it.decimalNumber,
            it.type?.name ?: "",
            if (it.productChargesProtocolList.isNotEmpty()) it.productChargesProtocolList.first().protocolNumber else "",
            it.comment
        ) }
    }

    @PostMapping("/detail/delivery-statement/edit/needed-price")
    fun detailDeliveryStatementAddProductEditNeededPrice(
        contractSectionId: Long,
        priceKind: PriceKindType,
        productId: Long,
        protocolId: Long?
    ): BigDecimal? {
        var neededPrice: BigDecimal? = BigDecimal.ZERO
        if (PriceKindType.EXPORT != priceKind) {
            val contractSection = contractSectionService.read(contractSectionId)
            val product = productService.read(productId)
            if (PriceKindType.PRELIMINARY == priceKind) {
                neededPrice = if (ContractType.SUPPLY_OF_EXPORTED == contractSection.contract?.type) {
                    product.exportPrice
                } else {
                    product.price
                }
            } else if (PriceKindType.FINAL == priceKind) {
                val protocol = product.productChargesProtocolList.firstOrNull { it.id == protocolId }
                neededPrice = protocol?.price ?: protocol?.priceUnpack ?: neededPrice
            }
        }
        return neededPrice
    }

    @PostMapping("/detail/delivery-statement/edit/save")
    fun detailDeliveryStatementEditSave(
        form: DeliveryStatementEditProductForm
    ): ValidatorResponse? {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val lotId = form.lotId
            if (lotId != null) {
                val lot = lotService.read(lotId)
                val lotAmount = lot.amount
                val formAmount = form.amount
                if (formAmount < lotAmount) {
                    if (allotmentService.getCountAllByLot(lot) == ONE_INT) {
                        val allotment = lot.allotmentList.first()
                        if (allotment.launchProduct == null &&
                            allotment.protocol == null &&
                            allotment.matValueList.isEmpty()
                        ) {
                            allotment.amount = allotment.amount - formAmount
                            allotmentService.save(allotment)
                            lot.amount = lotAmount - formAmount
                        } else throw AlertUIException("Распределение поставки связано с другими объектами. Уменьшение кол-ва экземпляров изделия/услуги невозможно")
                    } else throw AlertUIException("Распределение поставки содержит несколько частей. Уменьшение кол-ва экземпляров изделия/услуги невозможно")
                } else if (formAmount > lotAmount) {
                    val allotment = Allotment()
                    allotment.lot = lot
                    allotment.amount = formAmount - lotAmount
                    allotment.price = lot.price
                    allotment.priceKind = lot.priceKind
                    allotmentService.save(allotment)
                    lot.amount = formAmount
                }
                lot.deliveryDate = form.deliveryDate
                lot.price = form.price ?: BigDecimal.ZERO
                val priceKind = form.priceKind
                lot.priceKind = priceKind
                lot.acceptType = form.acceptType
                lot.specialTestType = form.specialTestType
                val protocolId = form.productChargesProtocol
                if (priceKind == PriceKindType.FINAL && protocolId == null) throw AlertUIException("Протокол цены отсутствует. Нельзя редактировать изделие ведомости поставки")
                protocolId?.let { lot.protocol = ProductChargesProtocol(it) }
                lotService.save(lot)
            } else {
                val sectionId = form.sectionId
                val productId = form.productId
                val serviceType = form.serviceTypeId?.let { serviceTypeService.read(it) }
                val section = sectionId?.let { contractSectionService.read(it) }
                val product = productId?.let { productService.read(it) }
                val lotGroupList = section?.lotGroupList
                val productList = lotGroupList?.map { it.product }
                val lotGroup = lotGroupList?.firstOrNull { it.product == product && it.serviceType == serviceType }
                lotGroupList?.let { lotGroup?.let { newLotSave(form, lotGroup, false) } }
                productList?.let {
                    if (!productList.contains(product) || lotGroup == null) {
                        val group = LotGroup()
                        group.contractSection = section
                        group.product = product
                        group.serviceType = serviceType
                        lotGroupService.save(group)
                        val newLotId = newLotSave(form, group, true)
                        response.putAttribute("addedLotId", newLotId)
                    }
                }
            }
        }
        return response
    }

    // Метод добавления и сохранения lot-a (изделия) в lotGroup ведомости поставки, а так же добавления allotment-та
    private fun newLotSave(
        form: DeliveryStatementEditProductForm,
        lotGroup: LotGroup,
        isDeleteLotGroup: Boolean
    ): Long? {
        val lot = Lot()
        lot.lotGroup = lotGroup
        lot.amount = form.amount
        lot.deliveryDate = form.deliveryDate
        lot.price = form.price ?: BigDecimal.ZERO
        val priceKind = form.priceKind
        lot.priceKind = priceKind
        lot.acceptType = form.acceptType
        lot.specialTestType = form.specialTestType
        lot.vat = valueAddedTaxService.getDateFromLast()
        val protocolId = form.productChargesProtocol
        if (isDeleteLotGroup && priceKind == PriceKindType.FINAL && protocolId == null) {
            lotGroupService.delete(lotGroup)
            throw AlertUIException("Протокол цены отсутствует. Нельзя добавить изделие в ведомость поставки")
        }
        protocolId?.let { lot.protocol = ProductChargesProtocol(it) }
        lotService.save(lot)
        val allotment = Allotment()
        allotment.lot = lot
        allotment.amount = lot.amount
        allotment.price = lot.price
        allotment.priceKind = lot.priceKind
        allotment.paid = BigDecimal.ZERO
        lot.protocol?.let { allotment.protocol = it }
        allotmentService.save(allotment)
        return lot.id
    }

    // Загрузка распределения поставки
    @GetMapping("/detail/delivery-statement/distribution-load")
    fun detailProductsDistributionLoad(
        lotId: Long
    ): List<*> {
        data class Item(
            val id: Long?,
            val allotmentAmount: Long, // количество изделий в части поставки
            val paid: BigDecimal, // оплачено (руб.)
            val percentPaid: BigDecimal, //  оплачено (%)
            val finalPrice: BigDecimal?, // окончательная цена
            val launchNumber: String, // запуск
            val letterNumber: String?, // письмо на производство
            val letterCreationDate: LocalDate?, // дата создания письма
            val shipmentPermitDate: LocalDateTime?, // дата разрешения на отгрузку
            val transferForWrappingDate: LocalDateTime?, // дата передачи на упаковку
            val readyForShipmentDate: LocalDateTime?, // дата готовности к отгрузке
            val shipmentDate: LocalDateTime?, // дата отгрузки
            val shipped: String?, // отгружено
            val allotmentShippedAmount: Long, // количество отгруженных изделий в части поставки
            val neededPrice: BigDecimal, // цена
            val priceKind: String, // вид цены
            val comment: String?, // комментарий
            val canAdd: Boolean, // возможность добавления изделий в запуск
            val canEdit: Boolean // возможность изменения запуска для изделий
        )

        val lot = lotService.read(lotId)
        val allotmentList = lot.allotmentList
        val oneHundred = BigDecimal.valueOf(100)
        return allotmentList.map {
            val launchProduct = it.launchProduct
            val shipmentDate = it.shipmentDate
            val contractType = lot.lotGroup?.contractSection?.contract?.type
            val priceKind = it.priceKind
            val valueAddedTax = it.lot?.vat

            val shipped = if (shipmentDate != null) {
                shipmentDate.format(DATE_FORMATTER)
            } else {
                val matValueList = it.matValueList
                if (matValueList.isNotEmpty()) {
                    if (matValueList.all { mv -> mv.permitForShipmentDate != null }) {
                        val permitForShipmentDate = matValueList.first().permitForShipmentDate
                        if (matValueList.all { mv -> mv.permitForShipmentDate == permitForShipmentDate }) {
                            permitForShipmentDate?.format(DATE_FORMATTER)
                        } else {
                            "Не отгружено"
                        }
                    } else {
                        "Не отгружено"
                    }
                } else {
                    "Не отгружено"
                }
            }

            // Величина процентной ставки НДС
            val vat = valueAddedTax?.value?.toBigDecimal() ?: BigDecimal.ZERO
            // Коэффициент НДС
            val vatRatio = vat / oneHundred + BigDecimal.ONE
            var costWithVAT = vatRatio * it.amount.toBigDecimal() * it.neededPrice
            costWithVAT = costWithVAT.setScale(SCALE, HALF_DOWN)

            val paid: BigDecimal = it.paid.setScale(SCALE, HALF_DOWN)

            val neededPriceKind = if (PriceKindType.FINAL == priceKind) {
                "По протоколу ${lot.protocolNumber}${if (lot.protocolDate != null) " от ${lot.protocolDate?.format(DATE_FORMATTER)}" else ""}"
            } else if (ContractType.SUPPLY_OF_EXPORTED != contractType && PriceKindType.EXPORT == priceKind) {
                "Фиксированная"
            } else if (ContractType.SUPPLY_OF_EXPORTED == contractType && PriceKindType.EXPORT == priceKind) {
                "Экспортная"
            } else {
                priceKind?.description ?: ""
            }

            Item(
                it.id,
                it.amount,
                paid,
                if (costWithVAT.compareTo(BigDecimal.ZERO) != 0) {
                    paid * oneHundred / costWithVAT.setScale(SCALE, HALF_UP)
                } else {
                    BigDecimal.ZERO
                },
                it.finalPrice,
                launchProduct?.launch?.numberInYear ?: "",
                if (it.matValueList.isNotEmpty()) it.matValueList[0].letter?.fullNumber else "",
                if (it.matValueList.isNotEmpty()) it.matValueList[0].letter?.createDate else null,
                it.shipmentPermitDate,
                it.transferForWrappingDate,
                it.readyForShipmentDate,
                shipmentDate,
                shipped,
                if (shipmentDate != null) it.amount else 0L,
                it.neededPrice,
                neededPriceKind,
                it.note,
                it.launchProduct == null,
                it.launchProduct != null
            )
        }
    }

    // Загрузка платежей
    @GetMapping("/detail/payments/load")
    fun detailPaymentsLoad(
        model: ModelMap,
        filterData: String,
        sectionId: Long
    ): List<*> {
        data class Item(
            val id: Long?,
            val paymentNumber: String, // номер платежного поручения
            val paymentDate: LocalDate?, // дата платежа
            val paymentAmount: BigDecimal, // сумма платежа
            val invoiceNumber: String, // номер счета
            val payerName: String?, // плательщик
            val accountNumber: String?, // расчетный счет
            val advanceInvoiceNumber: String?, // номер счет-фактуры на аванс
            val advanceInvoiceDate: LocalDate?, // дата счет-фактуры на аванс
            val comment: String? // комментарий
        )

        val form = jsonMapper.readDynamic(filterData)
        val section = contractSectionService.read(sectionId)
        val itemList = mutableListOf<Item>()
        section.paymentList.forEach {
            val invoice = it.invoice
            val year = invoice?.createdDate?.format(DateTimeFormatter.ofPattern("yy"))
            itemList += Item(
                it.id,
                it.number ?: "",
                it.date,
                it.amount,
                "${invoice?.number.toString()}/$year",
                invoice?.contractSection?.contract?.customer?.shortName,
                it.account?.account,
                it.advanceInvoiceNumber,
                it.advanceInvoiceDate,
                it.note
            )
        }

        // Фильтр по номеру платежного поручения
        form.string(ObjAttr.PAYMENT_NUMBER)?.let {
            itemList.removeIf { item -> !item.paymentNumber.contains(it, ignoreCase = true) }
        }

        // Фильтр по номеру счета
        form.string(ObjAttr.INVOICE_NUMBER)?.let {
            itemList.removeIf { item -> !item.invoiceNumber.contains(it, ignoreCase = true) }
        }

        // Фильтр по дате платежа c
        form.date(ObjAttr.PAYMENT_DATE_FROM)?.let {
            itemList.removeIf { item -> item.paymentDate?.isBefore(it) == true }
        }

        // Фильтр по дате платежа по
        form.date(ObjAttr.PAYMENT_DATE_TO)?.let {
            itemList.removeIf { item -> item.paymentDate?.isAfter(it) == true }
        }

        return itemList
    }

    // Загрузка результирующей таблицы платежей
    @GetMapping("/detail/payments/result-load")
    fun detailPaymentsPaymentsResultLoad(
        sectionId: Long
    ): List<*> {
        data class Item(
            val allCostWithVAT: BigDecimal, // стоимость запущенных изделий
            val sumPaymentAmount: BigDecimal, // оплачено
            val outAllCostWithVAT: BigDecimal, // задолженность по запущенным изделиям
            val outNotDistributed: BigDecimal // не распределено по изделиям
        )
        val section = contractSectionService.read(sectionId)
        val itemList = mutableListOf<Item>()
        val paymentList = section.paymentList
        val paymentAmountList = paymentList.map { it.amount }
        val sumPaymentAmount = paymentAmountList.fold(BigDecimal.ZERO) { total, next -> total + next }
        val allCostWithVATList = mutableListOf<BigDecimal>()
        val oneHundred = BigDecimal.valueOf(100)

        lotService.getLotListBySection(section).forEach {
            // Определяем в какой период действия ставки НДС попадает дата поставки lot-a
            val valueAddedTax = it.vat
            // величина процентной ставки НДС
            val amount = valueAddedTax?.value?.toBigDecimal() ?: BigDecimal.ZERO

            it.allotmentList.filter { allotment -> allotment.launchProduct != null }.forEach { allotment ->
                if (allotment.launchProduct != null) {
                    val allotmentPrice = allotment.neededPrice
                    val allotmentAmount = allotment.amount.toBigDecimal() // кол-во запущенных изделий в allotment-e
                    val vatRatio = (amount / oneHundred) + BigDecimal.ONE
                    val allotmentCostWithVAT = allotmentPrice * allotmentAmount * vatRatio // стоимость запущенного изделия с НДС
                    allCostWithVATList += allotmentCostWithVAT
                }
            }
        }

        // стоимость запущенных изделий
        val allCostWithVAT = if (allCostWithVATList.isNotEmpty()) allCostWithVATList.fold(BigDecimal.ZERO) { total, next -> total + next } else BigDecimal.ZERO

        val amountPaymentList = paymentList.map { it.amount }

        // Сумма платежей
        val allPaymentsSum = amountPaymentList.fold(BigDecimal.ZERO) { total, next -> total + next }

        val allAllotmentSectionList = section.lotGroupList.flatMap { it.lotList }.flatMap { it.allotmentList }
        val allAllotmentsPaidSumList = allAllotmentSectionList.map { it.paid }
        // Использованная (распределенная) сумма
        val allAllotmentPaidSum = allAllotmentsPaidSumList.fold(BigDecimal.ZERO) { total, next -> total + next }

        // Нераспределенная сумма
        val unallocatedSum = allPaymentsSum - allAllotmentPaidSum

        itemList += Item(
            allCostWithVAT,
            sumPaymentAmount,
            allCostWithVAT.subtract(sumPaymentAmount),
            unallocatedSum
        )

        return itemList
    }

    @PostMapping("/detail/payments/edit/save")
    fun detailPaymentsEditSave(
        form: EditPaymentForm
    ): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId: Long? = form.id
            lateinit var payment: Payment
            baseService.exec {
                payment = formId?.let { paymentService.read(it) ?: throw AlertUIException("Платеж не найден") } ?: Payment()
                payment.apply {
                    number = form.number
                    invoice = form.invoice
                    date = form.date
                    account = form.accountPayerId?.let { id -> accountService.read(id) }
                    amount = form.amount ?: BigDecimal.ZERO
                    note = form.note
                    advanceInvoiceNumber = form.advanceInvoice
                    contractSection = form.sectionId?.let { contractSectionService.read(it) }
                    paymentService.save(this)
                }
            }
        }
        return response
    }

    // Загрузка счетов
    @GetMapping("/detail/invoices/load")
    fun detailInvoicesLoad(
        model: ModelMap,
        filterForm: String,
        contractSectionId: Long
    ): List<*> {
        data class Item(
            val id: Long?,
            val invoiceNumber: Long?, // номер счета
            val date: LocalDate?, // дата счета
            val dateValidBefore: LocalDate?, // счет действителен до
            val productionFinishDate: LocalDate?, // срок изготовления изделий
            val amount: BigDecimal, // сумма
            val paidAmount: BigDecimal?, // сумма, на которую оплачен счет (оплачено)
            val description: String?, // описание
            val statusName: String?, // статус (наименование)
            val status: InvoiceStatus?, // статус
            val account: String?, // расчетный счет
            val bankName: String?, // название банка
            val canStatusActing: Boolean, // возможность аннулировать счет или закрыть
            val canStatusClosedOrCanceled: Boolean // возможность сделать счет действующим
        )

        val form = jsonMapper.readValue(filterForm, InvoicesFilterForm::class.java)
        model.addAttribute(INVOICES_FILTER_FORM_ATTR, form)
        val section = contractSectionService.read(contractSectionId)
        val itemList = mutableListOf<Item>()
        section.invoiceList.forEach {
            val status = it.status
            val account = it.account
            itemList += Item(
                it.id,
                it.number,
                it.createdDate,
                it.dateValidBefore,
                it.productionFinishDate,
                it.price,
                it.paid ?: BigDecimal.ZERO,
                it.type?.description,
                status?.description,
                status,
                account?.account,
                account?.bank?.name,
                it.status == InvoiceStatus.ACTING,
                it.status == InvoiceStatus.CLOSED || it.status == InvoiceStatus.CANCELED
            )
        }

        // Фильтр по номеру счета
        val invoiceNumber = form.invoiceNumber
        itemList.removeIf { invoiceNumber != null && it.invoiceNumber != invoiceNumber }

        // Фильтр по статусу счета
        val invoiceStatusList = form.invoiceStatusList
        itemList.removeIf { invoiceStatusList.isNotEmpty() && !invoiceStatusList.contains(it.status?.id) }

        // Фильтр по дате счета c
        val invoiceDateFrom = form.invoiceDateFrom
        invoiceDateFrom?.let { itemList.removeIf { it.date?.isBefore(invoiceDateFrom) == true } }

        // Фильтр по дате счета по
        val invoiceDateTo = form.invoiceDateTo
        invoiceDateTo?.let { itemList.removeIf { it.date?.isAfter(invoiceDateTo) == true } }

        return itemList
    }

    // Загрузка расчетных счетов
    @GetMapping("/detail/invoices/add/accounts-load")
    fun detailInvoicesAddAccountsLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val account: String?, // расчетный счет
            val bankName: String?, // наименование банка
            val comment: String? // комментарий
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, accountService.findTableData(input, form)) { Item(
            it.id,
            it.account,
            it.bank?.name,
            it.note
        ) }
    }

    // Загрузка выбранного расчетного счета при создании счета
    @GetMapping("/detail/invoices/add/selected-account-load")
    fun detailInvoicesAddSelectedAccountLoad(
        accountId: Long?
    ): List<*> {
        data class Item(
            val id: Long?, // идентификатор
            val account: String?, // расчетный счет
            val bankName: String?, // наименование банка
            val comment: String? // комментарий
        )
        val tableItemOutList = mutableListOf<Item>()
        accountId?.let {
            val account = accountService.read(it)
            tableItemOutList += Item(
                account.id,
                account.account,
                account.bank?.name,
                account.note
            )
        }
        return tableItemOutList
    }

    // Загрузка оплачиваемых изделий
    @GetMapping("/detail/invoices/add/products-load")
    fun detailInvoicesAddPaidProductsLoad(
        model: ModelMap,
        filterForm: String,
        sectionId: Long
    ): List<*> {
        data class Item(
            val id: Long?, // идентификатор
            val productConditionalName: String?, // условное наименование изделия
            val groupMain: String, // название главной группы группировки столбцов
            val groupSubMain: String, // название sub группы группировки столбцов
            val deliveryDate: LocalDate?, // дата поставки
            val allotmentAmount: Long, // количество изделий в части поставки
            val cost: BigDecimal, // стоимость
            val paid: BigDecimal, // оплачено (руб.)
            val percentPaid: BigDecimal, // оплачено (%)
            val finalPrice: BigDecimal?, // окончательная цена
            val launchNumber: String, // запуск
            val launchAmount: Long?, // количество запущенных изделий у allotment-a
            val letterNumber: String?, // письмо на производство
            val letterCreationDate: LocalDate?, // дата создания письма
            val shipmentPermitDate: LocalDateTime?, // дата разрешения на отгрузку
            val transferForWrappingDate: LocalDateTime?, // дата передачи на упаковку
            val readyForShipmentDate: LocalDateTime?, // дата готовности к отгрузке
            val shipmentDate: LocalDateTime?, // дата отгрузки
            val allotmentShippedAmount: Long, // количество отгруженных изделий в части поставки
            val comment: String?, // комментарий
            val cannotAddedInvoice: Boolean, // возможность добавления allotment-та в invoice (счет)
            val isReceivedByMSN: Boolean = false //  получено по МСН или нет
        )

        val form = jsonMapper.readValue(filterForm, PaidProductsFilterForm::class.java)
        val section = contractSectionService.read(sectionId)

        val lotGroupList = section.lotGroupList
        val allotmentList = lotGroupList.flatMap { it.lotList }.flatMap { it.allotmentList }
        val oneHundred = BigDecimal.valueOf(100)
        val itemList = mutableListOf<Item>()
        allotmentList.forEach { allotment ->
            val filterLotList = lotGroupList.filter { it == allotment.lot?.lotGroup }.flatMap { it.lotList }
            val lotGroupAllotmentList = filterLotList.flatMap { it.allotmentList }

            // Общее кол-во изделий в группе
            val totalNumberProducts = filterLotList.sumOf { it.amount }

            // Общее кол-во запущенных изделий в группе
            val totalNumberProductsLaunched =
                lotGroupAllotmentList.filter { it.launchProduct != null }.sumOf { it.amount }

            // Общее кол-во отгруженных изделий в группе
            val totalNumberProductsShipped =
                lotGroupAllotmentList.filter { it.shipmentDate != null }.sumOf { it.amount }

            val lot = allotment.lot
            val launchProduct = allotment.launchProduct
            val productConditionalName = lot?.lotGroup?.product?.conditionalName
            val groupMain = "$productConditionalName. Количество изделий: $totalNumberProducts. Запущено: $totalNumberProductsLaunched. Отгружено: $totalNumberProductsShipped"
            val deliveryDate = lot?.deliveryDate
            val formattedDate = deliveryDate?.format(DateTimeFormatter.ofPattern(BaseConstant.DATE_PATTERN))
            val shipmentDate = allotment.shipmentDate
            val price = allotment.lot?.price!!
            val amount = allotment.amount
            val allotmentPaid = allotment.paid
            val valueAddedTax = lot?.vat
            // Величина процентной ставки НДС
            val vat = valueAddedTax?.value?.toBigDecimal() ?: BigDecimal.ZERO

            // Коэффициент НДС
            val vatRatio = vat / oneHundred + BigDecimal.ONE

            var costWithVAT = vatRatio * allotment.amount.toBigDecimal() * allotment.neededPrice
            costWithVAT = costWithVAT.setScale(SCALE, HALF_DOWN)
            val paid: BigDecimal = allotmentPaid.setScale(SCALE, HALF_DOWN)

            val lotAllotmentList = lot?.allotmentList ?: emptyList()
            // Общее количество изделий в lotGroup
            val totalAmountProducts = lotAllotmentList.sumOf { it.amount }

            // Кол-во запущенных изделий в lotGroup
            val numberProductsLaunched = lotAllotmentList.filter { it.launchProduct != null }.sumOf { it.amount }

            // Кол-во отгруженных изделий в lotGroup
            val numberProductsShipped = lotAllotmentList.filter { it.shipmentDate != null }.sumOf { it.amount }

            val groupSubMain = "Дата поставки: $formattedDate. Количество изделий: $totalAmountProducts. Запущено: $numberProductsLaunched. Отгружено: $numberProductsShipped"

            itemList += Item(
                allotment.id,
                productConditionalName,
                groupMain,
                groupSubMain,
                deliveryDate,
                allotment.amount,
                costWithVAT,
                paid,
                if (costWithVAT.compareTo(BigDecimal.ZERO) != 0) {
                    paid * oneHundred / costWithVAT.setScale(SCALE, HALF_UP)
                } else {
                    BigDecimal.ZERO
                },
                allotment.finalPrice,
                launchProduct?.launch?.numberInYear ?: "",
                launchProduct?.contractAmount?.toLong(),
                if (allotment.matValueList.isNotEmpty()) allotment.matValueList[0].letter?.fullNumber else "",
                if (allotment.matValueList.isNotEmpty()) allotment.matValueList[0].letter?.createDate else null,
                allotment.shipmentPermitDate,
                allotment.transferForWrappingDate,
                allotment.readyForShipmentDate,
                shipmentDate,
                if (shipmentDate != null) allotment.amount else 0L,
                allotment.note,
                price * amount.toBigDecimal() != allotmentPaid
            )
        }

        // Фильтр по тексту поля "условное наименование изделия"
        val conditionalName = form.conditionalName
        itemList.removeIf {
            conditionalName.isNotBlank() &&
                !if (it.productConditionalName != null) it.productConditionalName.contains(
                    conditionalName,
                    ignoreCase = true
                ) else false
        }

        // Фильтр по дате поставки c
        val deliveryDateFrom = form.deliveryDateFrom
        deliveryDateFrom?.let { itemList.removeIf { it.deliveryDate?.isBefore(deliveryDateFrom) == true } }

        // Фильтр по дате поставки по
        val deliveryDateTo = form.deliveryDateTo
        deliveryDateTo?.let { itemList.removeIf { it.deliveryDate?.isAfter(deliveryDateTo) == true } }

        return itemList
    }

    @PostMapping("/detail/invoices/add/save")
    fun detailInvoicesAddSave(
        form: EditInvoiceForm
    ): ValidatorResponse {
        val response = ValidatorResponse(form)
        val allotmentIdFormList = jsonMapper.safetyReadListValue(form.allotmentIdList, Long::class)
        if (response.isValid) {
            baseService.exec {
                val formAllotmentList = allotmentService.getAllById(allotmentIdFormList)
                val invoiceType = form.invoiceType
                val percentAdvance = form.percentAdvance
                val forAmountInvoice = form.forAmountInvoice

                val isAdvanceInvoiceType = invoiceType?.let { type -> InvoiceType.getById(type) } == InvoiceType.ADVANCE
                val isForAmountInvoiceType = invoiceType?.let { type -> InvoiceType.getById(type) } == InvoiceType.INVOICE_FOR_AMOUNT
                val isFinalInvoiceType = invoiceType?.let { type -> InvoiceType.getById(type) } == InvoiceType.FINAL_INVOICE

                val isAddPaymentFinalInvoiceType = formAllotmentList.all {
                    it.priceKind == PriceKindType.FINAL ||
                    it.priceKind == PriceKindType.EXPORT ||
                    it.priceKind == PriceKindType.STATEMENT
                }

                val oneHundred = BigDecimal.valueOf(100)
                val allCostWithVATList = mutableListOf<BigDecimal>()
                val allVATList = mutableListOf<BigDecimal>()
                formAllotmentList.forEach { allotment ->
                    val price = allotment.lot?.price!!
                    val amount = allotment.amount
                    val vat = price* allotment.lot?.vat?.value!!.toBigDecimal() / oneHundred
                    val advancePrice = percentAdvance?.let {  (price + vat) * amount.toBigDecimal() * percentAdvance.toBigDecimal() / oneHundred } ?: BigDecimal.ZERO
                    val advanceVAT = percentAdvance?.let {  vat * amount.toBigDecimal() * percentAdvance.toBigDecimal() / oneHundred } ?: BigDecimal.ZERO
                    val finalPrice = (price + vat) * amount.toBigDecimal() - allotment.paid
                    val finalPriceWOVAT = price * amount.toBigDecimal() - allotment.paid
                    if (isAdvanceInvoiceType) {
                        if (advancePrice > finalPrice) throw AlertUIException("При указанных параметрах счета и с учетом " +
                            "ранее поступивших платежей величина аванса превышает стоимость выбранных изделий поставки. Добавление счета невозможно")
                        allCostWithVATList += advancePrice
                        allVATList += advanceVAT
                    } else if (isForAmountInvoiceType) {
                        val allFinalPrice = formAllotmentList.map { (it.lot?.price!! + (it.lot?.price!! * it.lot?.vat?.value!!.toBigDecimal())) * it.amount.toBigDecimal() - it.paid }
                            .fold(BigDecimal.ZERO) { total, next -> total + next }
                        if (forAmountInvoice != null && forAmountInvoice > allFinalPrice && !form.invoiceForAmountDialog) {
                            response.putAttribute(ObjAttr.INVOICE_FOR_AMOUNT_DIALOG, true)
                        }
                        if (allCostWithVATList.size == 0) {
                            allCostWithVATList += forAmountInvoice!!
                            allVATList += forAmountInvoice!! * valueAddedTaxService.findByDateFrom(LocalDate.now())?.value!!.toBigDecimal() / oneHundred
                        }
                    } else if (isFinalInvoiceType) {
                        if (isAddPaymentFinalInvoiceType) {
                            allCostWithVATList += finalPrice
                            allVATList += finalPrice - finalPriceWOVAT
                        } else {
                            throw AlertUIException("Для некоторых изделий указана предварительная цена. Добавление счета на окончательную оплату невозможно")
                        }
                    }
                }

                if (isAdvanceInvoiceType || isForAmountInvoiceType || (isFinalInvoiceType || form.invoiceForAmountDialog)) {
                    val invoice = Invoice()
                    invoice.number = form.invoiceNumber
                    invoice.createdDate = form.invoiceDate
                    invoice.dateValidBefore = form.goodThroughDate
                    invoice.productionFinishDate = form.productionDate
                    invoice.status = InvoiceStatus.ACTING
                    invoice.type = form.invoiceType?.let { InvoiceType.getById(it) }
                    invoice.contractSection = form.sectionId?.let { contractSectionService.read(it) }
                    invoice.account = form.accountId?.let { accountService.read(it) }
                    invoice.allotmentList = formAllotmentList
                    invoice.price = allCostWithVATList.fold(BigDecimal.ZERO) { total, next -> total + next }
                    invoiceService.save(invoice)
                }
            }
        }
        return response
    }

    // Загрузка расчетных счетов при редактировании счета
    @GetMapping("/detail/invoices/edit/accounts-load")
    fun detailInvoicesEditAccountsLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val account: String?, // расчетный счет
            val bankName: String?, // наименование банка
            val comment: String? // комментарий
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, accountService.findTableData(input, form)) { Item(
            it.id,
            it.account,
            it.bank?.name,
            it.note
        ) }
    }

    // Загрузка выбранного расчетного счета при редактировании счета
    @GetMapping("/detail/invoices/edit/selected-account-load")
    fun detailInvoicesEditSelectedAccountLoad(
        accountId: Long?
    ): List<*> {
        data class Item(
            val id: Long?, // идентификатор
            val account: String?, // расчетный счет
            val bankName: String?, // наименование банка
            val comment: String? // комментарий
        )
        val tableItemOutList = mutableListOf<Item>()
        accountId?.let {
            val account = accountService.read(it)
            tableItemOutList += Item(
                account.id,
                account.account,
                account.bank?.name,
                account.note
            )
        }
        return tableItemOutList
    }

    // Аннулирование счёта
    @PostMapping("/detail/invoices/canceled-invoice")
    fun detailInvoicesCanceledInvoice(id: Long) = baseService.exec {
        val invoice = invoiceService.read(id) ?: throw AlertUIException("Счёт не найден")
        val paymentList = invoice.id?.let { paymentService.findByInvoiceId(it) }
        if (paymentList != null && paymentList.isNotEmpty()) throw AlertUIException("По данному счету был совершен платеж. Аннулирование невозможно")
        invoice.status = InvoiceStatus.CANCELED
        invoiceService.save(invoice)
    }

    // Закрытие счёта
    @PostMapping("/detail/invoices/closed-invoice")
    fun detailInvoicesClosedInvoice(id: Long) = baseService.exec {
        val invoice = invoiceService.read(id) ?: throw AlertUIException("Счёт не найден")
        invoice.status = InvoiceStatus.CLOSED
        invoiceService.save(invoice)
    }

    // Сделать счёт действующим
    @PostMapping("/detail/invoices/acting-invoice")
    fun detailInvoicesActingInvoice(id: Long) = baseService.exec {
        val invoice = invoiceService.read(id) ?: throw AlertUIException("Счёт не найден")
        invoice.status = InvoiceStatus.ACTING
        invoiceService.save(invoice)
    }

    // Удаление счета
    @DeleteMapping("/detail/invoice/delete/{id}")
    fun detailInvoiceDelete(@PathVariable id: Long) = baseService.exec {
        val invoice = invoiceService.read(id) ?: throw AlertUIException("Счёт не найден")
        if (invoice.status != InvoiceStatus.CANCELED) throw AlertUIException("Удаление возможно только для аннулированных счетов")
        invoiceService.deleteById(id)
    }

    // Загрузка выбранного расчетного счета при создании платежа
    @GetMapping("/detail/payments/edit/selected-account-load")
    fun detailPaymentsEditSelectedAccountLoad(
        accountPayerId: Long?
    ): List<*> {
        data class Item(
            val id: Long?, // идентификатор
            val account: String?, // расчетный счет
            val bankName: String?, // наименование банка
            val comment: String? // комментарий
        )
        val tableItemOutList = mutableListOf<Item>()
        accountPayerId?.let {
            val account = accountService.read(it)
            tableItemOutList += Item(
                account.id,
                account.account,
                account.bank?.name,
                account.note
            )
        }
        return tableItemOutList
    }

    // Загрузка расчетных счетов в платежах
    @GetMapping("/detail/payments/edit/accounts-load")
    fun detailPaymentsEditAccountsLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val account: String?, // расчетный счет
            val bankName: String?, // наименование банка
            val comment: String? // комментарий
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return instance(input, accountService.findTableData(input, form)) { Item(
            it.id,
            it.account,
            it.bank?.name,
            it.note
        ) }
    }

    // Распределение платежей ("Рассчитать")
    @GetMapping("/detail/payments/distribute/calculate")
    fun detailIPaymentsDistributeCalculate(
        sectionId: Long,
        unallocatedAmount: BigDecimal,
        allocatedAmount: BigDecimal,
        algorithmTypeId: Long,
        paymentId: Long?,
        paidProductsDataJson: String
    ): List<*> {
        if (unallocatedAmount < allocatedAmount) throw AlertUIException("Распределяемая сумма не может быть больше нераспределенной")
        val paidProductsTableData = jsonMapper.safetyReadMutableListValue(paidProductsDataJson, DistributionForm.PaidProductsTableData::class)
        val oneHundred = BigDecimal.valueOf(100)
        val seventy = BigDecimal.valueOf(70)

        val formAllCostSum = paidProductsTableData.map { it.cost?.toBigDecimal()?.setScale(SCALE, HALF_UP) ?: BigDecimal.ZERO }.fold(BigDecimal.ZERO) { total, next -> total + next }

        var allocatedResidue = allocatedAmount
        var unallocatedResidue = unallocatedAmount
        for (it in paidProductsTableData) {
            when (val algorithmType = DistributionAlgorithmType.getById(algorithmTypeId)) {
                DistributionAlgorithmType.PROPORTIONALLY -> {
                    val cost = it.cost?.toBigDecimal()?.setScale(SCALE, HALF_UP) ?: BigDecimal.ZERO
                    val percentage = (cost * oneHundred / formAllCostSum).setScale(SCALE, HALF_UP)
                    val calcPaid = it.paid?.toBigDecimal()?.setScale(SCALE, HALF_UP) ?: BigDecimal.ZERO
                    val residue = if (paidProductsTableData.size == ONE_INT) (cost - calcPaid).setScale(SCALE, HALF_UP) else (percentage * allocatedAmount / oneHundred).setScale(SCALE, HALF_UP)
                    if (paidProductsTableData.size == ONE_INT) {
                        if (cost != calcPaid) {
                            if (residue >= allocatedResidue) {
                                if (residue <= unallocatedResidue) {
                                    val paid = calcPaid + allocatedResidue
                                    val percentPaid = if (cost.compareTo(BigDecimal.ZERO) != 0) paid * oneHundred / cost.setScale(SCALE, HALF_UP) else BigDecimal.ZERO
                                    it.paid = paid.toString()
                                    it.percentPaid = percentPaid.toString()
                                    if (paymentId == null) allocatedResidue -= allocatedResidue
                                    unallocatedResidue -= allocatedResidue
                                } else {
                                    if (paymentId == null) {
                                        if (unallocatedResidue >= allocatedResidue) {
                                            val paid = calcPaid + allocatedResidue
                                            val percentPaid = if (cost.compareTo(BigDecimal.ZERO) != 0) paid * oneHundred / cost.setScale(SCALE, HALF_UP) else BigDecimal.ZERO
                                            it.paid = paid.toString()
                                            it.percentPaid = percentPaid.toString()
                                            unallocatedResidue -= allocatedResidue
                                            allocatedResidue -= allocatedResidue
                                        }
                                    } else {
                                        if (unallocatedResidue >= allocatedResidue) {
                                            val paid = calcPaid + allocatedResidue
                                            val percentPaid = if (cost.compareTo(BigDecimal.ZERO) != 0) paid * oneHundred / cost.setScale(SCALE, HALF_UP) else BigDecimal.ZERO
                                            it.paid = paid.toString()
                                            it.percentPaid = percentPaid.toString()
                                            unallocatedResidue -= allocatedResidue
                                        }
                                    }
                                }
                            } else {
                                if (residue > BigDecimal.ZERO) {
                                    val paid = calcPaid + residue
                                    val percentPaid = if (cost.compareTo(BigDecimal.ZERO) != 0) paid * oneHundred / cost.setScale(SCALE, HALF_UP) else BigDecimal.ZERO
                                    it.paid = paid.toString()
                                    it.percentPaid = percentPaid.toString()
                                    if (paymentId == null) allocatedResidue -= residue
                                    unallocatedResidue -= residue
                                }
                            }
                            break
                        }
                    } else {
                        if (cost != calcPaid) {
                            if (unallocatedResidue >= allocatedResidue) {
                                val paid = calcPaid + residue
                                val percentPaid = if (cost.compareTo(BigDecimal.ZERO) != 0) paid * oneHundred / cost.setScale(SCALE, HALF_UP) else BigDecimal.ZERO
                                it.paid = paid.toString()
                                it.percentPaid = percentPaid.toString()
                                if (paymentId == null) allocatedResidue -= residue
                                unallocatedResidue -= residue
                            }
                        }
                    }
                }
                DistributionAlgorithmType.ADD70, DistributionAlgorithmType.ADD100 -> {
                    val calcPaid = it.paid?.toBigDecimal() ?: BigDecimal.ZERO
                    val cost = it.cost?.toBigDecimal() ?: BigDecimal.ZERO
                    val calcCost = if (algorithmType == DistributionAlgorithmType.ADD70) cost * seventy / oneHundred else cost
                    val residue = calcCost - calcPaid
                    if (residue >= allocatedResidue) {
                        if (residue <= unallocatedResidue) {
                            val paid = calcPaid + allocatedResidue
                            it.paid = paid.toString()
                            it.percentPaid = (paid * oneHundred / cost.setScale(SCALE, HALF_UP)).toString()
                            if (paymentId == null) allocatedResidue -= allocatedResidue
                            unallocatedResidue -= allocatedResidue
                        } else {
                            if (paymentId == null) {
                                if (unallocatedResidue >= allocatedResidue) {
                                    val paid = calcPaid + allocatedResidue
                                    it.paid = paid.toString()
                                    it.percentPaid = (paid * oneHundred / cost.setScale(SCALE, HALF_UP)).toString()
                                    unallocatedResidue -= allocatedResidue
                                    allocatedResidue -= allocatedResidue
                                }
                            } else {
                                if (unallocatedResidue >= allocatedResidue) {
                                    val paid = calcPaid + allocatedResidue
                                    it.paid = paid.toString()
                                    it.percentPaid = (paid * oneHundred / cost.setScale(SCALE, HALF_UP)).toString()
                                    unallocatedResidue -= allocatedResidue
                                }
                            }
                        }
                        break
                    } else {
                        if (unallocatedResidue >= allocatedResidue) {
                            val paid = calcPaid + residue
                            it.paid = paid.toString()
                            it.percentPaid = (paid * oneHundred / cost.setScale(SCALE, HALF_UP)).toString()
                            if (paymentId == null) allocatedResidue -= residue
                            unallocatedResidue -= residue
                        }
                    }
                }
            }
        }
        paidProductsTableData.forEach {
            it.unallocatedAmount = unallocatedResidue.toString()
            it.allocatedAmount = allocatedResidue.toString()

        }
        return paidProductsTableData
    }

    // Распределение платежей ("Обнулить")
    @GetMapping("/detail/payments/distribute/Nullify")
    fun detailIPaymentsDistributeNullify(
        sectionId: Long,
        unallocatedAmount: BigDecimal,
        allocatedAmount: BigDecimal,
        paymentId: Long?,
        paidProductsDataJson: String
    ): List<*> {
        val paidProductsTableData = jsonMapper.safetyReadMutableListValue(paidProductsDataJson, DistributionForm.PaidProductsTableData::class)
        var allocatedResidue = allocatedAmount
        var unallocatedResidue = unallocatedAmount
        paidProductsTableData.forEach {
            val paid = it.paid?.toBigDecimal()?.setScale(SCALE, HALF_UP) ?: BigDecimal.ZERO
            unallocatedResidue += paid
            if (paymentId == null) allocatedResidue += paid
            it.paid = BigDecimal.ZERO.toString()
            it.percentPaid = BigDecimal.ZERO.toString()
        }
        paymentId?.let { allocatedResidue = paymentService.read(it).amount }
        paidProductsTableData.forEach {
            it.unallocatedAmount = unallocatedResidue.toString()
            it.allocatedAmount = allocatedResidue.toString()
        }
        return paidProductsTableData
    }

    // Сохранение распределения платежей
    @PostMapping("/detail/payments/distribute/save")
    fun detailPaymentsDistributeSave(form: DistributionForm): ValidatorResponse {
        val response = ValidatorResponse()
        val paidProductsTableData = jsonMapper.safetyReadMutableListValue(form.paidProductsTableData, DistributionForm.PaidProductsTableData::class)
        baseService.exec {
            paidProductsTableData.forEach {
                val allotment: Allotment = it.id?.let { id -> allotmentService.read(id) } ?: throw AlertUIException("Часть поставки не найдена")
                allotment.apply {
                    paid = it.paid?.toBigDecimal()?.setScale(SCALE, HALF_UP) ?: this.paid
                    allotmentService.save(this)
                }
            }
        }
        return response
    }

    // Удаление платежа
    @DeleteMapping("/detail/payment/delete/{id}")
    fun detailPaymentDelete(@PathVariable id: Long) = paymentService.deleteById(id)

    // Загрузка списка загруженной документации
    @GetMapping("/detail/documentation/load")
    fun detailDocumentationLoad(
        request: HttpServletRequest,
        sectionId: Long
    ): TabrOut<*> {
        data class Item(
            var id: Long?,
            var fileHash: String?,
            var name: String,
            var comment: String?
        )
        val input = TabrIn(request)
        val tableData = sectionDocumentationService.findTableData(input, sectionId)
        val fileList = fileStorageService.readAny(tableData.data, FileStorageType.ContractSectionDocumentationFile)
        return instance(input, tableData) { Item(
            it.id,
            fileList.extractSingular(it, FileStorageType.ContractSectionDocumentationFile)?.urlHash,
            it.name,
            it.comment
        ) }
    }

    // Загрузка списка формируемой документации
    @GetMapping("/detail/documentation-formed/load")
    fun detailDocumentationFormedLoad(
        request: HttpServletRequest,
        sectionId: Long
    ): TabrOut<*> {
        data class Item(
            var id: Long?,
            var name: String?
        )
        val input = TabrIn(request)
        val tableData = contractDocumentationParamsService.findTableData(input, sectionId)
        return instance(input, tableData) { Item(it.id, it.name) }
    }

    @GetMapping("/detail/documentation-formed/with-invoice-info/load")
    fun detailDocumentationFormedWithInvoiceInfoLoad(
        request: HttpServletRequest,
        model: ModelMap,
        sectionId: Long
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val number: Long?, // условное наименование
            val createdDate: LocalDate?, // дата создания
            val dateValidBefore: LocalDate?, // действителен дог
            val amount: BigDecimal?, // стоимость
            val paidAmount: BigDecimal? // оплачено
        )
        val input = TabrIn(request)
        val tableData = invoiceService.findTableData(input, sectionId)
        return instance(input, tableData) { Item(
            it.id,
            it.number,
            it.createdDate,
            it.dateValidBefore,
            it.price,
            it.paid
        ) }
    }

    @PostMapping("/detail/documentation-formed/with-invoice-info/save")
    fun detailDocumentationFormedWithInvoiceInfoSave(
        sectionId: Long,
        invoiceId: Long,
        documentType: Long
    ) {
        val map = mutableMapOf(ObjAttr.INVOICE_ID to invoiceId.toString(), ObjAttr.TYPE to documentType.toString())
        documentationFormedSave(map, sectionId, DocumentationType.getById(documentType))
    }

    @GetMapping("/detail/documentation-formed/with-payment-info/load")
    fun detailDocumentationFormedWithPaymentInfoLoad(
        request: HttpServletRequest,
        model: ModelMap,
        sectionId: Long
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val number: String?, // номер п/п
            val date: String?, // дата
            val amount: BigDecimal? // стоимость
        )
        val input = TabrIn(request)
        val tableData = paymentService.findTableData(input, sectionId)
        return instance(input, tableData) { Item(
            it.id,
            it.number,
            it.date?.format(DATE_FORMATTER),
            it.amount
        ) }
    }

    @PostMapping("/detail/documentation-formed/with-payment-info/save")
    fun detailDocumentationFormedWithPaymentInfoSave(
        sectionId: Long,
        paymentId: Long,
        documentType: Long
    ) {
        val map = mutableMapOf(ObjAttr.PAYMENT_ID to paymentId.toString(), ObjAttr.TYPE to documentType.toString())
        documentationFormedSave(map, sectionId, DocumentationType.getById(documentType))
    }

    @GetMapping("/detail/documentation-formed/with-account-info/load")
    fun detailDocumentationFormedWithAccountInfoLoad(
        request: HttpServletRequest,
        model: ModelMap,
        sectionId: Long,
        isPerformer: Boolean
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val account: String?, // расчетный счет
            val bank: String?, // банк
            val comment: String?, // комментарий
        )
        val input = TabrIn(request)
        val company =
            if (isPerformer) companyTypeService.getFirstByCompanyType(OAO_KORUND_M).company
            else contractSectionService.read(sectionId).contract?.customer

        val tableData = accountService.findTableDataByCompany(input, company?.id)
        return instance(input, tableData) { Item(
            it.id,
            it.account,
            it.bank?.name,
            it.note
        ) }
    }

    @PostMapping("/detail/documentation-formed/with-account-info/save")
    fun detailDocumentationFormedWithAccountInfoSave(
        sectionId: Long,
        performerAccount: Long,
        customerAccount: Long,
        documentType: Long
    ) {
        val map = mutableMapOf(
            ObjAttr.PERFORMER_ACCOUNT to performerAccount.toString(),
            ObjAttr.CUSTOMER_ACCOUNT to customerAccount.toString(),
            ObjAttr.TYPE to documentType.toString()
        )
        documentationFormedSave(map, sectionId, DocumentationType.getById(documentType))
    }

    @GetMapping("/detail/documentation-formed/with-allotment-info/load")
    fun detailDocumentationFormedWithAllotmentInfoLoad(
        request: HttpServletRequest,
        model: ModelMap,
        sectionId: Long
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val name: String?, // условное наименование изделия
            val allotmentAmount: Long, // количество изделий в части поставки
            val shipmentDate: LocalDateTime?, // дата отгрузки
            val paid: BigDecimal // оплачено
        )
        val input = TabrIn(request)
        val tableData = allotmentService.findTableDataByContractSection(input, sectionId)
        return instance(input, tableData) { Item(
            it.id,
            it.lot?.lotGroup?.product?.conditionalName,
            it.amount,
            it.shipmentDate,
            it.paid
        ) }
    }

    @PostMapping("/detail/documentation-formed/with-allotment-info/save")
    fun detailDocumentationFormedWithAllotmentInfoSave(
        sectionId: Long,
        @RequestParam paymentIdList: List<Long>,
        @RequestParam allotmentIdList: List<Long>,
        documentType: Long
    ) {
        val map = mutableMapOf(
            ObjAttr.PAYMENT_ID_LIST to objectMapper.writeValueAsString(paymentIdList),
            ObjAttr.ALLOTMENT_ID_LIST to objectMapper.writeValueAsString(allotmentIdList),
            ObjAttr.TYPE to documentType.toString()
        )
        if (DocumentationType.getById(documentType) == DocumentationType.DISPOSITION) {
            val now = LocalDateTime.now()
            val allotList = allotmentService.getAllById(allotmentIdList).filter { it.shipmentPermitDate == null }
            allotList.forEach { it.shipmentPermitDate = now }
            allotmentService.saveAll(allotList)
        }
        documentationFormedSave(map, sectionId, DocumentationType.getById(documentType))
    }

    @PostMapping("/detail/documentation-formed/save")
    fun detailDocumentationFormedSave(
        sectionId: Long,
        documentType: Long
    ) {
        val map = mutableMapOf(ObjAttr.TYPE to documentType.toString())
        documentationFormedSave(map, sectionId, DocumentationType.getById(documentType))
    }

    private fun documentationFormedSave(
        map: Map<String, String>,
        sectionId: Long,
        documentType: DocumentationType
    ) {
        val params = objectMapper.writeValueAsString(map)
        val document = ContractDocumentationParam()
        document.name = "${documentType.property} ${LocalDate.now().format(DATE_FORMATTER)}"
        document.contractSection = contractSectionService.read(sectionId)
        document.params = params
        contractDocumentationParamsService.save(document)
    }

    @PostMapping("/detail/documentation/edit/save")
    fun detailDocumentationEditSave(
        form: EditDocumentationForm
    ): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId: Long? = form.id
            lateinit var doc: ContractSectionDocumentation
            baseService.exec {
                doc = formId?.let { sectionDocumentationService.read(formId) } ?: ContractSectionDocumentation()
                doc.apply {
                    name = form.name?.trim() ?: ""
                    comment = form.comment
                    section = ContractSection(form.sectionId)
                    sectionDocumentationService.save(doc)
                    if (form.file?.isEmpty == false) {
                        fileStorageService.saveEntityFile(
                            doc,
                            FileStorageType.ContractSectionDocumentationFile,
                            form.file
                        )
                    }
                }
            }
            if (formId == null) response.putAttribute(ObjAttr.ID, doc.id)
        }
        return response
    }

    // Удаление документации
    @DeleteMapping("/detail/documentation/delete/{id}")
    fun detailDocumentationDelete(@PathVariable id: Long) = sectionDocumentationService.deleteById(id)

    // Удаление сгеннерированной документации
    @DeleteMapping("/detail/documentation-formed/delete/{id}")
    fun detailDocumentationFormedDelete(@PathVariable id: Long) = contractDocumentationParamsService.deleteById(id)

    // Загрузка неутвержденных запусков
    @GetMapping("/detail/delivery-statement/distribution/edit-products-launch-load")
    fun detailDeliveryStatementDistributionAddToLaunchLoad(
        allotmentId: Long
    ): List<*> {
        data class Item(
            val id: Long?, // идентификатор
            val numberInYear: String, // номер запуска в году
            val comment: String?, // комментарий
        )
        val tableItemOutList = mutableListOf<Item>()
        val unapprovedLaunchList = launchService.findAllByApprovalDateIsNull()
        unapprovedLaunchList.map { tableItemOutList += Item(
            it.id,
            it.numberInYear,
            it.comment
        ) }
        return tableItemOutList
    }

    @PostMapping("/detail/delivery-statement/distribution/edit-products-launch/save")
    fun detailDeliveryStatementDistributionAddToLaunchSave(
        launchId: Long,
        allotmentId: Long,
        isAllotmentLaunch: Boolean
    ) = baseService.exec {
        val launch = launchService.read(launchId) ?: throw AlertUIException("Запуск не найден")
        val allotment = allotmentService.read(allotmentId)
        val product = allotment.lot?.lotGroup?.product
        if (allotment.matValueList.isEmpty()) {
            if (isAllotmentLaunch) {
                val lp = allotment.launchProduct
                if (launch == lp?.launch) throw AlertUIException("Изделия уже добавлены в выбранный запуск")
                if (lp != null) {
                    lp.contractAmount = lp.contractAmount - allotment.amount.toInt()
                    allotmentAddToLaunchSave(product, allotment, launch)
                }
            } else {
                allotmentAddToLaunchSave(product, allotment, launch)
            }
        } else throw AlertUIException("Изделия добавлены в письмо на производство ${allotment.matValueList.firstOrNull()?.letter?.fullNumber}. Редактирование запуска невозможно")
    }

    // Метод добавления и сохранения allotment-a в запуск
    private fun allotmentAddToLaunchSave(
        product: Product?,
        allotment: Allotment,
        launch: Launch
    ) {
        val lpList = launch.launchProductList
        if (lpList.any { it.product == product }) {
            val launchProduct = lpList.firstOrNull { it.product == product }
            if (launchProduct != null) {
                launchProduct.contractAmount = launchProduct.contractAmount + allotment.amount.toInt()
                allotment.launchProduct = launchProduct
            }
        } else {
            val lp = launchProductService.getOrAddByLaunchIdAndProductId(launch.id, product?.id)
            lp?.contractAmount = allotment.amount.toInt()
            allotment.launchProduct = lp
        }
    }

    @PostMapping("/detail/delivery-statement/distribution/edit-products-launch/delete")
    fun detailDeliveryStatementDistributionAddToLaunchDelete(id: Long) = baseService.exec {
        val allotment = allotmentService.read(id)
        val lp = allotment.launchProduct
        if (allotment.matValueList.isEmpty() && lp != null) {
            lp.contractAmount = lp.contractAmount - allotment.amount.toInt()
            allotment.launchProduct = null
        } else throw AlertUIException("Изделия добавлены в письмо на производство ${allotment.matValueList[0].letter?.fullNumber}. Удаление из запуска невозможно")
    }

    @PostMapping("/detail/delivery-statement/add/needed-prefix")
    fun detailDeliveryStatementAddNeededPrefix(typeId: Long) = serviceTypeService.read(typeId).prefix ?: "Отсутствует"

    @PostMapping("/detail/delivery-statement/distribution/split/change-quantity")
    fun detailDeliveryStatementDistributionSplitChangeQuantity(allotmentId: Long, quantity: Long) = allotmentService.read(allotmentId).amount - quantity

    @PostMapping("/detail/delivery-statement/distribution/split/save")
    fun detailDeliveryStatementDistributionSplitSave(
        allotmentId: Long,
        quantityOne: Long,
        quantityTwo: Long
    ) {
        val allotment = allotmentService.read(allotmentId)
        baseService.exec {
            if (allotment.matValueList.isNotEmpty()) throw AlertUIException("Изделия части поставки добавлены в письмо на производство. Разделение невозможно")
            allotment.amount = quantityOne
            allotmentService.save(allotment)
            //
            val newAllotment = Allotment()
            newAllotment.lot = allotment.lot
            newAllotment.launchProduct = allotment.launchProduct
            newAllotment.amount = quantityTwo
            newAllotment.price = allotment.price
            newAllotment.priceKind = allotment.priceKind
            newAllotment.protocol = allotment.protocol
            newAllotment.paid = allotment.paid
            newAllotment.shipmentDate = allotment.shipmentDate
            newAllotment.finalPrice = allotment.finalPrice
            newAllotment.shipmentPermitDate = allotment.shipmentPermitDate
            newAllotment.intendedShipmentDate = allotment.intendedShipmentDate
            newAllotment.requestId = allotment.requestId
            newAllotment.transferForWrappingDate = allotment.transferForWrappingDate
            newAllotment.readyForShipmentDate = allotment.readyForShipmentDate
            newAllotment.shipment = allotment.shipment
            newAllotment.advancedStudyDate = allotment.advancedStudyDate
            allotmentService.save(newAllotment)
        }
    }

    @PostMapping("/detail/delivery-statement/distribution/unite")
    fun detailDeliveryStatementDistributionUnite(
        @RequestParam allotmentIdList: List<Long>
    ) {
        if (allotmentIdList.isEmpty() || allotmentIdList.size == ONE_INT) throw AlertUIException("Выберите несколько частей поставки")
        baseService.exec {
            val allotmentList = allotmentService.getAllById(allotmentIdList)
            if (allotmentList.any { it.matValueList.isNotEmpty() }) throw AlertUIException("Среди выбранных изделий есть уже добавленные в письма на производство. Объединение невозможно")
            val firstAllotment = allotmentList[0]
            val isIdenticalAttributes = allotmentList.all {
                it.lot == firstAllotment.lot && it.launchProduct == firstAllotment.launchProduct &&
                it.price == firstAllotment.price && it.priceKind == firstAllotment.priceKind &&
                it.protocol == firstAllotment.protocol && it.paid == firstAllotment.paid &&
                it.shipmentDate == firstAllotment.shipmentDate
                && it.finalPrice == firstAllotment.finalPrice && it.shipmentPermitDate == firstAllotment.shipmentPermitDate &&
                it.intendedShipmentDate == firstAllotment.intendedShipmentDate && it.requestId == firstAllotment.requestId &&
                it.transferForWrappingDate == firstAllotment.transferForWrappingDate &&
                it.readyForShipmentDate == firstAllotment.readyForShipmentDate && it.shipment == firstAllotment.shipment &&
                it.advancedStudyDate == firstAllotment.advancedStudyDate
            }
            if (!isIdenticalAttributes) throw AlertUIException("Части распределения несовместимы. Объединение невозможно")
            val deleteAllotmentList = allotmentList.filter { it != firstAllotment }
            val sumAmount = deleteAllotmentList.sumOf { it.amount }
            firstAllotment.amount = firstAllotment.amount + sumAmount
            allotmentService.save(firstAllotment)
            allotmentService.deleteAll(deleteAllotmentList)
        }
    }

    // Загрузка оплачиваемых изделий в распределении платежей
    @GetMapping("/detail/payment/edit/products-load")
    fun detailPaymentEditProductsLoad(
        model: ModelMap,
        filterForm: String,
        sectionId: Long,
        paymentId: Long?
    ): List<*> {
        data class Item(
            val id: Long?, // идентификатор
            val productConditionalName: String?, // условное наименование изделия
            val groupMain: String, // название главной группы группировки столбцов
            val groupSubMain: String, // название sub группы группировки столбцов
            val deliveryDate: LocalDate?, // дата поставки
            val allotmentAmount: Long, // количество изделий в части поставки
            val cost: BigDecimal, // стоимость
            val paid: BigDecimal, // оплачено (руб.)
            val percentPaid: BigDecimal, // оплачено (%)
            val finalPrice: BigDecimal?, // окончательная цена
            val launchNumber: String, // запуск
            val launchAmount: Long?, // количество запущенных изделий у allotment-a
            val letterNumber: String?, // письмо на производство
            val letterCreationDate: LocalDate?, // дата создания письма
            val shipmentPermitDate: LocalDateTime?, // дата разрешения на отгрузку
            val transferForWrappingDate: LocalDateTime?, // дата передачи на упаковку
            val readyForShipmentDate: LocalDateTime?, // дата готовности к отгрузке
            val shipmentDate: LocalDateTime?, // дата отгрузки
            val allotmentShippedAmount: Long, // количество отгруженных изделий в части поставки
            val comment: String?, // комментарий
            val unallocatedAmount: BigDecimal, // нераспределенная сумма
            val allocatedAmount: BigDecimal, // распределяемая сумма
            val isReceivedByMSN: Boolean = false //  получено по МСН или нет
        )

        val form = jsonMapper.readValue(filterForm, PaidProductsFilterForm::class.java)
        val section = contractSectionService.read(sectionId) ?: throw AlertUIException("Секция договора не найдена")

        val paymentAmountList = section.paymentList.map { it.amount }

        // Сумма платежей
        val allPaymentsSum = paymentAmountList.fold(BigDecimal.ZERO) { total, next -> total + next }

        val allAllotmentSectionList = section.lotGroupList.flatMap { it.lotList }.flatMap { it.allotmentList }
        val allAllotmentsPaidSumList = allAllotmentSectionList.map { it.paid }
        // Использованная (распределенная) сумма
        val allAllotmentPaidSum = allAllotmentsPaidSumList.fold(BigDecimal.ZERO) { total, next -> total + next }

        // Нераспределенная сумма
        val unallocatedSum = allPaymentsSum - allAllotmentPaidSum
        // Распределяемая сумма
        val allocatedAmount = if (paymentId != null) paymentService.read(paymentId).amount else unallocatedSum

        val lotGroupList = section.lotGroupList
        val allotmentList = lotGroupList.flatMap { it.lotList }.flatMap { it.allotmentList }
        val oneHundred = BigDecimal.valueOf(100)
        val itemList = mutableListOf<Item>()
        allotmentList.forEach { allotment ->
            val filterLotList = lotGroupList.filter { it == allotment.lot?.lotGroup }.flatMap { it.lotList }
            val lotGroupAllotmentList = filterLotList.flatMap { it.allotmentList }

            // Общее кол-во изделий в группе
            val totalNumberProducts = filterLotList.sumOf { it.amount }

            // Общее кол-во запущенных изделий в группе
            val totalNumberProductsLaunched =
                lotGroupAllotmentList.filter { it.launchProduct != null }.sumOf { it.amount }

            // Общее кол-во отгруженных изделий в группе
            val totalNumberProductsShipped =
                lotGroupAllotmentList.filter { it.shipmentDate != null }.sumOf { it.amount }

            val lot = allotment.lot
            val launchProduct = allotment.launchProduct
            val productConditionalName = lot?.lotGroup?.product?.conditionalName
            val groupMain = "$productConditionalName. Количество изделий: $totalNumberProducts. Запущено: $totalNumberProductsLaunched. Отгружено: $totalNumberProductsShipped"
            val deliveryDate = lot?.deliveryDate
            val formattedDate = deliveryDate?.format(DateTimeFormatter.ofPattern(BaseConstant.DATE_PATTERN))
            val shipmentDate = allotment.shipmentDate
            val allotmentPaid = allotment.paid
            val valueAddedTax = lot?.vat
            // Величина процентной ставки НДС
            val vat = valueAddedTax?.value?.toBigDecimal() ?: BigDecimal.ZERO

            // Коэффициент НДС
            val vatRatio = vat / oneHundred + BigDecimal.ONE

            var costWithVAT = vatRatio * allotment.amount.toBigDecimal() * allotment.neededPrice
            costWithVAT = costWithVAT.setScale(SCALE, HALF_DOWN)
            val paid: BigDecimal = allotmentPaid.setScale(SCALE, HALF_DOWN)

            val lotAllotmentList = lot?.allotmentList ?: emptyList()
            // Общее количество изделий в lotGroup
            val totalAmountProducts = lotAllotmentList.sumOf { it.amount }

            // Кол-во запущенных изделий в lotGroup
            val numberProductsLaunched = lotAllotmentList.filter { it.launchProduct != null }.sumOf { it.amount }

            // Кол-во отгруженных изделий в lotGroup
            val numberProductsShipped = lotAllotmentList.filter { it.shipmentDate != null }.sumOf { it.amount }

            val groupSubMain = "Дата поставки: $formattedDate. Количество изделий: $totalAmountProducts. Запущено: $numberProductsLaunched. Отгружено: $numberProductsShipped"

            itemList += Item(
                allotment.id,
                productConditionalName,
                groupMain,
                groupSubMain,
                deliveryDate,
                allotment.amount,
                costWithVAT,
                paid,
                if (costWithVAT.compareTo(BigDecimal.ZERO) != 0) {
                    paid * oneHundred / costWithVAT.setScale(SCALE, HALF_UP)
                } else {
                    BigDecimal.ZERO
                },
                allotment.finalPrice,
                launchProduct?.launch?.numberInYear ?: "",
                launchProduct?.contractAmount?.toLong(),
                allotment.matValueList.firstOrNull()?.letter?.fullNumber ?: "",
                allotment.matValueList.firstOrNull()?.letter?.createDate,
                allotment.shipmentPermitDate,
                allotment.transferForWrappingDate,
                allotment.readyForShipmentDate,
                shipmentDate,
                if (shipmentDate != null) allotment.amount else 0L,
                allotment.note,
                unallocatedSum,
                allocatedAmount
            )
        }

        // Фильтр по тексту поля "условное наименование изделия"
        val conditionalName = form.conditionalName
        itemList.removeIf {
            conditionalName.isNotBlank() &&
                !if (it.productConditionalName != null) it.productConditionalName.contains(
                    conditionalName,
                    ignoreCase = true
                ) else false
        }

        // Фильтр по дате поставки c
        val deliveryDateFrom = form.deliveryDateFrom
        deliveryDateFrom?.let { itemList.removeIf { it.deliveryDate?.isBefore(deliveryDateFrom) == true } }

        // Фильтр по дате поставки по
        val deliveryDateTo = form.deliveryDateTo
        deliveryDateTo?.let { itemList.removeIf { it.deliveryDate?.isAfter(deliveryDateTo) == true } }

        return itemList
    }
}