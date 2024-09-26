package ru.korundm.controller.action.corp

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.OfficeSupplyService
import ru.korundm.entity.OfficeSupply
import ru.korundm.helper.ValidatorResponse
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.TabrResultQuery
import javax.servlet.http.HttpServletRequest
import ru.korundm.form.EditOfficeSupplyForm as ListEditForm
import ru.korundm.form.OfficeSupplyListFilterForm as ListFilterForm

private const val LIST_FILTER_FORM_ATTR = "officeSupplyListFilterForm"
@ActionController([RequestPath.Action.Corp.OFFICE_SUPPLY])
@SessionAttributes(names = [LIST_FILTER_FORM_ATTR], types = [ListFilterForm::class])
class OfficeSupplyActionCorpController(
    private val officeSupplyService: OfficeSupplyService,
    private val jsonMapper: ObjectMapper
) {

    // Загрузка канцелярских товаров
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterForm: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val article: String, // артикул
            val name: String, // наименование
            val active: Boolean, // активность
            val onlySecretaries: Boolean, // только для секретарей
            val articleContainDash: Boolean // содержит ли артикул тире
        )
        val form = jsonMapper.readValue(filterForm, ListFilterForm::class.java)
        model.addAttribute(LIST_FILTER_FORM_ATTR, form)
        val input = TabrIn(request)
        val dataResultQuery: TabrResultQuery<OfficeSupply> = officeSupplyService.queryDataByFilterForm(input, form)
        val outList = dataResultQuery.data.map { officeSupply -> Item(
            officeSupply.id,
            officeSupply.article,
            officeSupply.name,
            officeSupply.active,
            officeSupply.onlySecretaries,
            officeSupply.article.contains('-')
        ) }
        val output = TabrOut<Item>()
        output.currentPage = input.page
        output.setLastPage(input.size, dataResultQuery.count)
        output.data = outList
        return output
    }

    // Сохранение канцелярского товара
    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            (form.id?.let { officeSupplyService.read(it) } ?: OfficeSupply()).apply{
                article = form.article
                name = form.name
                active = form.active
                onlySecretaries = form.onlySecretaries
                officeSupplyService.save(this)
            }
        }
        return response
    }

    // Удаление канцелярского товара
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = officeSupplyService.deleteById(id)
}