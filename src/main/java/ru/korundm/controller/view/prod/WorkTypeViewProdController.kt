package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BaseService
import ru.korundm.dao.WorkTypeService
import ru.korundm.entity.WorkType
import ru.korundm.exception.AlertUIException
import ru.korundm.form.WorkTypeListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.WORK_TYPE])
class WorkTypeViewProdController(
    private val baseService: BaseService,
    private val workTypeService: WorkTypeService
) {

    @GetMapping("/list")
    fun list() = "prod/include/work-type/list"

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val type = id?.let { workTypeService.read(it) ?: throw AlertUIException("Тип операции не найден") }
        model.addAttribute("form", ListEditForm(id).apply { type?.let {
            version = it.version
            name = it.name
            separateDelivery = it.separateDelivery
            comment = it.comment ?: ""
        } })
        model.addAttribute("canEdit", baseService.existsRelation(WorkType::class, id).not())
        return "prod/include/work-type/list/edit"
    }
}