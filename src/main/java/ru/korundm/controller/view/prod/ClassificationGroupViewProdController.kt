package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BaseService
import ru.korundm.dao.ClassificationGroupService
import ru.korundm.entity.ClassificationGroup
import ru.korundm.exception.AlertUIException
import ru.korundm.form.ClassificationGroupListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.CLASSIFICATION_GROUP])
class ClassificationGroupViewProdController(
    private val baseService: BaseService,
    private val classificationGroupService: ClassificationGroupService
) {

    @GetMapping("/list")
    fun list() = "prod/include/classification-group/list"

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        if (baseService.existsRelation(ClassificationGroup::class, id)) throw AlertUIException("Невозможно редактировать группу, поскольку она используется в зависимых записях")
        val group = id?.let { classificationGroupService.read(it) ?: throw AlertUIException("Группа не найдена") }
        model.addAttribute("form", ListEditForm(id).apply { group?.let {
            version = it.version
            number = it.number.toString()
            characteristic = it.characteristic
            comment = it.comment ?: ""
        } })
        return "prod/include/classification-group/list/edit"
    }
}