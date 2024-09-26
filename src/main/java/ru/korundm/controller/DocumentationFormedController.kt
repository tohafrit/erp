package ru.korundm.controller

import asu.dao.AsuProdModuleService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.xmlbeans.XmlException
import org.apache.xmlbeans.XmlOptions
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.BaseConstant.DATE_PATTERN
import ru.korundm.constant.BaseConstant.MONTH_FORMATTER
import ru.korundm.dao.*
import ru.korundm.dto.letter.LetterAllotmentItemDto
import ru.korundm.enumeration.*
import ru.korundm.enumeration.DocumentationType.*
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.manager.documentation.DocumentationWordGenerate
import ru.korundm.util.KtCommonUtil.attachDocumentDOCX
import ru.korundm.util.KtCommonUtil.contractFullNumber
import ru.korundm.util.KtCommonUtil.getUser
import ru.korundm.util.KtCommonUtil.padStartZero
import ru.korundm.util.KtCommonUtil.safetyReadListValue
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
class DocumentationFormedController(
    private val invoiceService: InvoiceService,
    private val contractDocumentationParamsService: ContractDocumentationParamsService,
    private val documentationWordGenerate: DocumentationWordGenerate,
    private val contractSectionService: ContractSectionService,
    private val companyTypeService: CompanyTypeService,
    private val valueAddedTaxService: ValueAddedTaxService,
    private val lotService: LotService,
    private val paymentService: PaymentService,
    private val objectMapper: ObjectMapper,
    private val accountService: AccountService,
    private val allotmentService: AllotmentService,
    private val internalWaybillService: InternalWaybillService,
    private val shipmentWaybillService: ShipmentWaybillService,
    private val matValueService: MatValueService,
    private val sapsanProductService: SapsanProductService,
    private val asuProdModuleService: AsuProdModuleService,
    private val productionShipmentLetterService: ProductionShipmentLetterService,
    private val presentLogRecordService: PresentLogRecordService,
    private val productService: ProductService,
    private val userService: UserService
) {

    @GetMapping("/production-shipment-letter-documentation-formed/download/{documentId}")
    fun productionShipmentLetterDocumentationFormedDownload(
        session: HttpSession,
        response: HttpServletResponse,
        @PathVariable documentId: Long
    ) {
        val letterMap = ModelMap()
        val letterAllotmentList = contractSectionService.findTableData(documentId, LetterAllotmentItemDto::class).map {
            it.sectionFullNumber = contractFullNumber(it.contractNumber, it.performer, it.type, it.year, it.number)
            it.acceptTypeCode = ProductAcceptType.getById(it.acceptType).code
            it.specialTestTypeCode = it.specialTestType?.let { specType -> SpecialTestType.getById(specType).code } ?: SpecialTestType.WITHOUT_CHECKS.code
            it
        }
        val letter = productionShipmentLetterService.read(documentId)
        letterMap.addAttribute("allotmentList", letterAllotmentList)
        letterMap.addAttribute("number", letter.fullNumber)
        letterMap.addAttribute("date", letter.createDate?.format(DATE_FORMATTER))

        val headerText = "Письмо на производство ${letter.fullNumber}"
        response.attachDocumentDOCX(documentationWordGenerate.productionShipmentLetter(letterMap), headerText)
    }

    @GetMapping("/invoice-documentation-formed/download/{documentId}")
    fun invoiceDocumentationFormedDownload(
        session: HttpSession,
        response: HttpServletResponse,
        @PathVariable documentId: Long
    ) {
        val invoiceModel = ModelMap()
        val invoice = invoiceService.read(documentId) ?: throw AlertUIException("Счет не найден")
        var companyType = CompanyTypeEnum.OAO_KORUND_M
        when (invoice.contractSection?.contract?.performer) {
            Performer.NIISI -> companyType = CompanyTypeEnum.NIISI
            Performer.KORUND -> companyType = CompanyTypeEnum.KORUND_M
            Performer.OAOKORUND -> companyType = CompanyTypeEnum.OAO_KORUND_M
        }

        val performer = companyTypeService.getFirstByCompanyType(companyType)
        invoiceModel.addAttribute("address", performer.company?.juridicalAddress)
        invoiceModel.addAttribute("kinn", performer.company?.inn)
        invoiceModel.addAttribute("kkpp", performer.company?.kpp)
        invoiceModel.addAttribute("rs", invoice.account?.account)
        invoiceModel.addAttribute("bank", invoice.account?.bank?.name)
        invoiceModel.addAttribute("location", invoice.account?.bank?.location)
        invoiceModel.addAttribute("ks", invoice.account?.bank?.correspondentAccount)
        invoiceModel.addAttribute("bik", invoice.account?.bank?.bik)
        invoiceModel.addAttribute("deadline", invoice.dateValidBefore?.format(DATE_FORMATTER))
        invoiceModel.addAttribute("delivery", invoice.productionFinishDate?.format(DATE_FORMATTER))
        invoiceModel.addAttribute("contract", invoice.contractSection?.let {
            "${if (it.externalNumber.isNullOrBlank()) "" else "${it.externalNumber}/"}${contractFullNumber(
                it.contract?.number,
                it.contract?.performer?.id,
                it.contract?.type?.id,
                it.year,
                it.number
            )}"
        })
        invoiceModel.addAttribute("contractDate", invoice.contractSection?.createDate?.format(DATE_FORMATTER))
        invoiceModel.addAttribute("invoice", invoice.number.toString())
        invoiceModel.addAttribute("date", invoice.createdDate?.format(DATE_FORMATTER))
        invoiceModel.addAttribute("customer", invoice.contractSection?.contract?.customer?.name)
        invoiceModel.addAttribute("custaddr", invoice.contractSection?.contract?.customer?.juridicalAddress)
        invoiceModel.addAttribute("cinn", invoice.contractSection?.contract?.customer?.inn)
        invoiceModel.addAttribute("ckpp", invoice.contractSection?.contract?.customer?.kpp)
        val oneHundred = BigDecimal.valueOf(100)
        var total = BigDecimal.ZERO
        invoice.allotmentList.forEach {
            total += it.amount.toBigDecimal() * (it.neededPrice + (it.neededPrice * it.lot?.vat?.value!!.toBigDecimal() / oneHundred))
        }
        var totalVAT = BigDecimal.ZERO
        invoice.allotmentList.forEach {
            totalVAT += it.amount.toBigDecimal() * (it.neededPrice * it.lot?.vat?.value!!.toBigDecimal() / oneHundred)
        }
        var paid = BigDecimal.ZERO
        invoice.allotmentList.forEach { paid += it.paid }
        val perc = invoice.price / ((total / oneHundred))
        var vat = BigDecimal.ZERO
        if (invoice.type == InvoiceType.ADVANCE) {
            vat = totalVAT * perc / oneHundred
        } else if (invoice.type == InvoiceType.INVOICE_FOR_AMOUNT) {
            vat = invoice.price * valueAddedTaxService.findByDateFrom(LocalDate.now())?.value!!.toBigDecimal() / oneHundred
        } else {
            invoice.allotmentList.forEach {
                val value = it.amount.toBigDecimal() * (it.neededPrice + (it.neededPrice * it.lot?.vat?.value!!.toBigDecimal() / oneHundred))
                vat += value - it.paid * it.lot?.vat?.value!!.toBigDecimal() / oneHundred
            }
        }
        invoiceModel.addAttribute("perc", perc)
        invoiceModel.addAttribute("total", total)
        invoiceModel.addAttribute("totalVAT", totalVAT)
        invoiceModel.addAttribute("totalWOVAT", total - totalVAT)
        invoiceModel.addAttribute("paid", paid)
        invoiceModel.addAttribute("priceToPay", invoice.price - vat)
        invoiceModel.addAttribute("vat", vat)
        invoiceModel.addAttribute("toPay", invoice.price)
        invoiceModel.addAttribute("invoiceType", invoice.type)
        invoiceModel.addAttribute("allotmentList", invoice.allotmentList)

        val headerText = "Счет на оплату " + invoice.number + "-" + invoice.createdDate?.year.toString()
        response.attachDocumentDOCX(documentationWordGenerate.invoice(invoiceModel), headerText)
    }

    @GetMapping("/warehouse-documentation-formed/download")
    fun warehouseDocumentationFormedDownload(
        session: HttpSession,
        response: HttpServletResponse,
        type: Long,
        id: Long?,
        chiefId: Long?,
        customer: String?,
        @DateTimeFormat(pattern = DATE_PATTERN) date: LocalDate?,
        @DateTimeFormat(pattern = DATE_PATTERN) dateFrom: LocalDate?,
        @DateTimeFormat(pattern = DATE_PATTERN) dateTo: LocalDate?
    ) {
        val headerText: String
        lateinit var generatedDocument: XWPFDocument
        when (DocumentationType.getById(type)) {
            MSN -> {
                val waybill = internalWaybillService.read(id ?: 0) ?: throw AlertUIException("Накладная не найдена")
                headerText = "МСН"
                generatedDocument = documentationWordGenerate.internalWaybill(waybill)
            }
            WAREHOUSE_ACT -> {
                val waybill = shipmentWaybillService.read(id ?: 0) ?: throw AlertUIException("Накладная не найдена")
                val waybillModel = ModelMap()
                waybillModel.addAttribute("date", waybill.createDate.format(DATE_FORMATTER))
                waybillModel.addAttribute("sectionName", waybill.contractSection?.let {
                    "${if (it.externalNumber.isNullOrBlank()) "" else "${it.externalNumber}/"}${contractFullNumber(
                        it.contract?.number,
                        it.contract?.performer?.id,
                        it.contract?.type?.id,
                        it.year,
                        it.number
                    )}"
                })
                waybillModel.addAttribute("payer", waybill.contractSection?.contract?.customer?.name)
                waybillModel.addAttribute("consigneeId", waybill.consignee?.id)
                waybillModel.addAttribute("reciever", waybill.receiver ?:"")
                waybillModel.addAttribute("transmittalLetter", waybill.transmittalLetter)
                waybillModel.addAttribute("permitUser", waybill.permitUser?.userOfficialName)
                waybillModel.addAttribute("accountantUser", waybill.accountantUser?.userOfficialName)
                waybillModel.addAttribute("letterOfAttorney", waybill.letterOfAttorney)
                waybillModel.addAttribute("matValues", waybill.matValueList)
                headerText = "Акт"
                generatedDocument = documentationWordGenerate.warehouseAct(waybillModel)
            }
            WAYBILL -> {
                val waybill = shipmentWaybillService.read(id ?: 0) ?: throw AlertUIException("Накладная не найдена")
                val waybillModel = ModelMap()
                waybillModel.addAttribute("number", waybill.number.toString())
                waybillModel.addAttribute("createDate", waybill.createDate.format(DATE_FORMATTER))
                waybillModel.addAttribute("sectionName", waybill.contractSection?.let {
                    "${if (it.externalNumber.isNullOrBlank()) "" else "${it.externalNumber}/"}${contractFullNumber(
                        it.contract?.number,
                        it.contract?.performer?.id,
                        it.contract?.type?.id,
                        it.year,
                        it.number
                    )}"
                })
                waybillModel.addAttribute("payer", waybill.contractSection?.contract?.customer?.name)
                waybillModel.addAttribute("reciever", waybill.receiver ?: "")
                waybillModel.addAttribute("giveUser", waybill.giveUser?.userOfficialName)
                waybillModel.addAttribute("permitUser", waybill.permitUser?.userOfficialName)
                waybillModel.addAttribute("accountantUser", waybill.accountantUser?.userOfficialName)
                waybillModel.addAttribute("letterOfAttorney", waybill.letterOfAttorney)
                waybillModel.addAttribute("matValues", waybill.matValueList)

                headerText = "Накладная на отгрузку"
                generatedDocument = documentationWordGenerate.shipmentWaybill(waybillModel)
            }
            WAREHOUSE_RESIDUE -> {
                headerText = "Остатки на складе на ${LocalDate.now().format(DATE_FORMATTER)}"
                generatedDocument = documentationWordGenerate.warehouseResidue(matValueService.findWarehouseResidueDocData())
            }
            WAREHOUSE_RECEIPT_SHIPMENT_PRODUCT_PERIOD -> {
                val product = id?.let { productService.read(it) } ?: throw AlertUIException("Изделие не найдено")
                val datePeriodFrom = dateFrom ?: throw AlertUIException("Период не задан")
                val datePeriodTo = dateTo ?: throw AlertUIException("Период не задан")
                headerText = "Поступление и отгрузка изделия ${product.conditionalName} за период с ${datePeriodFrom.format(DATE_FORMATTER)} по ${datePeriodTo.format(DATE_FORMATTER)}"
                generatedDocument = documentationWordGenerate.warehouseReceiptShipmentProductPeriod(matValueService.findWarehouseReceiptShipmentProductPeriodData(product.id ?: 0, datePeriodFrom, datePeriodTo), product, datePeriodFrom, datePeriodTo)
            }
            WAREHOUSE_MONTHLY_SHIPMENT_REPORT -> {
                headerText = "Отчет об отгрузке для ПЗ на ${date?.format(MONTH_FORMATTER)}"
                generatedDocument = documentationWordGenerate.warehouseMonthlyShipmentReport(matValueService.findWarehouseMonthlyShipmentReportData(date ?: LocalDate.now()), date, customer, chiefId?.let { userService.read(it) } )
            }
            else -> throw IllegalArgumentException("undefined documentation type")
        }
        response.attachDocumentDOCX(generatedDocument, headerText)
    }

    @GetMapping("/contract-documentation-formed/download/{documentationId}")
    fun contractDocumentationFormedDownload(
        session: HttpSession,
        response: HttpServletResponse,
        @PathVariable documentationId: Long
    ) {
        val document = contractDocumentationParamsService.read(documentationId)
        val params = objectMapper.readValue<MutableMap<String, String>>(document.params!!)
        var headerText = ""
        lateinit var generatedDocument: XWPFDocument
        val user = session.getUser()
        if (document != null) {
            when (params["type"]?.toLong()) {
                CONTRACT.id -> {
                    val performerAccount = accountService.read(params["performerAccount"]!!.toLong())
                    val customerAccount = accountService.read(params["customerAccount"]!!.toLong())
                    val company = companyTypeService.getFirstByCompanyType(CompanyTypeEnum.OAO_KORUND_M).company!!
                    val pair = getTotalSum(document.contractSection?.id)
                    headerText = "Текст договора"
                    generatedDocument = documentationWordGenerate.contractGenerate(
                        document.contractSection,
                        performerAccount,
                        customerAccount,
                        pair.first,
                        pair.second,
                        company
                    )
                }
                EXECUTION_STATEMENT.id -> {
                    val pair = getTotalSum(document.contractSection?.id)
                    headerText = "Ведомость поставки"
                    generatedDocument = documentationWordGenerate.executionGenerate(
                        document.contractSection?.contract,
                        document.contractSection,
                        pair.first,
                        pair.second
                    )
                }
                COVERING_CUSTOMER_LETTER.id -> {
                    val invoice = invoiceService.read(params["invoiceId"]!!.toLong())
                    headerText = "Сопроводительное письмо Заказчику"
                    generatedDocument = documentationWordGenerate.coveringCustomerLetterGenerate(
                        document.contractSection?.contract,
                        document.contractSection,
                        invoice,
                        user
                    )
                }
                DISPUTE_RECONCILIATION_PROTOCOL.id -> {
                    headerText = "Протокол согласования разногласий"
                    generatedDocument = documentationWordGenerate.disputeReconciliationProtocol(document.contractSection)
                }
                LETTER_DISPUTE_RECONCILIATION_PROTOCOL.id -> {
                    headerText = "Письмо Заказчику с протоколом согласования разногласий"
                    generatedDocument = documentationWordGenerate.letterDisputeReconciliationProtocol(document.contractSection, user)
                }
                SIMPLE_LETTER.id -> {
                    headerText = "Письмо"
                    generatedDocument = documentationWordGenerate.simpleLetter(document.contractSection, user)
                }
                CUSTOMER_LETTER_PRICE_PROTOCOL.id -> {
                    headerText = "Письмо Заказчику с документами"
                    generatedDocument = documentationWordGenerate.customerLetterPriceProtocol(document.contractSection, user)
                }
                CUSTOMER_LETTER_DEED.id -> {
                    headerText = "Письмо Заказчику с документами"
                    generatedDocument = documentationWordGenerate.customerLetterDeed(document.contractSection, user)
                }
                LETTER_PZ_COPY.id -> {
                    headerText = "Письмо в ПЗ"
                    generatedDocument = documentationWordGenerate.letterPZ(document.contractSection, user)
                }
                LETTER_PZ_DISPUTE_RECONCILIATION_PROTOCOL.id -> {
                    headerText = "Письмо в ПЗ"
                    generatedDocument = documentationWordGenerate.letterPZDisputeReconciliationProtocol(document.contractSection, user)
                }
                LETTER_PZ_PRICE_PROTOCOL.id -> {
                    headerText = "Письмо в ПЗ"
                    generatedDocument = documentationWordGenerate.letterPZPriceProtocol(document.contractSection, user)
                }
                SERVICE_NOTE_ACCOUNTING.id -> {
                    headerText = "Служебная записка"
                    generatedDocument = documentationWordGenerate.serviceNoteAccounting(document.contractSection, user)
                }
                SERVICE_NOTE_ACCOUNTING_DEED.id -> {
                    headerText = "Служебная записка"
                    generatedDocument = documentationWordGenerate.serviceNoteAccountingDeed(document.contractSection, user)
                }
                SERVICE_NOTE_ACCOUNTING_DEED_REQUEST.id -> {
                    headerText = "Служебная записка"
                    generatedDocument = documentationWordGenerate.serviceNoteAccountingDeedRequest(document.contractSection, user)
                }
                DEED.id -> {
                    val pair = getTotalSum(document.contractSection?.id)
                    val payments = paymentService.getBySectionId(document.contractSection!!.id ?: 0)
                    val paid = BigDecimal.ZERO
                    payments.forEach { paid.add(it.amount) }
                    headerText = "Акт приемки-передачи"
                    generatedDocument = documentationWordGenerate.deed(document.contractSection, pair.first.add(pair.second), paid, user)
                }
                CUSTOMER_LETTER_POSTPONEMENT.id -> {
                    headerText = "Письмо о переносе сроков"
                    generatedDocument = documentationWordGenerate.customerLetterPostponement(document.contractSection, user)
                }
                SERVICE_NOTE.id -> {
                    headerText = "Служебная записка"
                    generatedDocument = documentationWordGenerate.serviceNote(document.contractSection, user)
                }
                CUSTOMER_LETTER_ADVANCE_PAYMENT.id -> {
                    val invoice = invoiceService.read(params["invoiceId"]!!.toLong())
                    headerText = "Письмо на оплату аванса"
                    generatedDocument = documentationWordGenerate.customerLetterAdvancePayment(document.contractSection, invoice, user)
                }
                CUSTOMER_LETTER_PAYMENT.id -> {
                    val invoice = invoiceService.read(params["invoiceId"]!!.toLong())
                    headerText = "Письмо на окончательную оплату"
                    generatedDocument = documentationWordGenerate.customerLetterPayment(document.contractSection, invoice, user)
                }
                CUSTOMER_LETTER_INCORRECT_PAYMENT_ORDERS.id -> {
                    val payment = paymentService.read(params["paymentId"]!!.toLong())
                    val valueAddedTax = valueAddedTaxService.findByDateFrom(payment.date)
                    // Величина процентной ставки НДС
                    headerText = "Письмо неправильные платежные поручения"
                    generatedDocument = documentationWordGenerate.customerLetterIncorrectPayment(
                        document.contractSection,
                        payment,
                        user,
                        valueAddedTax?.value?.toBigDecimal()
                    )
                }
                DISPOSITION.id -> {
                    val paymentList = paymentService.getAllById(objectMapper.safetyReadListValue(params["paymentListId"]!!, Long::class))
                    val allotmentList = allotmentService.getAllById(objectMapper.safetyReadListValue(params["allotmentListId"]!!, Long::class))
                    val vat = valueAddedTaxService.findByDateFrom(document.contractSection?.createDate)
                    headerText = "Распоряжение"
                    generatedDocument = documentationWordGenerate.letterWarehouse(document.contractSection, paymentList, allotmentList, user, vat?.value)
                }
                else -> {
                    //TODO
                }
            }
        }
        response.attachDocumentDOCX(generatedDocument, headerText)
    }

    @GetMapping("/present-log-template-documentation-formed/download/{documentId}")
    fun presentLogTemplateDocumentationFormedDownload(
        session: HttpSession,
        response: HttpServletResponse,
        @PathVariable documentId: Long
    ) {
        val presentLog = presentLogRecordService.read(documentId)
        val headerText = "Паспорта изделий предъявление ${presentLog.number}"
        val documentList = mutableListOf<XWPFDocument>()
        for (matValue in presentLog.matValueList) {
            val templateMap = ModelMap()
            val logRecord = matValue.presentLogRecord
            val serialNumber = matValue.serialNumber ?: ""
            if (serialNumber.length != 12) throw AlertUIException("Неправильный серийный номер")
            val asuCode1 = serialNumber.substring(0, 6).toInt().toString()
            val asuCode2 = serialNumber.substring(6, 8).toInt().toString()
            val asuNum = serialNumber.substring(8).toInt()
            val mainProdModule = asuProdModuleService.getByParams(asuNum, asuCode1, asuCode2)

            val prodModList = mainProdModule?.asuProdModuleInUlistList?.lastOrNull()?.ulist?.asuProdModuleInUlistList?.map { it.prodModule } ?: emptyList()
            templateMap["decimalMain"] = matValue.allotment?.lot?.lotGroup?.product?.decimalNumber ?: ""
            templateMap["serialNumberMain"] = mainProdModule?.fullBarCode ?: ""
            templateMap["headEnterprisePassport"] = logRecord?.headEnterprisePassport?.user?.userShortName ?: ""
            templateMap["seniorController"] = logRecord?.seniorController?.user?.userShortName ?: ""
            templateMap["qcdChief"] = logRecord?.qcdChief?.user?.userShortName ?: ""
            templateMap["qcdRepresentative"] = logRecord?.qcdRepresentative?.user?.userShortName ?: ""
            templateMap["path"] = matValue.allotment?.lot?.lotGroup?.product?.templatePath

            val modulesMap = mutableMapOf<String, String>()
            prodModList.forEach { pmd ->
                val module = pmd.module
                val code1 = module.code1
                val code2 = module.code2
                val modulePrefix = code1.padStartZero(6) + code2.padStartZero(2)
                val sapsanProduct = sapsanProductService.getByPrefix(modulePrefix)
                val sapsanProductBomList = sapsanProduct.sapsanProductBomList
                if (sapsanProductBomList.isNotEmpty()) {
                    modulesMap[pmd.fullBarCode] = sapsanProductBomList.firstOrNull { it.prime }?.bom?.product?.decimalNumber ?: ""
                }
            }
            templateMap["modulesMap"] = modulesMap

            documentList.add(documentationWordGenerate.presentLogTemplate(templateMap))
        }
        if (documentList.isEmpty()) throw AlertUIException("Паспорта не найдены")
        response.attachDocumentDOCX((if (documentList.size == 1) documentList.first() else mergeDocuments(documentList)), headerText)
    }

    @GetMapping("/warehouse-report-documentation-formed/download/{productId}/{startDate}/{endDate}")
    fun reportProductInfoDocumentationFormedDownload(
        session: HttpSession,
        response: HttpServletResponse,
        @PathVariable productId: Long,
        @PathVariable startDate: LocalDate,
        @PathVariable endDate: LocalDate
    ) {
        val product = productService.read(productId)
        val reportMap = ModelMap()
        reportMap.addAttribute("PRODUCT", product?.techSpecName ?: "")
        reportMap.addAttribute("STARTDATE", startDate)
        reportMap.addAttribute("ENDDATE", endDate)
        val headerText = "Полная информация об изделии ${product.techSpecName}_${startDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN))}"
        val generatedDocument = documentationWordGenerate.reportProductInfo(reportMap)
        response.attachDocumentDOCX(generatedDocument, headerText)
    }

    private fun mergeDocuments(documents: List<XWPFDocument>): XWPFDocument {
        val mergedDocument = documents.first()
        val mergedCTDocument = mergedDocument.document.body
        try {
            for (i in 1..documents.size) {
                val ctBody: CTBody = documents[i].document.body
                appendBody(mergedCTDocument, ctBody)
            }
        } catch (e: Exception) {
            throw AlertUIException("Ошибка слияния паспортов")
        }
        return mergedDocument
    }

    private fun appendBody(src: CTBody, append: CTBody) {
        val optionsOuter = XmlOptions()
        optionsOuter.setSaveOuter()
        val appendString = append.xmlText(optionsOuter)
        val srcString = src.xmlText()
        val prefix = srcString.substring(0, srcString.indexOf(">") + 1)
        val mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"))
        val suffix = srcString.substring(srcString.lastIndexOf("<"))
        val addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"))
        val breakStr = "<w:br w:type=\"page\" />"
        val makeBody = try { CTBody.Factory.parse(prefix + mainPart + breakStr + addPart + suffix) } catch (e: XmlException) { throw XmlException("", e) }
        src.set(makeBody)
    }

    private fun getTotalSum(sectionId: Long?): Pair<BigDecimal, BigDecimal> {
        val contractSection = sectionId?.let { contractSectionService.read(it) }
        val totalLotGroupCostList = mutableListOf<BigDecimal>()
        val totalVATList = mutableListOf<BigDecimal>()
        val hundred = BigDecimal.valueOf(100)
        lotService.getLotListBySection(contractSection).forEach { lot ->
            // Определяем в какой период действия ставки НДС попадает дата поставки lot-a
            val valueAddedTax = lot.vat
            // Величина процентной ставки НДС
            val vat = valueAddedTax?.value?.toBigDecimal() ?: BigDecimal.ZERO
            val lotPriceAmountSum = lot.neededPrice.multiply(BigDecimal.valueOf(lot.amount))
            val totalVAT = lotPriceAmountSum.multiply(vat).divide(hundred)
            totalLotGroupCostList += lotPriceAmountSum
            totalVATList += totalVAT
        }
        val totalSum = totalLotGroupCostList.fold(BigDecimal.ZERO) { total, next -> total + next }
        val totalVATSum = totalVATList.fold(BigDecimal.ZERO) { total, next -> total + next }
        return totalSum to totalVATSum
    }
}