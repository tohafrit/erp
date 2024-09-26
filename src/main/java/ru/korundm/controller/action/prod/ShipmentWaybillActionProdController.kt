package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.*
import ru.korundm.dto.DropdownOption
import ru.korundm.entity.*
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.*
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import ru.korundm.util.KtCommonUtil.safetyReadMapValue
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest

@ActionController([RequestPath.Action.Prod.SHIPMENT_WAYBILL])
class ShipmentWaybillActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val shipmentWaybillService: ShipmentWaybillService,
    private val matValueService: MatValueService,
    private val contractSectionService: ContractSectionService,
    private val accountService: AccountService,
    private val waybillHistoryService: WaybillHistoryService
) {

    // Загрузка списка накладных
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val result = shipmentWaybillService.findListTableData(input, form)
        return TabrOut.instance(input, result)
    }

    // Удаление
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        val waybill = shipmentWaybillService.read(id) ?: throw AlertUIException("Накладная была удалена")
        if (waybill.shipmentDate != null) throw AlertUIException("Изделия по накладной отгружены. Удаление невозможно")
        val matValueList = matValueService.getAllByShipmentWaybillId(id)
        matValueList.forEach {
            it.shipmentWaybill = null
            it.shipmentWaybillDate = null
        }
        shipmentWaybillService.deleteById(id)
    }

    // Загрузка списка расчетных счетов для договора
    @GetMapping("/list/edit/load-account-data")
    fun listEditLoadAccountData(companyId: Long?) = accountService.getAllByCompanyId(companyId).map { DropdownOption(it.id, it.account) }

    // Загрузка списка договоров
    @GetMapping("/list/edit/contract/load")
    fun listEditContractLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val result = contractSectionService.findShipmentWaybillAddTableData(input, form)
        return TabrOut.instance(input, result)
    }

    // Сохранение накладной
    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val formId = form.long(ObjAttr.ID)
        val sectionId = form.long(ObjAttr.SECTION_ID)
        if (formId == null && sectionId == null) errors.putError(ObjAttr.SECTION_ID, ValidatorMsg.REQUIRED)
        val accountId = form.long(ObjAttr.ACCOUNT_ID)
        val consigneeId = form.long(ObjAttr.CONSIGNEE_ID)
        val transmittalLetter = form.stringNotNull(ObjAttr.TRANSMITTAL_LETTER).trim()
        if (transmittalLetter.length > 128) errors.putError(ObjAttr.TRANSMITTAL_LETTER, ValidatorMsg.RANGE_LENGTH, 0, 128)
        val receiver = form.stringNotNull(ObjAttr.RECEIVER)
        if (receiver.length > 64) errors.putError(ObjAttr.RECEIVER, ValidatorMsg.RANGE_LENGTH, 0, 64)
        val letterOfAttorney = form.stringNotNull(ObjAttr.LETTER_OF_ATTORNEY).trim()
        if (letterOfAttorney.length > 128) errors.putError(ObjAttr.LETTER_OF_ATTORNEY, ValidatorMsg.RANGE_LENGTH, 0, 128)
        val comment = form.stringNotNull(ObjAttr.COMMENT).trim()
        if (comment.length > 256) errors.putError(ObjAttr.COMMENT, ValidatorMsg.RANGE_LENGTH, 0, 256)
        if (response.isValid) baseService.exec {
            val waybill = formId?.let { shipmentWaybillService.read(it) ?: throw AlertUIException("Накладная не найдена") } ?: ShipmentWaybill()
            waybill.ver = form.longNotNull(ObjAttr.VERSION)
            if (formId == null) {
                val now = LocalDate.now()
                waybill.createDate = now
                waybill.year = now.year
                waybill.number = shipmentWaybillService.getLastByYear(waybill.year)?.let { it.number + 1 } ?: 1
                waybill.contractSection = sectionId?.let { contractSectionService.read(it) } ?: throw AlertUIException("Договор не найден")
            }
            if (accountId != null && accountService.existsByCompanyIdAndAccId(waybill.contractSection?.contract?.customer?.id, accountId).not()) throw AlertUIException("Расчетный счет не соответствует плательщику")
            waybill.account = accountId?.let { Account(it) }
            waybill.receiver = receiver.nullIfBlank()
            waybill.consignee = consigneeId?.let { Company(it) }
            waybill.transmittalLetter = transmittalLetter.nullIfBlank()
            waybill.letterOfAttorney = letterOfAttorney.nullIfBlank()
            waybill.comment = comment.nullIfBlank()
            shipmentWaybillService.save(waybill)
            if (formId == null) response.putAttribute(ObjAttr.ID, waybill.id)
        }
        return response
    }

    // Загрузка списка изделий для проверки отгрузки
    @GetMapping("/list/check-shipment/load")
    fun listCheckShipmentLoad(waybillId: Long) = matValueService.findCheckShipmentWaybillTableData(waybillId)

    // Проверка списка отгрузки
    @PostMapping("/list/check-shipment/save")
    fun listCheckShipmentSave(data: String) {
        val dataMap = jsonMapper.safetyReadMapValue(data, Long::class, Boolean::class)
        baseService.exec {
            matValueService.getAllById(dataMap.map { it.key }).forEach { it.shipmentChecked = dataMap[it.id] ?: it.shipmentChecked }
        }
    }

    // Отгрузка накладной
    @PostMapping("/list/shipment/apply")
    fun listShipmentApply(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val id = form.long(ObjAttr.ID)
        val shipmentDate = form.date(ObjAttr.SHIPMENT_DATE)
        if (shipmentDate == null) errors.putError(ObjAttr.SHIPMENT_DATE, ValidatorMsg.REQUIRED)
        val giveUserId = form.long(ObjAttr.GIVE_USER_ID)
        if (giveUserId == null) errors.putError(ObjAttr.GIVE_USER_ID, ValidatorMsg.REQUIRED)
        val permitUserId = form.long(ObjAttr.PERMIT_USER_ID)
        if (permitUserId == null) errors.putError(ObjAttr.PERMIT_USER_ID, ValidatorMsg.REQUIRED)
        val accountantUserId = form.long(ObjAttr.ACCOUNTANT_USER_ID)
        if (accountantUserId == null) errors.putError(ObjAttr.ACCOUNTANT_USER_ID, ValidatorMsg.REQUIRED)
        if (response.isValid) baseService.exec {
            val waybill = id?.let { shipmentWaybillService.read(it) } ?: throw AlertUIException("Накладная не найдена")
            if (waybill.shipmentDate != null) throw AlertUIException("Изделия накладной были отгружены")
            waybill.shipmentDate = shipmentDate
            waybill.giveUser = User(giveUserId)
            waybill.permitUser = User(permitUserId)
            waybill.accountantUser = User(accountantUserId)
            val now = LocalDate.now()
            val mvList = matValueService.getAllByShipmentWaybillId(id)
            if (mvList.isEmpty()) throw AlertUIException("Накладная не содержит ни одного изделия. Отгрузка изделий невозможна")
            mvList.forEach {
                it.shipmentWaybillDate = shipmentDate
                val waybillHistory = WaybillHistory()
                waybillHistory.matValue = it
                waybillHistory.shipment = waybill
                waybillHistory.createDate = now
                waybillHistoryService.save(waybillHistory)
            }
        }
        return response
    }

    // Отмена отгрузки
    @PostMapping("/list/unshipment")
    fun listUnshipment(id: Long) = baseService.exec {
        val waybill = shipmentWaybillService.read(id) ?: throw AlertUIException("Накладная не найдена")
        if (waybill.shipmentDate == null) throw AlertUIException("Отгрузка изделий была отменена")
        waybill.shipmentDate = null
        waybill.giveUser = null
        waybill.permitUser = null
        waybill.accountantUser = null
        matValueService.getAllByShipmentWaybillId(id).forEach { it.shipmentWaybillDate = null }
        waybillHistoryService.deleteAllByShipmentId(id)
    }

    // Загрузка списка изделий для накладной
    @GetMapping("/list/mat-value/load")
    fun listMatValueLoad(waybillId: Long) = matValueService.findShipmentWaybillTableData(waybillId)

    // Удаление изделия из накладной
    @DeleteMapping("/list/mat-value/delete/{id}")
    fun listMatValueDelete(@PathVariable id: Long) = baseService.exec {
        matValueService.read(id)?.apply {
            if (this.shipmentWaybill?.shipmentDate != null) throw AlertUIException("Изделия по накладной отгружены. Удаление невозможно")
            matValueService.getAllByPresentLogRecord(this.presentLogRecord).forEach {
                it.shipmentWaybillDate = null
                it.shipmentWaybill = null
            }
        }
    }

    // Загрузка списка изделий для добавления к накладной
    @GetMapping("/list/mat-value/add/load")
    fun listMatValueAddLoad(
        request: HttpServletRequest,
        filterData: String,
        waybillId: Long
    ): List<*> {
        val input = TabrIn(request)
        val sectionId = shipmentWaybillService.read(waybillId)?.contractSection?.id ?: throw AlertUIException("Договор не найден")
        return matValueService.findShipmentWaybillCandidateTableData(input, sectionId, jsonMapper.readDynamic(filterData))
    }

    // Привязка выбранных изделий к накладной
    @PostMapping("/list/mat-value/add/apply")
    fun listMatValueAddApply(@RequestParam idList: List<Long>, waybillId: Long) {
        if (idList.isEmpty()) throw AlertUIException("Список выбора пуст")
        baseService.exec {
            val waybill = shipmentWaybillService.read(waybillId) ?: throw AlertUIException("Накладная не найдена")
            if (waybill.shipmentDate != null) throw AlertUIException("Изделия по накладной отгружены. Добавление невозможно")
            val matValueList = matValueService.getAllByPresentLogRecordIdList(idList)
            if (matValueList.any { it.letter?.sendToWarehouseDate == null }) throw AlertUIException("Выбранные изделия содержат экземпляры, письма которых не имеют дату отгрузки")
            if (matValueList.any { it.internalWaybill == null }) throw AlertUIException("Выбранные изделия содержат экземляры, которые не пришли на СГП")
            if (matValueList.any { it.shipmentWaybill != null }) throw AlertUIException("Выбранные изделия содержат экземляры, которые были отгружены")
            matValueList.forEach { it.shipmentWaybill = waybill }
        }
    }
}