package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.SessionAttributes
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.ONE_LONG
import ru.korundm.constant.BaseConstant.ZERO_INT
import ru.korundm.constant.RequestPath
import ru.korundm.constant.view.ContractConstant
import ru.korundm.constant.view.ContractConstant.CONTRACT_TYPE_SEARCH_TYPE_LIST
import ru.korundm.constant.view.ContractConstant.DEFAULT_ADVANCE
import ru.korundm.constant.view.ContractConstant.EXTERNAL_TYPE_LIST
import ru.korundm.constant.view.ContractConstant.INTERNAL_TYPE
import ru.korundm.dao.*
import ru.korundm.dto.DropdownOption
import ru.korundm.enumeration.*
import ru.korundm.exception.AlertUIException
import ru.korundm.form.ContractDeliveryStatementEditProductForm
import ru.korundm.form.ContractInvoicesFilterForm
import ru.korundm.form.ContractPaidProductsFilterForm
import ru.korundm.helper.FileStorageType
import ru.korundm.util.KtCommonUtil.getChargesProtocolList
import ru.korundm.util.KtCommonUtil.getUser
import java.math.BigDecimal
import java.time.LocalDate
import javax.servlet.http.HttpSession
import ru.korundm.form.ContractEditDocumentationForm as EditDocumentationForm
import ru.korundm.form.ContractEditForm as ListEditForm
import ru.korundm.form.ContractEditInvoiceForm as EditInvoiceForm
import ru.korundm.form.ContractEditPaymentDistributionForm as DistributionPaymentForm
import ru.korundm.form.ContractEditPaymentForm as EditPaymentForm
import ru.korundm.form.ContractEditSectionForm as EditSectionForm

