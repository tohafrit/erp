package ru.korundm.controller.view.corp

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.SessionAttributes
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.OfficeSupplyService
import ru.korundm.form.EditOfficeSupplyForm as ListEditForm
import ru.korundm.form.OfficeSupplyListFilterForm as ListFilterForm

private const val LIST_FILTER_FORM_ATTR = "officeSupplyListFilterForm"
@ViewController([RequestPath.View.Corp.OFFICE_SUPPLY])
@SessionAttributes(names = [LIST_FILTER_FORM_ATTR], types = [ListFilterForm::class])
class OfficeSupplyViewCorpController(
    private val officeSupplyService: OfficeSupplyService
) {

    @ModelAttribute(LIST_FILTER_FORM_ATTR)
    fun officeSupplyListFilterForm() = ListFilterForm()

    @GetMapping("/list")
    fun list() = "corp/include/office-supply/list"

    // Редактирование канцелярского товара
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val form = ListEditForm()
        id?.let {
            val officeSupply = officeSupplyService.read(it)
            form.id = it
            form.article = officeSupply.article
            form.name = officeSupply.name
            form.active = officeSupply.active
            form.onlySecretaries = officeSupply.onlySecretaries
        }
        model.addAttribute("form", form)
        return "corp/include/office-supply/list/edit"
    }
}