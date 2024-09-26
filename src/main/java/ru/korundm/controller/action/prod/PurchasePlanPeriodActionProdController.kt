package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.BaseService
import ru.korundm.dao.PurchasePlanPeriodService
import ru.korundm.entity.PurchasePlanPeriod
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.*
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest

@ActionController([RequestPath.Action.Prod.PURCHASE_PLAN_PERIOD])
class PurchasePlanPeriodActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val purchasePlanPeriodService: PurchasePlanPeriodService
) {

    // Загрузка периодов поставок компонентов
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        data class Item(
            val id: Long?,
            val number: Int?, // номер
            val createDate: LocalDate?, // дата создания
            val firstDate: LocalDate?, // дата начала периода
            val lastDate: LocalDate?, // дата окончания период
            val comment: String? // комментарий
        )
        return TabrOut.instance(input, purchasePlanPeriodService.findTableData(input, form)) { Item(
            it.id,
            it.number,
            it.createDate,
            it.firstDate,
            it.lastDate,
            it.comment
        ) }
    }

    // Сохранение периода поставки компонентов
    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val formId = form.long(ObjAttr.ID)
        val comment = form.stringNotNull(ObjAttr.COMMENT).trim()
        if (comment.length > 256) errors.putError(ObjAttr.COMMENT, ValidatorMsg.RANGE_LENGTH, 0, 256)
        val createDate = form.date(ObjAttr.CREATE_DATE)
        if (createDate == null) errors.putError(ObjAttr.CREATE_DATE, ValidatorMsg.REQUIRED)
        val firstDate = form.date(ObjAttr.FIRST_DATE)
        if (firstDate == null) errors.putError(ObjAttr.FIRST_DATE, ValidatorMsg.REQUIRED)
        val lastDate = form.date(ObjAttr.LAST_DATE)
        if (lastDate == null) errors.putError(ObjAttr.LAST_DATE, ValidatorMsg.REQUIRED)
        if (response.isValid) baseService.exec {
            val period = formId?.let { purchasePlanPeriodService.read(it) ?: throw AlertUIException("Период поставки компонентов не найден") } ?: PurchasePlanPeriod()
            if (formId == null) {
                val lastPeriod = purchasePlanPeriodService.findLastPeriod()
                var periodNumber = lastPeriod?.number ?: BaseConstant.ONE_INT
                period.number = lastPeriod?.let { ++periodNumber } ?: periodNumber
            }
            period.version = form.longNotNull(ObjAttr.VERSION)
            period.createDate = createDate
            period.year = createDate?.year ?: throw AlertUIException("Дата создания не указана")
            period.firstDate = firstDate
            period.lastDate = lastDate
            period.comment = comment.nullIfBlank()
            purchasePlanPeriodService.save(period)
            if (formId == null) response.putAttribute(ObjAttr.ID, period.id)
        }
        return response
    }

    // Удаление
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        val period = purchasePlanPeriodService.read(id)
        if (period == purchasePlanPeriodService.findLastPeriod()) purchasePlanPeriodService.delete(period) else throw AlertUIException("Удалить можно только последний период поставки компонентов")
    }
}