package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.BaseService
import ru.korundm.dao.InternalWaybillService
import ru.korundm.dao.MatValueService
import ru.korundm.dao.WaybillHistoryService
import ru.korundm.entity.InternalWaybill
import ru.korundm.entity.StoragePlace
import ru.korundm.entity.User
import ru.korundm.entity.WaybillHistory
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.*
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest

@ActionController([RequestPath.Action.Prod.INTERNAL_WAYBILL])
class InternalWaybillActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val internalWaybillService: InternalWaybillService,
    private val matValueService: MatValueService,
    private val waybillHistoryService: WaybillHistoryService
) {

    // Загрузка списка накладных
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        selectedId: Long?,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val result = internalWaybillService.findListTableData(input, selectedId, form)
        return TabrOut.instance(input, result)
    }

    // Удаление
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        val matValueList = matValueService.getAllByInternalWaybillId(id)
        if (matValueList.any { it.shipmentWaybill != null }) throw AlertUIException("МСН содержит изделия, добавленные в накладные на отгрузку. Удаление невозможно")
        matValueList.forEach {
            it.internalWaybill = null
            it.internalWaybillDate = null
        }
        internalWaybillService.deleteById(id)
    }

    // Сохранение накладной
    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val formId = form.long(ObjAttr.ID)
        val comment = form.stringNotNull(ObjAttr.COMMENT).trim()
        if (comment.length > 256) errors.putError(ObjAttr.COMMENT, ValidatorMsg.RANGE_LENGTH, 0, 256)
        val storagePlaceId = form.long(ObjAttr.STORAGE_PLACE_ID)
        if (formId == null && storagePlaceId == null) errors.putError(ObjAttr.STORAGE_PLACE_ID, ValidatorMsg.REQUIRED)
        if (response.isValid) baseService.exec {
            val matValueList = matValueService.getAllByInternalWaybillId(formId)
            if (matValueList.any { it.shipmentWaybill != null }) throw AlertUIException("МСН содержит изделия, добавленные в накладные на отгрузку. Сохранение невозможно")
            val waybill = formId?.let { internalWaybillService.read(it) ?: throw AlertUIException("Накладная не найдена") } ?: InternalWaybill()
            if (formId == null) {
                val now = LocalDate.now()
                waybill.createDate = now
                waybill.year = now.year
                waybill.number = internalWaybillService.getLastByYear(waybill.year)?.let { it.number + 1 } ?: 1
                waybill.storagePlace = StoragePlace(storagePlaceId)
            }
            waybill.ver = form.longNotNull(ObjAttr.VERSION)
            waybill.comment = comment.nullIfBlank()
            internalWaybillService.save(waybill)
            if (formId == null) response.putAttribute(ObjAttr.ID, waybill.id)
        }
        return response
    }

    // Принятие накладной
    @PostMapping("/list/accept/apply")
    fun listAcceptApply(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val id = form.long(ObjAttr.ID)
        val acceptDate = form.date(ObjAttr.ACCEPT_DATE)
        if (acceptDate == null) errors.putError(ObjAttr.ACCEPT_DATE, ValidatorMsg.REQUIRED)
        val giveUserId = form.long(ObjAttr.GIVE_USER_ID)
        if (giveUserId == null) errors.putError(ObjAttr.GIVE_USER_ID, ValidatorMsg.REQUIRED)
        val acceptUserId = form.long(ObjAttr.ACCEPT_USER_ID)
        if (acceptUserId == null) errors.putError(ObjAttr.ACCEPT_USER_ID, ValidatorMsg.REQUIRED)
        if (response.isValid) baseService.exec {
            val waybill = id?.let { internalWaybillService.read(it) } ?: throw AlertUIException("Накладная не найдена")
            if (waybill.acceptDate != null) throw AlertUIException("Накладная была принята")
            waybill.acceptDate = acceptDate
            waybill.giveUser = User(giveUserId)
            waybill.acceptUser = User(acceptUserId)
            val now = LocalDate.now()
            val mvList = matValueService.getAllByInternalWaybillId(id)
            if (mvList.isEmpty()) throw AlertUIException("МСН не содержит ни одного изделия. Принятие изделий невозможно")
            mvList.forEach {
                it.internalWaybillDate = acceptDate
                val waybillHistory = WaybillHistory()
                waybillHistory.matValue = it
                waybillHistory.internal = waybill
                waybillHistory.createDate = now
                waybillHistoryService.save(waybillHistory)
            }
        }
        return response
    }

    // Принятие накладной
    @PostMapping("/list/unaccept")
    fun listUnaccept(id: Long) = baseService.exec {
        val waybill = internalWaybillService.read(id) ?: throw AlertUIException("Накладная не найдена")
        if (waybill.acceptDate == null) throw AlertUIException("Принятие было снято с накладной")
        val matValueList = matValueService.getAllByInternalWaybillId(id)
        if (matValueList.any { it.shipmentWaybill != null }) throw AlertUIException("МСН содержит изделия, добавленные в накладные на отгрузку. Отмена принятия невозможна")
        waybill.acceptDate = null
        waybill.giveUser = null
        waybill.acceptUser = null
        matValueList.forEach { it.internalWaybillDate = null }
        waybillHistoryService.deleteAllByInternalId(id)
    }

    // Загрузка списка изделий для накладной
    @GetMapping("/list/mat-value/load")
    fun listMatValueLoad(
        request: HttpServletRequest,
        waybillId: Long
    ): TabrOut<*> {
        val input = TabrIn(request)
        val result = matValueService.findInternalWaybillTableData(input, waybillId)
        return TabrOut.instance(input, result)
    }

    // Удаление изделий из накладной
    @DeleteMapping("/list/mat-value/delete/{id}")
    fun listMatValueDelete(@PathVariable id: Long) = baseService.exec {
        matValueService.read(id)?.apply {
            if (this.internalWaybill?.acceptDate != null) throw AlertUIException("МСН принята. Удаление изделий невозможно")
            matValueService.getAllByPresentLogRecord(this.presentLogRecord).forEach {
                it.internalWaybillDate = null
                it.internalWaybill = null
            }
        }
    }

    // Загрузка списка изделий для добавления к накладной
    @GetMapping("/list/mat-value/add/load")
    fun listMatValueAddLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val result = matValueService.findInternalWaybillCandidateTableData(input, form)
        return TabrOut.instance(input, result)
    }

    // Привязка выбранных изделий к накладной
    @PostMapping("/list/mat-value/add/apply")
    fun listMatValueAddApply(@RequestParam idList: List<Long>, waybillId: Long) {
        if (idList.isEmpty()) throw AlertUIException("Список предъявлений пуст")
        baseService.exec {
            val waybill = internalWaybillService.read(waybillId) ?: throw AlertUIException("Накладная не найдена")
            if (waybill.acceptDate != null) throw AlertUIException("МСН принята. Добавление изделий невозможно")
            val matValueList = matValueService.getAllByPresentLogRecordIdList(idList)
            if (matValueList.any { it.internalWaybill != null }) throw AlertUIException("Список содержит предъявления, которые были добавленны к накладной")
            if (matValueList.any { it.packedDate == null }) throw AlertUIException("Список предъявлений содержит неупакованные изделия")
            if (matValueList.any { it.technicalControlDate == null }) throw AlertUIException("Список предъявлений содержит изделия не прошедшие ОТК")
            matValueList.forEach { it.internalWaybill = waybill }
        }
    }
}