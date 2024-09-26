package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BaseService
import ru.korundm.dao.BasicEconomicIndicatorService
import ru.korundm.entity.BasicEconomicIndicator
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import ru.korundm.form.BasicEconomicIndicatorListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.BASIC_ECONOMIC_INDICATOR])
class BasicEconomicIndicatorActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val basicEconomicIndicatorService: BasicEconomicIndicatorService
) {

    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val name: String, // наименование
            val docName: String, // наименование документа обоснования
            val approvalDate: LocalDate, // дата утверждения
            var additionalSalary: Double = .0, // дополнительная заработная плата
            var socialInsurance: Double = .0, // отчисления на соц. страхование
            var overheadCosts: Double = .0, // накладные расходы
            var productionCosts: Double = .0, // общепроизводственные расходы
            var householdExpenses: Double = .0 // общехозяйственные расходы
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, basicEconomicIndicatorService.findTableData(input, form)) { p -> Item(
            p.id,
            p.name,
            p.docName,
            p.approvalDate,
            p.additionalSalary,
            p.socialInsurance,
            p.overheadCosts,
            p.productionCosts,
            p.householdExpenses
        ) }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var indicator: BasicEconomicIndicator
            baseService.exec {
                indicator = formId?.let { basicEconomicIndicatorService.read(it) ?: throw AlertUIException("Показатели не найдены") } ?: BasicEconomicIndicator()
                indicator.apply {
                    version = form.version
                    name = form.name
                    docName = form.docName
                    approvalDate = form.approvalDate ?: LocalDate.now()
                    additionalSalary = form.additionalSalary ?: .0
                    socialInsurance = form.socialInsurance ?: .0
                    overheadCosts = form.overheadCosts ?: .0
                    productionCosts = form.productionCosts ?: .0
                    householdExpenses = form.householdExpenses ?: .0
                    basicEconomicIndicatorService.save(this)
                }
            }
            if (formId == null) response.putAttribute(ObjAttr.ID, indicator.id)
        }
        return response
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec { basicEconomicIndicatorService.deleteById(id) }
}