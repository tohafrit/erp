package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.ServiceTypeService
import ru.korundm.exception.AlertUIException
import ru.korundm.form.ServiceTypeListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.SERVICE_TYPE])
class ServiceTypeViewProdController(
    private val serviceTypeService: ServiceTypeService
) {

    @GetMapping("/list")
    fun list(model: ModelMap) = "prod/include/service-type/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        return "prod/include/service-type/list/filter"
    }

    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val type = id?.let { serviceTypeService.read(it) ?: throw AlertUIException("Тип услуги не найден") }
        model.addAttribute("form", ListEditForm(id).apply {
            name = type?.name
            prefix = type?.prefix
            comment = type?.comment ?: ""
        })
        return "prod/include/service-type/list/edit"
    }
}