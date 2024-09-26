package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.RequestPath
import ru.korundm.dao.PurchasePlanPeriodService
import ru.korundm.exception.AlertUIException
import java.time.LocalDate

@ViewController([RequestPath.View.Prod.PURCHASE_PLAN_PERIOD])
class PurchasePlanPeriodViewProdController(
    private val purchasePlanPeriodService: PurchasePlanPeriodService
) {

    @GetMapping("/list")
    fun list() = "prod/include/purchase-plan-period/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/purchase-plan-period/list/filter"

    // Редактирование/добавление периода
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val period = id?.let { purchasePlanPeriodService.read(it) ?: throw AlertUIException("Период поставки компонентов не найден") }
        val lastPeriod = purchasePlanPeriodService.findLastPeriod()
        var periodNumber = lastPeriod?.number ?: ONE_INT
        model.addAttribute("id", period?.id)
        model.addAttribute("version", period?.version)
        model.addAttribute("number", id?.let { period?.number } ?: lastPeriod?.let { ++periodNumber } ?: periodNumber)
        model.addAttribute("createDate", (id?.let { period?.createDate } ?: LocalDate.now()).format(DATE_FORMATTER))
        model.addAttribute("firstDate", (id?.let { period?.firstDate } ?: lastPeriod?.lastDate ?: LocalDate.now()).format(DATE_FORMATTER))
        model.addAttribute("lastDate", id.let { period?.lastDate?.format(DATE_FORMATTER) })
        model.addAttribute("comment", period?.comment)
        return "prod/include/purchase-plan-period/list/edit"
    }
}