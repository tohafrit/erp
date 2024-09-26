package ru.korundm.controller.action.prod

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.BaseService
import ru.korundm.dao.WorkTypeService
import ru.korundm.entity.WorkType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.form.WorkTypeListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.WORK_TYPE])
class WorkTypeActionProdController(
    private val baseService: BaseService,
    private val workTypeService: WorkTypeService
) {

    // Загрузка списка
    @GetMapping("/list/load")
    fun listLoad(): List<*> {
        data class Item(
            val id: Long?,
            val name: String, // наименование
            val separateDelivery: Boolean, // отдельная поставка
            val comment: String? // комментарий
        )
        return workTypeService.all.map { type -> Item(type.id, type.name, type.separateDelivery, type.comment) }.toList()
    }

    // Сохранение элемента в списке назначений
    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        val formId = form.id
        baseService.exec {
            if (workTypeService.existsByNameAndIdNot(form.name, formId)) response.putError(ListEditForm::name.name, ValidatorMsg.UNIQUE)
            if (response.isValid) {
                val type = formId?.let { workTypeService.read(it) ?: throw AlertUIException("Тип опериции не найден") } ?: WorkType()
                type.apply {
                    version = form.version
                    if (baseService.existsRelation(WorkType::class, id).not()) {
                        name = form.name
                        separateDelivery = form.separateDelivery
                    }
                    comment = form.comment.nullIfBlank()
                    workTypeService.save(this)
                }
            }
        }
        return response
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) {
        if (baseService.existsRelation(WorkType::class, id)) throw AlertUIException("Невозможно удалить тип операции, поскольку он используется в зависимых записях")
        workTypeService.deleteById(id)
    }
}