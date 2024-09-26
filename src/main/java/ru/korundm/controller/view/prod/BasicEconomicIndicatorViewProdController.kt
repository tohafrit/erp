package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BasicEconomicIndicatorService
import ru.korundm.exception.AlertUIException
import java.time.LocalDate
import ru.korundm.form.BasicEconomicIndicatorListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.BASIC_ECONOMIC_INDICATOR])
class BasicEconomicIndicatorViewProdController(
    private val basicEconomicIndicatorService: BasicEconomicIndicatorService
) {

    @GetMapping("/list")
    fun list() = "prod/include/basic-economic-indicator/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/basic-economic-indicator/list/filter"

    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val indicator = id?.let { basicEconomicIndicatorService.read(it) ?: throw AlertUIException("Показатели не найдены") }
        model.addAttribute("form", ListEditForm(id).apply {
            val nowDate = LocalDate.now();
            version = indicator?.version ?: 0L
            name = if (id == null) "Плановые эк. показатели на ${nowDate.year}г." else indicator?.name ?: ""
            docName = indicator?.docName ?: ""
            approvalDate = if (id == null) nowDate else indicator?.approvalDate
            additionalSalary = indicator?.additionalSalary ?: .0
            socialInsurance = indicator?.socialInsurance ?: .0
            overheadCosts = indicator?.overheadCosts ?: .0
            productionCosts = indicator?.productionCosts ?: .0
            householdExpenses = indicator?.householdExpenses ?: .0
        })
        return "prod/include/basic-economic-indicator/list/edit"
    }
}