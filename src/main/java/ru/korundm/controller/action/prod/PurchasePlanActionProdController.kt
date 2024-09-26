package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.BaseService
import ru.korundm.dao.LaunchService
import ru.korundm.dao.PurchasePlanService
import ru.korundm.dao.UserService
import ru.korundm.entity.PurchasePlan
import ru.korundm.enumeration.BomVersionType
import ru.korundm.enumeration.ReserveUseType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.*
import ru.korundm.util.KtCommonUtil.getUser
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@ActionController([RequestPath.Action.Prod.PURCHASE_PLAN])
class PurchasePlanActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val purchasePlanService: PurchasePlanService,
    private val userService: UserService,
    private val launchService: LaunchService
) {

    // Загрузка закупочной ведомости
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        data class Item(
            val id: Long?,
            val name: String?, // наименование
            val createDate: LocalDate?, // дата создания
            val createdBy: String?, // кто создал
            val numberInYear: String?, // номер запуска
            val version: String?, // версия ЗС
            val date: LocalDate?, // дата отсечки
            val reserve: String?, // учет запасов
            val approvalDate: LocalDate?, // дата утверждения
            val approvedBy: String?, // кто утвердил
            val canApprove: Boolean, // возможность упаковки изделий
            val comment: String? // комментарий
        )
        return TabrOut.instance(input, purchasePlanService.findTableData(input, form)) { Item(
            it.id,
            it.name,
            it.createDate,
            it.createdBy?.userOfficialName,
            it.launch?.numberInYear,
            it.bomVersionType?.property,
            it.onTheWayLastDate,
            it.reserveUseType?.property,
            it.approvalDate,
            it.approvedBy?.userOfficialName,
            it.approvalDate == null,
            it.comment
        ) }
    }

    // Сохранение периода поставки компонентов
    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val formId = form.long(ObjAttr.ID)
        val name = form.stringNotNull(ObjAttr.NAME).trim()
        if (name.isBlank() || name.length > 128) errors.putError(ObjAttr.NAME, ValidatorMsg.RANGE_LENGTH, 0, 128)
        val createDate = form.date(ObjAttr.CREATE_DATE)
        if (createDate == null) errors.putError(ObjAttr.CREATE_DATE, ValidatorMsg.REQUIRED)
        val createdById = form.long(ObjAttr.CREATED_BY_ID)
        if (createdById == null) errors.putError(ObjAttr.CREATED_BY_ID, ValidatorMsg.REQUIRED)
        val launchId = form.long(ObjAttr.LAUNCH_ID)
        if (launchId == null) errors.putError(ObjAttr.LAUNCH_ID, ValidatorMsg.REQUIRED)
        val versionTypeId = form.long(ObjAttr.VERSION_TYPE_ID)
        if (versionTypeId == null) errors.putError(ObjAttr.VERSION_TYPE_ID, ValidatorMsg.REQUIRED)
        val reserveUseTypeId = form.long(ObjAttr.RESERVE_USE_TYPE_ID)
        if (reserveUseTypeId == null) errors.putError(ObjAttr.RESERVE_USE_TYPE_ID, ValidatorMsg.REQUIRED)
        val comment = form.stringNotNull(ObjAttr.COMMENT).trim()
        if (comment.length > 256) errors.putError(ObjAttr.COMMENT, ValidatorMsg.RANGE_LENGTH, 0, 256)
        if (response.isValid) baseService.exec {
            val purchase = formId?.let { purchasePlanService.read(it) ?: throw AlertUIException("Закупочная ведомость не найдена") } ?: PurchasePlan()
            purchase.version = form.longNotNull(ObjAttr.VERSION)
            purchase.name = name
            purchase.createDate = createDate
            purchase.createdBy = createdById?.let { userService.read(it) }
            purchase.launch = launchId?.let { launchService.read(it) }
            purchase.previousLaunch = form.long(ObjAttr.PREVIOUS_LAUNCH_ID)?.let { launchService.read(it) }
            purchase.bomVersionType = versionTypeId?.let { BomVersionType.getById(it) }
            purchase.onTheWayLastDate = form.date(ObjAttr.ON_THE_WAY_LAST_DATE)
            purchase.reserveUseType = reserveUseTypeId?.let { ReserveUseType.getById(it) }
            purchase.comment = comment.nullIfBlank()
            purchasePlanService.save(purchase)
            if (formId == null) response.putAttribute(ObjAttr.ID, purchase.id)
        }
        return response
    }

    @PostMapping("/list/approve")
    fun listApprove(
        session: HttpSession,
        id: Long,
        toApprove: Boolean
    ) = baseService.exec {
        val user = session.getUser()
        val purchase = purchasePlanService.read(id) ?: throw AlertUIException("Закупочная ведомость не найдена")
        if (toApprove && purchase.approvalDate != null) throw AlertUIException("Закупочная ведомость была утверждена")
        if (!toApprove && purchase.approvalDate == null) throw AlertUIException("Утверждение было снято с Закупочной ведомости")
        purchase.approvalDate = if (toApprove) LocalDate.now() else null
        purchase.approvedBy = if (toApprove) user else null
        purchasePlanService.save(purchase)
    }

    // Удаление
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        val purchase = purchasePlanService.read(id)
        if (purchase.approvalDate == null && purchase.approvedBy == null) purchasePlanService.delete(purchase) else throw AlertUIException("Закупочная ведомость утверждена. Удаление невозможно")
    }
}