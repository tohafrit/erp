package ru.korundm.controller.action.corp

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.CompanyDetailService
import ru.korundm.entity.CompanyDetail
import ru.korundm.helper.ValidatorResponse
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.TabrResultQuery
import javax.servlet.http.HttpServletRequest
import ru.korundm.form.CompanyDetailListFilterForm as ListFilterForm
import ru.korundm.form.EditCompanyDetailForm as ListEditForm

private const val LIST_FILTER_FORM_ATTR = "companyDetailListFilterForm"
@ActionController([RequestPath.Action.Corp.COMPANY_DETAIL])
@SessionAttributes(names = [LIST_FILTER_FORM_ATTR], types = [ListFilterForm::class])
class CompanyDetailActionCorpController(
    private val companyDetailService: CompanyDetailService,
    private val jsonMapper: ObjectMapper
) {

    // Загрузка реквизитов компании
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        model: ModelMap,
        filterForm: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?, // идентификатор
            val name: String, // название позиции
            val value: String, // значение позиции
            val sort: Int? // сортировка
        )
        val form = jsonMapper.readValue(filterForm, ListFilterForm::class.java)
        model.addAttribute(LIST_FILTER_FORM_ATTR, form)
        val input = TabrIn(request)
        val dataResultQuery: TabrResultQuery<CompanyDetail> = companyDetailService.queryDataByFilterForm(input, form)
        val outList = dataResultQuery.data.map { companyDetail -> Item(
            companyDetail.id,
            companyDetail.name,
            companyDetail.value,
            companyDetail.sort
        ) }.toList()
        val output = TabrOut<Item>()
        output.currentPage = input.page
        output.setLastPage(input.size, dataResultQuery.count)
        output.data = outList
        return output
    }

    // Сохранение элемента в списке назначений
    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            (form.id?.let { companyDetailService.read(it) } ?: CompanyDetail()).apply{
                name = form.name
                value = form.value
                sort = form.sort!!
                companyDetailService.save(this)
            }
        }
        return response
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = companyDetailService.deleteById(id)
}