private const val INVOICES_FILTER_FORM_ATTR = "contractInvoicesFilterForm"
private const val PAID_PRODUCTS_FILTER_FORM_ATTR = "contractPaidProductsFilterForm"
@SessionAttributes(
    names = [
        INVOICES_FILTER_FORM_ATTR,
        PAID_PRODUCTS_FILTER_FORM_ATTR
    ],
    types = [
        ContractInvoicesFilterForm::class,
        ContractPaidProductsFilterForm::class
    ]
)
@ViewController([RequestPath.View.Prod.CONTRACT])
class ContractViewProdController(
    private val contractService: ContractService,
    private val userService: UserService,
    private val contractSectionService: ContractSectionService,
    private val productTypeService: ProductTypeService,
    private val productService: ProductService,
    private val lotService: LotService,
    private val paymentService: PaymentService,
    private val sectionDocumentationService: ContractSectionDocumentationService,
    private val fileStorageService: FileStorageService,
    private val allotmentService: AllotmentService,
    private val serviceTypeService: ServiceTypeService
) {


    @ModelAttribute(INVOICES_FILTER_FORM_ATTR)
    fun contractInvoicesFilterFormAttr() = ContractInvoicesFilterForm()

    @ModelAttribute(PAID_PRODUCTS_FILTER_FORM_ATTR)
    fun contractPaidProductsFilterFormAttr() = ContractPaidProductsFilterForm()

    @GetMapping("/list")
    fun list(model: ModelMap) = "prod/include/contract/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        val contractTypeList = CONTRACT_TYPE_SEARCH_TYPE_LIST.map { DropdownOption(it.id, it.code) }.toList()
        model.addAttribute("contractTypeList", contractTypeList)
        return "prod/include/contract/list/filter"
    }

    // Информации о договоре и его доп. соглашениях
    @GetMapping("/list/structure")
    fun listStructure(
        model: ModelMap,
        id: Long
    ): String {
        val contract = contractService.read(id)
        val section = contractSectionService.getFirstSection(contract)
        val fullNumberWithDate = "Договор № ${contract.fullNumber} от ${section.createDate?.format(DATE_FORMATTER)}"
        model.addAttribute("fullNumberWithDate", fullNumberWithDate)
        model.addAttribute("id", id)
        return "prod/include/contract/list/structure"
    }

    @GetMapping("/list/edit")
    fun listEdit(
        session: HttpSession,
        model: ModelMap,
        id: Long?
    ): String {
        val form = ListEditForm(id)
        val user = session.getUser()
        if (id != null) {
            val contract = contractService.read(id)
            val section = contractSectionService.getFirstSection(contract)
            val archiveDate = section.archiveDate
            form.id = contract.id
            form.customerId = contract.customer?.id
            val contractType = contract.type
            val any = contractType?.equals(ContractType.INTERNAL_APPLICATION)
            form.customerTypeId = if (any == true) CustomerTypeEnum.INTERNAL.id else CustomerTypeEnum.EXTERNAL.id
            form.createDate = section.createDate
            form.contractType = contractType
            form.archiveDate = archiveDate
            form.archive = archiveDate == null
            form.comment = contract?.comment ?: ""
            form.identifier = section.identifier
            form.externalNumber = section.externalNumber
            form.manager = section.manager
            val sendToClientDate = section.sendToClientDate
            form.classDisable = if (section.lotGroupList.isNotEmpty()) "disabled" else ""
            form.sendToClientDate = sendToClientDate
            model.addAttribute("contractTypeId", contract.type?.id)
            model.addAttribute("contractNumber", contract.fullNumber)
        } else {
            form.customerTypeId = CustomerTypeEnum.EXTERNAL.id
            form.createDate = LocalDate.now()
            form.archive = true
            form.manager = user
        }
        val userList = userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) }
        model.addAttribute("form", form)
        model.addAttribute("isSendToClient", form.sendToClientDate != null)
        model.addAttribute("userList", userList)
        model.addAttribute("internalApplication", INTERNAL_TYPE)
        model.addAttribute("contractTypeExternalList", EXTERNAL_TYPE_LIST)
        model.addAttribute("externalCustomerTypeId", CustomerTypeEnum.EXTERNAL.id)
        model.addAttribute("internalCustomerTypeId", CustomerTypeEnum.INTERNAL.id)
        return "prod/include/contract/list/edit"
    }

    @GetMapping("/list/edit/customer")
    fun listEditCustomer() = "prod/include/contract/list/edit/customer"

    @GetMapping("/list/edit/customer/filter")
    fun listEditCustomerFilter() = "prod/include/contract/list/edit/customer/filter"

    // Детальная информация о договоре
    @GetMapping("/detail")
    fun detail(
        model: ModelMap,
        contractId: Long,
        sectionId: Long
    ): String {
        val contract = contractService.read(contractId)
        val isSection = contractSectionService.read(sectionId).number > ZERO_INT
        model.addAttribute("contractFullNumber", contract.fullNumber)
        model.addAttribute("sectionList", contract.sectionList)
        model.addAttribute("isSection", isSection)
        model.addAttribute("sectionId", sectionId)
        return "prod/include/contract/detail"
    }

    // Основная информация о договоре или доп. соглашении
    @GetMapping("/detail/general")
    fun detailGeneral(
        model: ModelMap,
        sectionId: Long
    ): String {
        val section = contractSectionService.read(sectionId)
        val contract = section.contract
        val sectionNumber = section.number
        val isSectionNumberZero = sectionNumber == ZERO_INT
        val archiveDate = section.archiveDate
        val sendToClientDate = section.sendToClientDate
        val sendToClient = sendToClientDate?.format(DATE_FORMATTER) ?: "Не передано"
        val isActive = archiveDate == null
        val status = if (isActive) "Активен" else "Добавлено в архив " + archiveDate?.format(DATE_FORMATTER)
        model.addAttribute("contractSection", section)
        model.addAttribute("isSectionNumberZero", isSectionNumberZero)
        model.addAttribute("isAdditionalAgreement", sectionNumber > ZERO_INT)
        model.addAttribute("sectionNumber", sectionNumber)
        model.addAttribute("customerName", contract?.customer?.name)
        model.addAttribute("creationDate", section.createDate?.format(DATE_FORMATTER))
        model.addAttribute("isActive", isActive)
        model.addAttribute("sendToClient", sendToClient)
        model.addAttribute("status", status)
        model.addAttribute("externalName", section.externalNumber)
        model.addAttribute("comment", section.comment)
        model.addAttribute("identifier", section.identifier)
        model.addAttribute("separateAccount", section.separateAccount?.account)
        model.addAttribute("leadContract", section.manager?.userOfficialName)
        model.addAttribute("contractId", contract?.id)
        model.addAttribute("sectionId", section.id)
        model.addAttribute("isSendToClientDate", sendToClientDate == null)
        return "prod/include/contract/detail/general"
    }

    // Редактирование дополнительного соглашения
    @GetMapping("/detail/general/edit")
    fun detailGeneralEdit(
        session: HttpSession,
        model: ModelMap,
        id: Long
    ): String {
        val user = session.getUser()
        val form = EditSectionForm()
        val section = contractSectionService.read(id)
        val contract = section.contract
        val sectionNumber = section.number
        val isSectionNumberZero = sectionNumber == ZERO_INT
        form.id = section.id
        if (isSectionNumberZero) {
            val now = LocalDate.now()
            contract?.let {
                val lastSection = contractSectionService.getLastContractSection(it)
                var lastSectionNumber = lastSection.number
                val newSectionNumber = ++lastSectionNumber
                form.sectionNumber = newSectionNumber
                form.createDate = now
                form.manager = user
                model.addAttribute("newSectionNumber", newSectionNumber)
                model.addAttribute("now", now)
            }
        } else {
            val archiveDate = section.archiveDate
            form.createDate = section.createDate
            form.externalNumber = section.externalNumber
            form.archiveDate = archiveDate
            form.archive = archiveDate == null
            form.comment = section.comment
            form.identifier = section.identifier
            form.sendToClientDate = section.sendToClientDate
            form.manager = section.manager
        }
        val userList = userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) }
        model.addAttribute("form", form)
        model.addAttribute("fullNumber", section.fullNumber)
        model.addAttribute("isSectionNumberZero", isSectionNumberZero)
        model.addAttribute("sectionId", id)
        model.addAttribute("userList", userList)
        return "prod/include/contract/detail/general/edit"
    }

    // Ведомость поставки
    @GetMapping("/detail/delivery-statement")
    fun detailDeliveryStatement(
        model: ModelMap,
        sectionId: Long
    ): String {
        val section = contractSectionService.read(sectionId)
        val sectionNumber = section.number
        model.addAttribute("sectionId", section.id)
        model.addAttribute("sectionNumber", sectionNumber)
        model.addAttribute("isAdditionalAgreement", sectionNumber > ZERO_INT)
        return "prod/include/contract/detail/deliveryStatement"
    }

    @GetMapping("/detail/delivery-statement/filter")
    fun detailDeliveryStatementFilter() = "prod/include/contract/detail/delivery-statement/filter"

    // Добавление изделия в ведомость поставки
    @GetMapping("/detail/delivery-statement/add")
    fun detailDeliveryStatementAddProduct(model: ModelMap, sectionId: Long): String {
        model.addAttribute("sectionId", sectionId)
        val serviceTypeList = serviceTypeService.all.map { type -> type.id?.let { type.name?.let { name -> DropdownOption(it, name) } } }
        model.addAttribute("serviceTypeList", serviceTypeList)
        return "prod/include/contract/detail/delivery-statement/add"
    }

    @GetMapping("/detail/delivery-statement/add/filter")
    fun detailDeliveryStatementAddFilter(model: ModelMap): String {
        val productTypeList = productTypeService.all.map { type -> DropdownOption(type.id, type.name) }
        model.addAttribute("productTypeList", productTypeList)
        return "prod/include/contract/detail/delivery-statement/add/filter"
    }

    @GetMapping("/detail/delivery-statement/edit")
    fun detailDeliveryStatementAddProductEdit(
        model: ModelMap,
        lotId: Long?,
        productId: Long?,
        serviceTypeId: Long?,
        sectionId: Long?
    ): String {
        val form = ContractDeliveryStatementEditProductForm()
        if (lotId == null) {
            val section = sectionId?.let { contractSectionService.read(it) }
            val product = productId?.let { productService.read(it) }
            val serviceType = serviceTypeId?.let { serviceTypeService.read(it) }
            var conditionalName = product?.conditionalName
            conditionalName =
                if (serviceTypeId == ContractConstant.MANUFACTURING ||
                    serviceTypeId == ContractConstant.EXPORT_MANUFACTURING ||
                    serviceTypeId == ContractConstant.SCIENTIFIC_AND_TECHNICAL_MANUFACTURING ||
                    serviceTypeId == ContractConstant.INTERNAL_MANUFACTURING ||
                    serviceTypeId == ContractConstant.ORDER_MANUFACTURING) {
                    conditionalName
                } else {
                    "${serviceType?.prefix} $conditionalName"
                }
            form.sectionId = section?.id
            form.productId = product?.id
            form.serviceTypeId = serviceTypeId
            form.deliveryDate = LocalDate.now()
            val isExport = ContractType.SUPPLY_OF_EXPORTED == section?.contract?.type
            val price = if (isExport) product?.exportPrice else product?.price
            form.price = price?.let { price } ?: BigDecimal.ZERO
            val productChargesProtocolList = getChargesProtocolList(product)
            model.addAttribute("contractSectionId", section?.id)
            model.addAttribute("productId", product?.id)
            model.addAttribute("conditionalName", conditionalName)
            model.addAttribute("contractSectionFullNumber", section?.fullNumber)
            model.addAttribute("isExport", !isExport)
            model.addAttribute("price", price)
            model.addAttribute("productChargesProtocolList", productChargesProtocolList)
            model.addAttribute("isProtocolList", productChargesProtocolList.isNotEmpty())
        } else {
            val lot = lotService.read(lotId)
            val lotGroup = lot.lotGroup
            val product = lotGroup?.product
            val lotAmount = lot.amount
            val section = lotGroup?.contractSection
            var conditionalName = product?.conditionalName
            val serviceType = lotGroup?.serviceType
            val typeId = serviceType?.id
            conditionalName =
                if (typeId == ContractConstant.MANUFACTURING ||
                    typeId == ContractConstant.EXPORT_MANUFACTURING ||
                    typeId == ContractConstant.SCIENTIFIC_AND_TECHNICAL_MANUFACTURING ||
                    typeId == ContractConstant.INTERNAL_MANUFACTURING ||
                    typeId == ContractConstant.ORDER_MANUFACTURING) {
                    conditionalName
                } else {
                    serviceType?.prefix + " " + conditionalName
                }
            form.lotId = lotId
            form.sectionId = section?.id
            form.productId = product?.id
            form.serviceTypeId = typeId
            form.amount = lotAmount
            form.deliveryDate = lot.deliveryDate
            form.acceptType = lot.acceptType
            form.specialTestType = lot.specialTestType
            form.priceKind = lot.priceKind
            form.price = lot.price
            val protocol = lot.protocol
            protocol?.let {
                form.productChargesProtocol = it.id
                model.addAttribute("protocolId", form.productChargesProtocol)
            }
            val isExport: Boolean = ContractType.SUPPLY_OF_EXPORTED == section?.contract?.type
            val price = if (isExport) product?.exportPrice else product?.price
            val chargesProtocolList = getChargesProtocolList(product)
            model.addAttribute("contractSectionId", section?.id)
            model.addAttribute("conditionalName", conditionalName)
            model.addAttribute("contractSectionFullNumber", section?.fullNumber)
            model.addAttribute("productChargesProtocolList", chargesProtocolList)
            model.addAttribute("isProtocolList", chargesProtocolList.isNotEmpty())
            model.addAttribute("productId", product?.id)
            model.addAttribute("price", price)
            model.addAttribute("lotAmount", lotAmount)
        }
        model.addAttribute("form", form)
        model.addAttribute("acceptTypeList", ProductAcceptType.values())
        model.addAttribute("specialTestTypeList", SpecialTestType.values())
        model.addAttribute("priceKindTypeList", listOf(PriceKindType.PRELIMINARY, PriceKindType.EXPORT, PriceKindType.FINAL))
        model.addAttribute("priceKindExport", PriceKindType.EXPORT)
        model.addAttribute("priceKindFinal", PriceKindType.FINAL)
        model.addAttribute("priceKindPreliminary", PriceKindType.PRELIMINARY)
        return "prod/include/contract/detail/delivery-statement/add/edit"
    }

    // Информации о распределении поставки
    @GetMapping("/detail/delivery-statement/distribution")
    fun detailDeliveryStatementStructure(
        model: ModelMap,
        lotId: Long
    ): String {
        val lot = lotService.read(lotId)
        val lotGroup = lot.lotGroup
        val product = lotGroup?.product
        var conditionalName = product?.conditionalName
        val serviceType = lotGroup?.serviceType
        val serviceTypeId = serviceType?.id
        conditionalName =
            if (serviceTypeId == ContractConstant.MANUFACTURING ||
                serviceTypeId == ContractConstant.EXPORT_MANUFACTURING ||
                serviceTypeId == ContractConstant.SCIENTIFIC_AND_TECHNICAL_MANUFACTURING ||
                serviceTypeId == ContractConstant.INTERNAL_MANUFACTURING ||
                serviceTypeId == ContractConstant.ORDER_MANUFACTURING) {
                conditionalName
            } else {
                "${serviceType?.prefix} $conditionalName"
            }
        model.addAttribute("lotId", lotId)
        model.addAttribute("conditionalName", conditionalName)
        model.addAttribute("lotAmount", lot.amount)
        model.addAttribute("deliveryDate", lot.deliveryDate)
        return "prod/include/contract/detail/delivery-statement/distribution"
    }

    // Платежи договора или дополнительного соглашения
    @GetMapping("/detail/payments")
    fun detailPayments(
        model: ModelMap,
        sectionId: Long
    ): String {
        val section = contractSectionService.read(sectionId)
        val sectionNumber = section.number
        model.addAttribute("sectionId", section.id)
        model.addAttribute("contractSectionNumber", sectionNumber)
        model.addAttribute("isAdditionalAgreement", sectionNumber > ZERO_INT)
        return "prod/include/contract/detail/payments"
    }

    // Счета договора или дополнительного соглашения
    @GetMapping("/detail/invoices")
    fun detailInvoices(
        model: ModelMap,
        sectionId: Long
    ): String {
        val section = contractSectionService.read(sectionId)
        val sectionNumber = section.number
        val invoiceStatusList = InvoiceStatus.values().map { status -> DropdownOption(status.id, status.description) }
        model.addAttribute("contractSectionId", section.id)
        model.addAttribute("contractSectionNumber", sectionNumber)
        model.addAttribute("isAdditionalAgreement", sectionNumber > ZERO_INT)
        model.addAttribute("invoiceStatusList", invoiceStatusList)
        return "prod/include/contract/detail/invoices"
    }

    @GetMapping("/detail/invoices/add/filter")
    fun detailInvoicesAddFilter() = "prod/include/contract/detail/invoices/add/filter"

    // Добавление счета
    @GetMapping("/detail/invoices/add")
    fun detailInvoiceAdd(
        model: ModelMap,
        sectionId: Long
    ): String {
        val form = EditInvoiceForm()
        val now = LocalDate.now()
        form.invoiceDate = now
        form.sectionId = sectionId
        form.invoiceType = InvoiceType.ADVANCE.id
        form.percentAdvance = DEFAULT_ADVANCE
        form.productionDate = now
        val invoiceTypeList = InvoiceType.values().map { status -> DropdownOption(status.id, status.description) }
        model.addAttribute("form", form)
        model.addAttribute("sectionId", sectionId)
        model.addAttribute("invoiceTypeList", invoiceTypeList)
        model.addAttribute("invoiceAdvanceId", InvoiceType.ADVANCE.id)
        model.addAttribute("invoiceForAmountId", InvoiceType.INVOICE_FOR_AMOUNT.id)
        return "prod/include/contract/detail/invoices/add"
    }

    @GetMapping("/detail/invoices/add/accounts")
    fun detailInvoicesAddAccounts() = "prod/include/contract/detail/invoices/add/accounts"

    @GetMapping("/detail/invoices/edit/filter")
    fun detailInvoicesEditFilter() = "prod/include/contract/detail/invoices/edit/filter"

    @GetMapping("/detail/invoices/edit/accounts")
    fun detailInvoicesEditAccounts() = "prod/include/contract/detail/invoices/edit/accounts"

    @GetMapping("/detail/payments/filter")
    fun detailPaymentsFilter() = "prod/include/contract/detail/payments/filter"

    @GetMapping("/detail/payment/edit")
    fun detailPaymentEdit(
        model: ModelMap,
        id: Long?,
        sectionId: Long
    ): String {
        val form = EditPaymentForm()
        val section = contractSectionService.read(sectionId)
        form.payer = section.contract?.customer?.shortName
        if (id != null) {
            val payment = paymentService.read(id)
            form.id = payment.id
            form.sectionId = payment.contractSection?.id
            form.date = payment.date
            form.number = payment.number
            form.amount = payment.amount
            form.invoice = payment.invoice
            form.advanceInvoice = payment.advanceInvoiceNumber
            form.accountPayerId = payment.account?.id
            form.note = payment.note
        } else {
            form.date = LocalDate.now()
            form.sectionId = sectionId
        }
        val invoicesNumbersList = section.invoiceList.map {
            val invoiceId = it.id
            val number = it.number
            val price = it.price
            val description = it.status?.description
            DropdownOption(invoiceId, "Счет № $number от ${it.createdDate?.format(DATE_FORMATTER)} на сумму $price руб. Статус: $description")
        }
        model.addAttribute("form", form)
        model.addAttribute("invoicesNumbersList", invoicesNumbersList)
        return "prod/include/contract/detail/payments/edit"
    }

    @GetMapping("/detail/payments/edit/accounts")
    fun detailPaymentsEditAccounts() = "prod/include/contract/detail/payments/edit/accounts"

    @GetMapping("/detail/payments/edit/filter")
    fun detailPaymentsEditFilter() = "prod/include/contract/detail/payments/edit/filter"

    @GetMapping("/detail/payment/distribute")
    fun detailPaymentDistribute(
        model: ModelMap,
        paymentId: Long?,
        sectionId: Long
    ): String {
        val form = DistributionPaymentForm()
        form.sectionId = sectionId
        val distributionAlgorithmTypeList = DistributionAlgorithmType.values().map { type -> DropdownOption(type.id, type.property) }
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

        form.unallocatedAmount = unallocatedSum
        form.allocatedAmount = if (paymentId != null) paymentService.read(paymentId).amount else unallocatedSum

        model.addAttribute("form", form)
        model.addAttribute("paymentId", paymentId)
        model.addAttribute("sectionId", sectionId)
        model.addAttribute("distributionAlgorithmTypeList", distributionAlgorithmTypeList)
        return "prod/include/contract/detail/payments/distribute"
    }


    // Документация
    @GetMapping("/detail/documentation")
    fun detailDocumentation(
        model: ModelMap,
        sectionId: Long
    ): String {
        model.addAttribute("sectionId", sectionId)
        return "prod/include/contract/detail/documentation"
    }

    @GetMapping("/detail/documentation/edit")
    fun detailDocumentationEdit(
        model: ModelMap,
        id: Long?,
        sectionId: Long
    ): String {
        val form = EditDocumentationForm()
        form.sectionId = sectionId
        if (id != null) {
            val doc = sectionDocumentationService.read(id)
            form.id = doc.id
            form.name = doc.name
            form.comment = doc.comment
            form.fileStorage = fileStorageService.readOneSingular(doc, FileStorageType.ContractSectionDocumentationFile)
        }
        model.addAttribute("form", form)
        return "prod/include/contract/detail/documentation/edit"
    }

    @GetMapping("/detail/documentation-formed/edit")
    fun detailDocumentationFormedEdit(
        model: ModelMap,
        sectionId: Long,
        documentType: Int
    ): String {
        model.addAttribute("sectionId", sectionId)
        model.addAttribute("documentType", documentType)
        return "prod/include/contract/documentation-formed/edit"
    }

    // Добавление в запуск
    @GetMapping("/detail/delivery-statement/distribution/edit-products-launch")
    fun detailDeliveryStatementDistributionAddToLaunch(
        model: ModelMap,
        id: Long
    ): String {
        val allotment = allotmentService.read(id)
        val lot = allotment.lot
        val lotGroup = lot?.lotGroup
        val product = lotGroup?.product
        var conditionalName = product?.conditionalName
        val serviceType = lotGroup?.serviceType
        val serviceTypeId = serviceType?.id
        conditionalName =
            if (serviceTypeId == ContractConstant.MANUFACTURING ||
                serviceTypeId == ContractConstant.EXPORT_MANUFACTURING ||
                serviceTypeId == ContractConstant.SCIENTIFIC_AND_TECHNICAL_MANUFACTURING ||
                serviceTypeId == ContractConstant.INTERNAL_MANUFACTURING ||
                serviceTypeId == ContractConstant.ORDER_MANUFACTURING) {
                conditionalName
            } else {
                "${serviceType?.prefix} $conditionalName"
            }
        model.addAttribute("conditionalName", conditionalName)
        model.addAttribute("allotmentAmount", allotment.amount)
        model.addAttribute("isAllotmentLaunch", allotment.launchProduct != null)
        if (lot != null) model.addAttribute("deliveryDate", lot.deliveryDate)
        model.addAttribute("allotmentId", allotment.id)
        return "prod/include/contract/detail/delivery-statement/distribution/editProductsLaunch"
    }

    @GetMapping("/detail/delivery-statement/distribution/split")
    fun detailDeliveryStatementDistributionSplit(
        model: ModelMap,
        @RequestParam allotmentIdList: List<Long>
    ): String {
        if (allotmentIdList.isEmpty() || allotmentIdList.size > ONE_INT) throw AlertUIException("Выберите одну часть поставки")
        val allotment = allotmentService.read(allotmentIdList[0])
        if (allotment.amount == ONE_LONG) throw AlertUIException("Кол-во экземпляров изделий в части поставки должно быть больше одного. Разделение невозможно.")
        val lotGroup = allotment.lot?.lotGroup
        val serviceType = lotGroup?.serviceType
        val serviceTypeId = serviceType?.id
        var conditionalName = lotGroup?.product?.conditionalName
        conditionalName =
            if (serviceTypeId == ContractConstant.MANUFACTURING ||
                serviceTypeId == ContractConstant.EXPORT_MANUFACTURING ||
                serviceTypeId == ContractConstant.SCIENTIFIC_AND_TECHNICAL_MANUFACTURING ||
                serviceTypeId == ContractConstant.INTERNAL_MANUFACTURING ||
                serviceTypeId == ContractConstant.ORDER_MANUFACTURING) {
                conditionalName
            } else {
                "${serviceType?.prefix} $conditionalName"
            }

        model.addAttribute("conditionalName", conditionalName)
        model.addAttribute("allotmentAmount", allotment.amount)
        model.addAttribute("inputNumberMax", allotment.amount - ONE_LONG)
        model.addAttribute("allotmentId", allotment.id)
        return "prod/include/contract/detail/delivery-statement/distribution/split"
    }
}