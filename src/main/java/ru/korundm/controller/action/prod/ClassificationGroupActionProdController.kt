package ru.korundm.controller.action.prod

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.BaseService
import ru.korundm.dao.ClassificationGroupService
import ru.korundm.entity.ClassificationGroup
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.form.ClassificationGroupListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.CLASSIFICATION_GROUP])
class ClassificationGroupActionProdController(
    private val baseService: BaseService,
    private val classificationGroupService: ClassificationGroupService
) {

    // Загрузка списка
    @GetMapping("/list/load")
    fun listLoad(): List<*> {
        data class Item(
            val id: Long?,
            val number: Int, // номер
            val characteristic: String, // характеристика
            val comment: String? // комментарий
        )
        return classificationGroupService.all.map { gr -> Item(gr.id, gr.number, gr.characteristic, gr.comment) }.toList()
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        val formId = form.id
        val formNumber = form.number.toIntOrNull()
        baseService.exec {
            if (baseService.existsRelation(ClassificationGroup::class, formId)) throw AlertUIException("Невозможно сохранить группу, поскольку она используется в зависимых записях")
            formNumber?.let {
                if (classificationGroupService.existsByNumberAndIdNot(it, formId)) response.putError(ListEditForm::number.name, ValidatorMsg.UNIQUE)
            }
            if (response.isValid) {
                val group = formId?.let { classificationGroupService.read(it) ?: throw AlertUIException("Группа не найдена") } ?: ClassificationGroup()
                group.apply {
                    version = form.version
                    number = formNumber!!
                    characteristic = form.characteristic
                    comment = form.comment.nullIfBlank()
                    classificationGroupService.save(this)
                }
            }
        }
        return response
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) {
        if (baseService.existsRelation(ClassificationGroup::class, id)) throw AlertUIException("Невозможно удалить группу, поскольку она используется в зависимых записях")
        classificationGroupService.deleteById(id)
    }
}