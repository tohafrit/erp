package ru.korundm.controller.view.corp

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.SessionAttributes
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.CompanyDetailService
import ru.korundm.form.CompanyDetailListFilterForm as ListFilterForm
import ru.korundm.form.EditCompanyDetailForm as ListEditForm

private const val LIST_FILTER_FORM_ATTR = "companyDetailListFilterForm"
@ViewController([RequestPath.View.Corp.COMPANY_DETAIL])
@SessionAttributes(names = [LIST_FILTER_FORM_ATTR], types = [ListFilterForm::class])
class CompanyDetailViewCorpController(
    private val companyDetailService: CompanyDetailService
) {

    @ModelAttribute(LIST_FILTER_FORM_ATTR)
    fun companyDetailListFilterForm() = ListFilterForm()

    @GetMapping("/list")
    fun list() = "corp/include/company-detail/list"

    // Редактирование позиции
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val form = ListEditForm()
        id?.let {
            val companyDetail = companyDetailService.read(it)
            form.id = it
            form.name = companyDetail.name
            form.value = companyDetail.value
            form.sort = companyDetail.sort
        }
        model.addAttribute("form", form)
        return "corp/include/company-detail/list/edit"
    }
}