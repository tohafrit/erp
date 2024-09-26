package ru.korundm.controller.action.prod

import asu.dao.AsuProdModuleService
import asu.entity.AsuProdModule
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.springframework.core.io.ResourceLoader
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.ZERO_INT
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.entity.Bom
import ru.korundm.entity.PresentLogRecord
import ru.korundm.entity.Product
import ru.korundm.enumeration.ProductAcceptType
import ru.korundm.enumeration.SpecialTestType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.report.word.util.WordUtil
import ru.korundm.util.KtCommonUtil.attachDocumentDOCX
import ru.korundm.util.KtCommonUtil.blankIfNullOr
import ru.korundm.util.KtCommonUtil.monthInGenitive
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.nullIfEmpty
import ru.korundm.util.KtCommonUtil.padStartZero
import ru.korundm.util.KtCommonUtil.readDynamic
import ru.korundm.util.KtCommonUtil.safetyReadMutableListValue
import java.io.FileInputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import ru.korundm.form.PresentLogRecordListEditForm as ListEditForm

// Идентификаторы протоколов для формирования пакетов документов
private const val PROTOCOL_BTM = 1
private const val PROTOCOL_BT = 2
private const val PROTOCOL_BT83 = 3
private const val PROTOCOL_BT01 = 4

@ActionController([RequestPath.Action.Prod.PRESENT_LOG_RECORD])
class PresentLogRecordActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val presentLogRecordService: PresentLogRecordService,
    private val userService: UserService,
    private val resourceLoader: ResourceLoader,
    private val productionShipmentLetterService: ProductionShipmentLetterService,
    private val allotmentService: AllotmentService,
    private val matValueService: MatValueService,
    private val documentLabelService: DocumentLabelService,
    private val asuProdModuleService: AsuProdModuleService,
    private val bomService: BomService,
    private val productService: ProductService
) {

    // Загрузка журнала регистрации предъявлений
    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val presentLogRecordNumber: Int?, // номер предъявления
            val registrationDate: LocalDate?, // дата регистрации
            val letterId: Long?, // индентификатор письма на производство
            val letterNumber: String?, //номер письма
            val contractNumber: String?, // номер договора или доп. соглашения
            val lotGroupNumber: Long?, // пункт ведомости поставки
            val customer: String?, // заказчик
            val productName: String?, // условное наименование изделия
            val amount: Int, // кол-во
            val acceptType: String, // тип приемки
            var specialTestType: String?, // тип спец. проверки
            val examAct: String?, // № акта периодических испытаний
            val familyDecimalNumber: String?, // ТУ семейства
            val suffix: String?, // суффикс
            val wrappingDate: LocalDate?, // дата передачи на упаковку
            val conformityStatement: String?, // Заявление о соответствии
            val comment: String?, // комментарий
            val canPack: Boolean, // возможность упаковки изделий
            val canStatementWord: Boolean // возможность выгрузки заявления о соответствии
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, presentLogRecordService.findTableData(input, form)) { logRecord ->
            val matValueList = matValueService.getAllByPresentLogRecord(logRecord)
            val matValue = matValueList.first()
            val lotGroup = matValue.allotment?.lot?.lotGroup
            val product = lotGroup?.product
            val section = lotGroup?.contractSection
            val isConformityStatement =
                logRecord.conformityStatementNumber != null &&
                logRecord.conformityStatementCreateDate != null &&
                logRecord.conformityStatementValidity != null &&
                logRecord.conformityStatementTransferDate != null
            Item(
                logRecord.id,
                logRecord.number,
                logRecord.registrationDate,
                matValue.letter?.id,
                matValue.letter?.fullNumber,
                section?.fullNumber,
                lotGroup?.orderIndex,
                section?.contract?.customer?.name,
                product?.conditionalName,
                matValueList.count { it.presentLogRecord != null },
                logRecord.presentationType?.let { ProductAcceptType.getById(it.id) }?.code ?: "",
                logRecord.specialTestType?.let { SpecialTestType.getById(it.id).code } ?: "",
                "${product?.examAct} от ${product?.examActDate?.format(DATE_FORMATTER)}",
                product?.familyDecimalNumber,
                product?.suffix,
                matValue.packedDate,
                if (isConformityStatement) logRecord.conformityStatementNumber else "",
                logRecord.comment,
                matValueList.all { it.packedDate == null },
                logRecord.conformityStatementNumber != null
            )
        }
    }

    // Загрузка информации о предъявлении
    @GetMapping("/list/log-record-info/load")
    fun listLogRecordLoad(
        logRecordId: Long,
        request: HttpServletRequest
    ): List<*> {
        data class Item(
            val matValueId: Long?,
            val serialNumber: String?, // серийные номера
            val technicalControlDate: LocalDate?, // дата прохождения ОТК
            val interWarehouseWaybill: String?, // МСН
            val internalWaybillId: Long?, // идентификатор МСН
        )

        val logRecord = presentLogRecordService.read(logRecordId)
        val matValueList = matValueService.getAllByPresentLogRecord(logRecord)
        return matValueList.filter { it.presentLogRecord != null }.map {
            Item (
                it.id,
                it.serialNumber,
                it.technicalControlDate,
                it.internalWaybill?.let { mv -> "${mv.number.toString().padStart(3, '0')}/АО от ${mv.acceptDate?.format(DATE_FORMATTER)}" },
                it.internalWaybill?.id
            )
        }
    }

    // Загрузка доступных серийных номеров
    @GetMapping("/list/edit/serial-number/load")
    fun listEditSerialNumberLoad(
        id: Long,
        logRecordId: Long?,
        request: HttpServletRequest
    ): List<*> {
        data class Item(
            val letterFullNumber: String?, // номер письма на производство
            val orderIndex: Long?, // пункт письма
            val contractNumber: String?, // номер договора
            val productName: String?, // изделие
            val acceptType: String?, // тип приёмки
            val serialNumber: String? // серийный номер
        )
        val allotment = allotmentService.read(id) ?: throw AlertUIException("Часть поставки не найдена")
        val lot = allotment.lot ?: throw AlertUIException("Поставка не найдена")
        val lotGroup = lot.lotGroup ?: throw AlertUIException("Группа поставок не найдена")
        val matValue = allotment.matValueList.firstOrNull { it.letter != null }
        val letterFullNumber = matValue?.letter?.fullNumber
        val prodModuleList = asuProdModuleService.getGivenSiteStateProdModuleList("10", "12400")
        val product = lotGroup.product ?: throw AlertUIException("Изделие не найдено")
        val lastBom: Bom? = bomService.getLastApprovedOrAccepted(product.id)
        val bomList = lastBom?.sapsanProductBomList.nullIfEmpty() ?: throw AlertUIException("У данного изделия нет рабочей спецификации. Невозможно получить доступные серийные номера")
        val sapsanProductBom = bomList.firstOrNull { it.prime }
        val prefix = sapsanProductBom?.sapsanProduct?.prefix ?: throw AlertUIException("Префикс изделия не был найден")

        var existSerialNumberList = emptyList<String>()
        logRecordId?.let {
            existSerialNumberList = presentLogRecordService.read(it)?.matValueList?.filterNot { mv -> mv.presentLogRecord == null }?.mapNotNull { mv -> mv.serialNumber } ?: emptyList()
        }

        if (existSerialNumberList.isNotEmpty()) {
            val existProdModuleList = mutableListOf<AsuProdModule>()
            existSerialNumberList.forEach {
                val asuCode1 = it.substring(0, 6).toInt().toString()
                val asuCode2 = it.substring(6, 8).toInt().toString()
                val asuNum = it.substring(8).toInt()
                val prodModule = asuNum.let { num -> asuProdModuleService.getByParams(num, asuCode1, asuCode2) }
                if (prodModule != null) existProdModuleList += prodModule
            }
            if (existProdModuleList.isNotEmpty()) prodModuleList.removeIf { existProdModuleList.contains(it) }
        }

        val serialNumberList = mutableListOf<AsuProdModule>()
        prodModuleList.forEach { pmd ->
            val module = pmd.module
            val code1 = module.code1
            val code2 = module.code2
            val modulePrefix = code1.padStartZero(6) + code2.padStartZero(2)
            if (modulePrefix == prefix) serialNumberList += pmd
        }

        return serialNumberList.map {
            Item(
                letterFullNumber,
                lotGroup.orderIndex,
                lotGroup.contractSection?.fullNumber,
                lotGroup.product?.conditionalName,
                lot.acceptType?.let { type -> ProductAcceptType.getById(type.id).code },
                it.fullBarCode
            )
        }
    }

    // Выгрузка заявления о соответствии
    @GetMapping("/list/download-conformity-statement-word")
    fun downloadConformityStatementWord(
        response: HttpServletResponse,
        logRecordId: Long
    ) {
        val logRecord = presentLogRecordService.read(logRecordId) ?: throw AlertUIException("Предъявление не найдено")
        val number = logRecord.conformityStatementNumber ?: ""
        val createDate = logRecord.conformityStatementCreateDate ?: throw AlertUIException("Не указана дата создания заявления о соответствии")
        val year = logRecord.conformityStatementYear ?: throw AlertUIException("Отсутствует год создания заявления о соответствии")
        val validityDate = logRecord.conformityStatementValidity ?: throw AlertUIException("Не указан срок действия заявления о соответствии")
        //val transferDate = logRecord.conformityStatementTransferDate ?: throw AlertUIException("Не указана дата передачи")
        val signatoryStatement = logRecord.signatoryConformityStatement ?: throw AlertUIException("Не указан подписант заявления о соответствии")
        //
        val day = validityDate.dayOfMonth
        val monthValue = validityDate.monthInGenitive()
        val intYear = validityDate.year.toString().substring(2)
        //
        val currenDay = createDate.dayOfMonth
        val currentMonth = createDate.monthInGenitive()
        val currentYearInt = year.toString().substring(2)
        //
        val user = userService.read(signatoryStatement.id).userShortName
        //
        val companyName = "Открытое акционерное общество"
        val reasonOne = "Протоколов предъявительских и "
        val reasonTwo = " приемосдаточных испытаний, сертификата соответствия № ВР 31.1.7469-2014."
        val chiefInfo = "исполнительного директора Шпильмана Вадима Марковича"

        val matValueList = matValueService.getAllByPresentLogRecord(logRecord)
        val matValue = matValueList.firstOrNull() ?: throw AlertUIException("Материальные ценности не найдены")
        val allotment = matValue.allotment ?: throw AlertUIException("Часть поставки не найдена")
        val lot = allotment.lot ?: throw AlertUIException("Поставка не найдена")
        val lotGroup = lot.lotGroup ?: throw AlertUIException("Группа поставок не найдена")
        val product = lotGroup.product ?: throw AlertUIException("Изделие не найдено")
        val section = lotGroup.contractSection ?: throw AlertUIException("Секция договора не найдена")
        val sectionFullNumber = section.fullNumber
        val sectionCreateDate = section.createDate?.format(DATE_FORMATTER)
        val contract = section.contract ?: throw AlertUIException("Договор не найден")
        val customer = contract.customer ?: throw AlertUIException("Заказчик не найден")
        val customerName = customer.name
        val customerLocation = customer.location ?: ""
        val conditionalName = product.conditionalName ?: ""
        val familyDecimalNumber = product.familyDecimalNumber ?: ""

        val amount = matValueList.count()
        //
        val map = hashMapOf<String, String>()
        map["number"] = number
        map["companyName"] = companyName
        map["contractInfo"] = "$sectionFullNumber от $sectionCreateDate"
        map["customerInfo"] = "$customerName, $customerLocation"
        map["productName"] = conditionalName
        map["amount"] = amount.toString()
        map["productDNumber"] = familyDecimalNumber
        map["reasonOne"] = reasonOne
        map["reasonTwo"] = reasonTwo
        map["chief"] = chiefInfo
        map["user"] = user
        map["1"] = day.toString()
        map["2"] = monthValue
        map["3"] = intYear
        map["4"] = currenDay.toString()
        map["5"] = currentMonth
        map["6"] = currentYearInt

        val resource = resourceLoader.getResource("classpath:blank/word/conformityStatement.docx")
        val fileInputStream = FileInputStream(resource.file)

        // читаем шаблон
        val template = XWPFDocument(fileInputStream)

        // Замена меток в параграфах
        val paragraphList = template.paragraphs
        for (paragraph in paragraphList) WordUtil.replaceTagInText(paragraph, map)

        // Замена меток в строках таблиц
        WordUtil.replaceTableRow(template.tables, map)

        response.attachDocumentDOCX(template,"Заявление о соответствии")
    }

    // Загрузка писем на производство
    @GetMapping("/list/edit/letter/load")
    fun listEditLetterLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val number: String?, // номер письма
            val createDate: LocalDate?, // дата создания
            val contractNumber: String? // номер договора или дополнительного соглашения
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, productionShipmentLetterService.findTableData(input, form)) { letter ->
            val sectionList = letter.matValueList.map { it.allotment?.lot?.lotGroup?.contractSection }.distinct()
            Item(
                letter.id,
                letter.fullNumber,
                letter.createDate,
                sectionList.map { it?.fullNumber }.joinToString(", "),
            )
        }
    }

    // Загрузка пунктов письма
    @GetMapping("/list/edit/letter/info/load")
    fun listEditLetterInfoLoad(
        letterId: Long,
        request: HttpServletRequest
    ): List<*> {
        data class Item(
            val id: Long?,
            val letterNumber: String?, // письмо
            var customer: String?, // заказчик
            var fullNumber: String?, // номер договора или доп. соглашения
            var productName: String?, // комплектность поставки
            var orderIndex: Long?, // пункт
            var amount: Long?, // количество
            var acceptType: String?, // тип приёмки
            var specialTestType: String?, // вид спец. проверки
            var deliveryDate: LocalDate?, // дата поставки
            var productId: Long?, // идентификатор изделия
            var examAct: String?, // № акта периодических испытаний
            var examActDate: LocalDate?, // дата составления Акта ПИ
            var familyDecimalNumber: String?, // ТУ семейства
            var suffix: String?, // суффикс
            var maxSerialNumberQuantity: Int? // максимальное кол-во matValue к добавлению
        )
        val letter = productionShipmentLetterService.read(letterId)
        val matValueList = matValueService.getAllByProductionShipmentLetter(letter).filter { it.presentLogRecord == null }
        val tableItemOutList = mutableListOf<Item>()
        matValueList.forEach {
            val allotment = it.allotment
            val lot = allotment?.lot
            val lotGroup = lot?.lotGroup
            val product = lotGroup?.product
            val contractSection = lotGroup?.contractSection
            val matValueLogRecordNullCount = matValueService.getAllByAllotment(allotment).count { mv -> mv.presentLogRecord == null }
            val item = Item(
                allotment?.id,
                letter.fullNumber,
                contractSection?.contract?.customer?.name,
                contractSection?.fullNumber,
                product?.conditionalName,
                lotGroup?.orderIndex,
                allotment?.amount,
                lot?.acceptType?.let { type -> ProductAcceptType.getById(type.id).code },
                lot?.specialTestType?.let { specType -> SpecialTestType.getById(specType.id).code },
                lot?.deliveryDate,
                product?.id,
                product?.examAct,
                product?.examActDate,
                product?.familyDecimalNumber,
                product?.suffix,
                matValueLogRecordNullCount
            )
            tableItemOutList += item
        }
        return tableItemOutList.distinct()
    }

    // Сохранение предъявления
    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        val formId = form.id
        val serialNumberList = jsonMapper.safetyReadMutableListValue(form.groupSerialNumber, ListEditForm.GroupSerialNumber::class)
        val conformityStatementList = jsonMapper.safetyReadMutableListValue(form.conformityStatement, ListEditForm.ConformityStatement::class)
        baseService.exec {
            if (serialNumberList.isNotEmpty()) {
                if (serialNumberList.any { it.serialNumber.blankIfNullOr().length != 12 }) {
                    throw AlertUIException("Серийный номер должен содержать 12 цифр")
                } else {
                    val prefixSerialNumberList = serialNumberList.map { it.serialNumber.substring(0, 8) }
                    val productIdList = serialNumberList.map { it.productId }
                    val productList: List<Product> = productService.getAllById(productIdList)
                    val product = productList.firstOrNull() ?: throw AlertUIException("Изделие не найдено")
                    val lastBom: Bom? = bomService.getLastApprovedOrAccepted(product.id)
                    val bomList = lastBom?.sapsanProductBomList.nullIfEmpty() ?: throw AlertUIException("У данного изделия нет рабочей спецификации. Добавление серийного номера невозможно")
                    val sapsanProductBom = bomList.firstOrNull { it.prime }
                    val prefix = sapsanProductBom?.sapsanProduct?.prefix ?: throw AlertUIException("Префикс изделия не был найден")
                    if (prefixSerialNumberList.any { it != prefix }) {
                        throw AlertUIException("Префикс серийных номеров не соответствует для данного вида изделия. Необходимо ввести корректный префикс серийных номеров. Для данного изделия корректным префиксом серийных номеров является $prefix")
                    }
                }
            }
            if (conformityStatementList.isNotEmpty()) {
                val conformityStatement = conformityStatementList[0]
                if (conformityStatement.conformityStatementNumber == null ||
                    conformityStatement.conformityStatementCreateDate == null ||
                    conformityStatement.conformityStatementValidity == null ||
                    conformityStatement.conformityStatementTransferDate == null) {
                    throw AlertUIException("У заявления о соответствии не заполнены все поля. Сохранение невозможно")
                }
            }

            val formSerialNumberList = serialNumberList.map { it.serialNumber }
            val repeatingElements = formSerialNumberList.groupingBy { it }.eachCount().filter { it.value > 1 }
            val isRepeatingElements = repeatingElements.isNotEmpty()
            if (isRepeatingElements) throw AlertUIException("Серийные номера должны быть уникальными. Сохранение невозможно")
            if (response.isValid) {
                val logRecord = formId?.let { presentLogRecordService.read(it) ?: throw AlertUIException("Предъявление не найдено") } ?: PresentLogRecord()
                val allotment = form.allotmentId?.let { allotmentService.read(it) }
                val allSerialNumber = matValueService.all.mapNotNull { it.serialNumber }.toMutableList()
                logRecord.apply {
                    version = form.version
                    // Находим последнее предъявление
                    val lastLogRecord = presentLogRecordService.findLastLogRecord()
                    var logRecordNumber = lastLogRecord?.number ?: ONE_INT
                    number = formId?.let { this.number } ?: lastLogRecord?.let { ++logRecordNumber } ?: logRecordNumber
                    registrationDate = form.registrationDate
                    year = form.registrationDate?.year ?: ZERO_INT
                    presentationType = form.acceptType
                    specialTestType = form.specialTestType
                    qcdRepresentative = documentLabelService.getByLabel("qcdRepresentative")
                    qcdChief = documentLabelService.getByLabel("qcdChief")
                    seniorController = documentLabelService.getByLabel("seniorController")
                    headEnterprisePassport = documentLabelService.getByLabel("headEnterprisePassport")
                    headWarehouse = documentLabelService.getByLabel("headWarehouse")
                    headEnterprise = documentLabelService.getByUserId(userService.findByUserName("shpilman_vm").id)
                    conformityStatementNumber = conformityStatementList.firstOrNull()?.conformityStatementNumber
                    val date = conformityStatementList.firstOrNull()?.conformityStatementCreateDate?.let { LocalDate.parse(it, DATE_FORMATTER) }
                    conformityStatementCreateDate = date
                    conformityStatementYear = date?.year
                    conformityStatementValidity = conformityStatementList.firstOrNull()?.conformityStatementValidity?.let { LocalDate.parse(it, DATE_FORMATTER) }
                    conformityStatementTransferDate = conformityStatementList.firstOrNull()?.conformityStatementTransferDate?.let { LocalDate.parse(it, DATE_FORMATTER) }
                    signatoryConformityStatement = conformityStatementList.firstOrNull()?.managerId?.let { userService.read(it) }
                    comment = form.comment.nullIfBlank()
                    presentLogRecordService.save(this)
                }
                if (formId == null) {
                    if (allSerialNumber.any { formSerialNumberList.contains(it) }) throw AlertUIException("Серийные номера должны быть уникальными. Сохранение невозможно")
                    val matValueList = matValueService.getAllByAllotment(allotment).filter { it.presentLogRecord == null }
                    val takeMatValueList = matValueList.take(serialNumberList.size)
                    for ((count, mv) in takeMatValueList.withIndex()) {
                        mv.presentLogRecord = logRecord
                        mv.serialNumber = serialNumberList[count].serialNumber
                        mv.technicalControlDate = form.registrationDate
                        matValueService.save(mv)
                    }
                    response.putAttribute(ObjAttr.ID, logRecord.id)
                } else {
                    // список matValue предъявления
                    val recordMatValueList = matValueService.getAllByPresentLogRecord(logRecord)
                    val recordSerialNumberList = recordMatValueList.map { it.serialNumber }
                    allSerialNumber.removeIf { recordSerialNumberList.contains(it) }
                    if (allSerialNumber.any { formSerialNumberList.contains(it) }) throw AlertUIException("Серийные номера должны быть уникальными. Сохранение невозможно")

                    recordMatValueList.forEach {
                        it.presentLogRecord = null
                        it.technicalControlDate = null
                        it.serialNumber = null
                        matValueService.save(it)
                    }

                    val matValueList = matValueService.getAllByAllotment(allotment).filter { it.presentLogRecord == null }
                    val takeMatValueList = matValueList.take(serialNumberList.size)
                    for ((count, mv) in takeMatValueList.withIndex()) {
                        mv.presentLogRecord = logRecord
                        mv.serialNumber = serialNumberList[count].serialNumber
                        mv.technicalControlDate = form.registrationDate
                        matValueService.save(mv)
                    }
                }
                allotment?.lot?.lotGroup?.product?.apply {
                    examAct = form.examAct
                    examActDate = form.examActDate
                    familyDecimalNumber = form.familyDecimalNumber
                    suffix = form.suffix
                    productService.save(this)
                }
            }
        }
        return response
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        val logRecord = presentLogRecordService.read(id) ?: throw AlertUIException("Предъявление не найдено")
        val matValueRecordList = matValueService.getAllByPresentLogRecord(logRecord).nullIfEmpty() ?: throw AlertUIException("У предъявления отсутствуют материальные ценности")
        val matValueList = matValueRecordList.filter { it.internalWaybill != null }
        if (matValueList.isNotEmpty()) {
            val serialNumbers = matValueList.map { it.serialNumber }.joinToString(", ")
            throw AlertUIException("Изделия с серийными номерами: $serialNumbers были добавлены в МСН. Удаление невозможно")
        }
        logRecord.apply {
            matValueRecordList.forEach {
                it.presentLogRecord = null
                it.serialNumber = null
                it.technicalControlDate = null
                it.packedDate = null
            }
            presentLogRecordService.delete(this)
        }
    }

    @DeleteMapping("/list/edit/delete/{id}")
    fun listEditDelete(@PathVariable id: Long) = baseService.exec {
        val logRecord = presentLogRecordService.read(id) ?: throw AlertUIException("Предъявление не найдено")
        logRecord.apply {
            this.conformityStatementNumber = null
            this.conformityStatementCreateDate = null
            this.conformityStatementYear = null
            this.conformityStatementValidity = null
            this.conformityStatementTransferDate = null
            this.signatoryConformityStatement = null
            presentLogRecordService.save(this)
        }
    }

    // Формирование пакета документов на изделие
    @GetMapping("/list/download-create-package")
    fun downloadCreatePackage(
        response: HttpServletResponse,
        logRecordId: Long,
        protocolVal: Int
    ) {
        val logRecord = presentLogRecordService.read(logRecordId) ?: throw AlertUIException("Предъявление не найдено")
        val logRecordNumber = logRecord.number
        val logRecordMatValueList = logRecord.matValueList.nullIfEmpty() ?: throw AlertUIException("У предъявления отсутствуют материальные ценности")
        val matValue = logRecordMatValueList.firstOrNull() ?: throw AlertUIException("Материальные ценности не найдены")
        val allotment = matValue.allotment ?: throw AlertUIException("Часть поставки не найдена")
        val lot = allotment.lot ?: throw AlertUIException("Поставка не найдена")
        val lotGroup = lot.lotGroup ?: throw AlertUIException("Группа поставок не найдена")
        val orderIndex = lotGroup.orderIndex ?: throw AlertUIException("Пункт ведомости поставки не найден")
        val section = lotGroup.contractSection ?: throw AlertUIException("Секция договора не найдена")
        val product = lotGroup.product ?: throw AlertUIException("Изделие не найдено")
        val examAct = product.examAct
        val suffix = product.suffix
        val familyDecimalNumber = product.familyDecimalNumber
        val registrationDate = logRecord.registrationDate
        //
        val createDayOfMonth = registrationDate?.dayOfMonth
        val createMonthValueShort = registrationDate?.format(DateTimeFormatter.ofPattern("MM"))
        val createMonth = registrationDate.monthInGenitive()
        val createYear = registrationDate?.year
        val createYearShort = registrationDate?.format(DateTimeFormatter.ofPattern("yy"))
        //
        val examActDate = product.examActDate
        val examActDayOfMonth = examActDate?.dayOfMonth
        val examActMonth = examActDate.monthInGenitive()
        val examActYear = examActDate?.year
        //
        val conditionalName = product.conditionalName
        val amountLogRecord = logRecord.matValueList.count { it.presentLogRecord != null }
        //
        val matValueList = logRecordMatValueList.filter { it.presentLogRecord != null }
        val serialNumberList = matValueList.map { it.serialNumber }.toMutableList()
        val lastSerialNumber = serialNumberList.last()?.substring(8)

        val neededSerialNumberList = mutableListOf<String>()
        for((index, element) in serialNumberList.withIndex()) {
            neededSerialNumberList += if (index == 0) element.toString() else element?.substring(8).toString()
        }

        val contractNumber = section.let { if (it.externalNumber != null) "${it.externalNumber}-${it.fullNumber}" else it.fullNumber }
        val sectionCreateDate = section.createDate
        val sectionDayOfMonth = sectionCreateDate?.dayOfMonth
        val sectionMonth = sectionCreateDate.monthInGenitive()
        val sectionYear = sectionCreateDate?.year

        val map = hashMapOf<String, String>()
        map["number"] = "$logRecordNumber/$suffix"
        map["1"] = createDayOfMonth.toString()
        map["2"] = createMonth
        map["3"] = createYear.toString()
        map["productName"] = conditionalName ?: ""
        map["amount"] = amountLogRecord.toString()
        map["serialNumbers"] = neededSerialNumberList.joinToString(", ")
        map["contractNumber"] = contractNumber
        map["4"] = sectionDayOfMonth.toString()
        map["5"] = sectionMonth
        map["6"] = sectionYear.toString()
        map["7"] = orderIndex.toString()
        map["examAct"] = examAct ?: ""
        map["8"] = examActDayOfMonth.toString()
        map["9"] = examActMonth
        map["10"] = examActYear.toString()
        map["familyDecimalNumber"] = familyDecimalNumber ?: ""
        map["11"] = lastSerialNumber ?: ""
        map["12"] = createDayOfMonth.toString()
        map["13"] = createMonth
        map["14"] = createYear.toString()
        map["15"] = createYear.toString()
        map["headEnterprise"] = logRecord.headEnterprise?.user?.userShortName ?: ""
        map["qcdChief"] = logRecord.qcdChief?.user?.userShortName ?: ""
        map["16"] = createYear.toString()
        map["17"] = createYear.toString()
        map["headWarehouse"] = logRecord.headWarehouse?.user?.userShortName ?: ""
        map["18"] = registrationDate?.format(DATE_FORMATTER) ?: ""
        map["19"] = ".${createMonthValueShort}.${createYearShort}"
        map["20"] = ".${createMonthValueShort}.${createYearShort}"
        map["qcdRepresentative"] = logRecord.qcdRepresentative?.user?.userOfficialName ?: ""

        val classpath = when (protocolVal) {
            PROTOCOL_BTM -> "classpath:blank/word/packageProtocolBTM.docx"
            PROTOCOL_BT -> "classpath:blank/word/packageProtocolBT.docx"
            PROTOCOL_BT83 -> "classpath:blank/word/packageProtocolBT83.docx"
            else -> "classpath:blank/word/packageProtocolBT01.docx"
        }

        val resource = resourceLoader.getResource(classpath)
        val fileInputStream = FileInputStream(resource.file)

        // читаем шаблон
        val template = XWPFDocument(fileInputStream)

        // Замена меток в параграфах
        val paragraphList = template.paragraphs
        for (paragraph in paragraphList) WordUtil.replaceTagInText(paragraph, map)

        // Замена меток в строках таблиц
        WordUtil.replaceTableRow(template.tables, map)

        val protocolName = when (protocolVal) {
            PROTOCOL_BTM -> "Протокол БТМ"
            PROTOCOL_BT -> "Протокол БТ"
            PROTOCOL_BT83 -> "Протокол БТ83"
            else -> "Протокол БТ01"
        }

        response.attachDocumentDOCX(template, "Пакет документов ${product.conditionalName} $protocolName")

        logRecord.apply {
            noticeNumber = "$logRecordNumber/$suffix"
            noticeCreateDate = registrationDate
            presentLogRecordService.save(this)
        }
    }

    @DeleteMapping("/list/log-record-info/delete/{id}")
    fun listLogRecordInfoDelete(@PathVariable id: Long) = baseService.exec {
        val matValue = matValueService.read(id) ?: throw AlertUIException("Материальная ценность не найдена")
        val matValueRecordList = matValueService.getAllByPresentLogRecord(matValue.presentLogRecord)
        if (matValueRecordList.size == ONE_INT) throw AlertUIException("Предъявление должно содержать хотя бы одно изделие. Удаление невозможно")
        val matValueList = matValueRecordList.filter { it.internalWaybill != null }
        if (matValueList.isNotEmpty()) {
            val serialNumbers = matValueList.map { it.serialNumber }.joinToString(", ")
            throw AlertUIException("Изделия с серийными номерами: $serialNumbers были добавлены в МСН. Удаление невозможно")
        }
        matValue.presentLogRecord = null
        matValue.serialNumber = null
        matValue.technicalControlDate = null
        matValueService.save(matValue)
    }

    // Упаковка изделий/отмена упаковки изделий
    @PostMapping("/list/pack")
    fun listPack(id: Long, toPack: Boolean) = baseService.exec {
        val logRecord = presentLogRecordService.read(id) ?: throw AlertUIException("Предъявление не найдено")
        val matValueRecordList = matValueService.getAllByPresentLogRecord(logRecord)
        val matValueList = matValueRecordList.filter { it.internalWaybill != null }
        if (!toPack && matValueList.isNotEmpty()) {
            val serialNumbers = matValueList.map { it.serialNumber }.joinToString(", ")
            throw AlertUIException("Изделия с серийными номерами: $serialNumbers были добавлены в МСН. Отмена упаковки невозможна")
        }
        matValueRecordList.forEach {
            it.packedDate = if (toPack) LocalDate.now() else null
            matValueService.save(it)
        }
    }
}