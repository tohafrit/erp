package ru.korundm.controller.view.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.dto.DropdownOption
import ru.korundm.enumeration.ProductAcceptType
import ru.korundm.enumeration.SpecialTestType
import ru.korundm.exception.AlertUIException
import ru.korundm.util.KtCommonUtil.safetyReadMutableListValue
import java.time.LocalDate
import ru.korundm.form.PresentLogRecordListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.PRESENT_LOG_RECORD])
class PresentLogRecordViewProdController(
    private val jsonMapper: ObjectMapper,
    private val presentLogRecordService: PresentLogRecordService,
    private val productionShipmentLetterService: ProductionShipmentLetterService,
    private val matValueService: MatValueService,
    private val productService: ProductService,
    private val userService: UserService
) {

    @GetMapping("/list")
    fun list() = "prod/include/present-log-record/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/present-log-record/list/filter"

    // Информация о предъявлении
    @GetMapping("/list/log-record-info")
    fun listLogRecordInfo(
        model: ModelMap,
        id: Long
    ): String {
        val logRecord = presentLogRecordService.read(id)
        val number = logRecord.number
        val registrationDate = logRecord.registrationDate?.format(DATE_FORMATTER)
        model.addAttribute("id", id)
        model.addAttribute("number", number)
        model.addAttribute("registrationDate", registrationDate)
        model.addAttribute("conditionalName", logRecord.matValueList.first().allotment?.lot?.lotGroup?.product?.conditionalName)
        return "prod/include/present-log-record/list/logRecordInfo"
    }

    // Редактирование/добавление предъявления
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val logRecord = id?.let { presentLogRecordService.read(it) ?: throw AlertUIException("Предъявление не найдено") }
        // Находим последнее предъявление
        val lastLogRecord = presentLogRecordService.findLastLogRecord()
        var logRecordNumber = lastLogRecord?.number ?: BaseConstant.ONE_INT

        model.addAttribute("form", ListEditForm(id).apply {
            number = logRecord?.number ?: lastLogRecord?.let { ++logRecordNumber } ?: logRecordNumber
            registrationDate = logRecord?.registrationDate ?: LocalDate.now()
            maxSerialNumberQuantity = logRecord?.let {
                val matValueList = matValueService.getAllByPresentLogRecord(logRecord)
                val matValue = matValueList.first()
                val allotment = matValue.allotment
                val allMatValueCount = matValueService.getAllByAllotment(allotment).count { it.presentLogRecord == null } + matValueList.count()
                allMatValueCount
            }
            val matValue = logRecord?.matValueList?.first()
            val product = matValue?.allotment?.lot?.lotGroup?.product
            allotmentId = matValue?.allotment?.id
            productId = product?.id
            managerId = logRecord?.headEnterprise?.id
            managerId = logRecord?.headEnterprise?.id
            acceptType = logRecord?.presentationType
            specialTestType = logRecord?.specialTestType
            examAct = product?.examAct
            examActDate = product?.examActDate
            familyDecimalNumber = product?.decimalNumber
            suffix = product?.suffix
            comment = logRecord?.comment ?: ""
            conformityStatement = id?.let {
                if (logRecord?.conformityStatementNumber == null) {
                    "[]"
                } else {
                    jsonMapper.writeValueAsString(listOf(ListEditForm.ConformityStatement(
                        logRecord.id,
                        logRecord.conformityStatementNumber,
                        logRecord.conformityStatementCreateDate?.format(DATE_FORMATTER),
                        logRecord.conformityStatementValidity?.format(DATE_FORMATTER),
                        logRecord.conformityStatementTransferDate?.format(DATE_FORMATTER),
                        logRecord.signatoryConformityStatement?.id,
                        logRecord.signatoryConformityStatement?.userOfficialName
                    )))
                }
            } ?: "[]"
            groupSerialNumber = id?.let { jsonMapper.writeValueAsString(matValueService.getAllByPresentLogRecord(logRecord).map { mv ->
                ListEditForm.GroupSerialNumber(
                    mv.id,
                    "Письмо № ${mv.letter?.fullNumber}, Пункт: ${mv.allotment?.lot?.lotGroup?.orderIndex}, Договор: ${mv.allotment?.lot?.lotGroup?.contractSection?.fullNumber}",
                    "Изделие: " + mv.allotment?.lot?.lotGroup?.product?.conditionalName + ", " +
                        "Тип приемки: " + mv.allotment?.lot?.acceptType?.let { type -> ProductAcceptType.getById(type.id).code },
                    mv.serialNumber ?: "",
                    mv.allotment?.id ?: 0
                )
            }) } ?: "[]"
        })
        model.addAttribute("acceptTypeList", ProductAcceptType.values())
        model.addAttribute("specialTestTypeList", SpecialTestType.values())
        return "prod/include/present-log-record/list/edit"
    }

    @GetMapping("/list/edit/letter")
    fun listEditLetter() = "prod/include/present-log-record/list/edit/letter"

    @GetMapping("/list/edit/letter/filter")
    fun listEditLetterFilter() = "prod/include/present-log-record/list/edit/letter/filter"

    @GetMapping("/list/edit/conformity-statement")
    fun listEditConformityStatement(
        model: ModelMap,
        logRecordId: Long?,
        formJson: String
    ): String {
        val isFormJson = formJson == "[]"
        val conformityStatementList = jsonMapper.safetyReadMutableListValue(formJson, ListEditForm.ConformityStatement::class)
        val statementNumber = if (logRecordId == null) {
            if (isFormJson) {
                val lastLogRecord = presentLogRecordService.findLastLogRecord()
                var logRecordNumber = lastLogRecord?.number ?: BaseConstant.ONE_INT
                val number = lastLogRecord?.let { ++logRecordNumber } ?: logRecordNumber
                "${LocalDate.now().year.toString().substring(2)}-$number"
            } else {
                conformityStatementList.firstOrNull()?.conformityStatementNumber
            }
        } else {
            if (isFormJson) {
                val logRecord = presentLogRecordService.read(logRecordId)
                "${logRecord?.registrationDate?.year.toString().substring(2)}-${logRecord?.number}"
            } else {
                conformityStatementList.firstOrNull()?.conformityStatementNumber
            }
        }
        model.addAttribute("userList", userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) })
        model.addAttribute("isFormJson", isFormJson)
        model.addAttribute("transferDate", conformityStatementList.firstOrNull()?.conformityStatementTransferDate ?: "")
        model.addAttribute("number", statementNumber)
        model.addAttribute("createDate", if (isFormJson) LocalDate.now().format(DATE_FORMATTER) else conformityStatementList.firstOrNull()?.conformityStatementCreateDate ?: "")
        model.addAttribute("validityDate", if (isFormJson) LocalDate.now().plusYears(20).format(DATE_FORMATTER) else conformityStatementList.firstOrNull()?.conformityStatementValidity ?: "")
        model.addAttribute("managerId", if (isFormJson) userService.findByUserName("shpilman_vm")?.id else conformityStatementList.firstOrNull()?.managerId ?: "")
        return "prod/include/present-log-record/list/edit/conformityStatement"
    }

    // Информация о письме на производство (пункты письма)
    @GetMapping("/list/edit/letter/info")
    fun listEditLetterInfo(
        model: ModelMap,
        id: Long
    ): String {
        val letter = productionShipmentLetterService.read(id)
        val number = letter.fullNumber
        val creationDate = letter.createDate?.format(DATE_FORMATTER)
        model.addAttribute("letterId", id)
        model.addAttribute("number", number)
        model.addAttribute("creationDate", creationDate)
        return "prod/include/present-log-record/list/edit/letter/letterInfo"
    }

    // Выбор протокола для формирования пакета документов
    @GetMapping("/list/create-package")
    fun listCreatePackage(model: ModelMap, id: Long): String {
        val logRecord = presentLogRecordService.read(id)
        model.addAttribute("logRecordId", logRecord.id)
        return "prod/include/present-log-record/list/createPackage"
    }

    @GetMapping("/list/edit/serial-number")
    fun listEditSerialNumber(
        model: ModelMap,
        logRecordId: Long?,
        productId: Long?
    ): String {
        val logRecord = logRecordId?.let { presentLogRecordService.read(it) }
        val product = productId?.let { productService.read(it) }
        model.addAttribute("logRecordId", logRecordId)
        val conditionalName = logRecord?.let { it.matValueList.firstOrNull()?.allotment?.lot?.lotGroup?.product?.conditionalName } ?: product?.conditionalName
        model.addAttribute("conditionalName", conditionalName)
        return "prod/include/present-log-record/list/edit/serialNumber"
    }
}