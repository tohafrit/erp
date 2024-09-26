package ru.korundm.controller.action.prod

import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.constant.ValidatorMsg.RANGE_LENGTH
import ru.korundm.dao.BaseService
import ru.korundm.dao.TechnologicalEntityService
import ru.korundm.dao.TechnologicalEntityTypeService
import ru.korundm.entity.TechnologicalEntityType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.ValidatorErrors
import ru.korundm.helper.ValidatorResponse

@ActionController([RequestPath.Action.Prod.TECHNOLOGICAL_ENTITY_TYPE])
class TechnologicalEntityTypeActionProdController(
    private val baseService: BaseService,
    private val technologicalEntityTypeService: TechnologicalEntityTypeService,
    private val technologicalEntityService: TechnologicalEntityService
) {

    // Загрузка списка
    @GetMapping("/list/load")
    fun listLoad() = technologicalEntityTypeService.all

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)

        val formId = form.long(ObjAttr.ID)
        val name = form.stringNotNull(ObjAttr.NAME)
        if (name.isBlank() || name.length > 256) errors.putError(ObjAttr.NAME, RANGE_LENGTH, 1, 256)
        val shortName = form.stringNotNull(ObjAttr.SHORT_NAME)
        if (shortName.isBlank() || shortName.length > 64) errors.putError(ObjAttr.SHORT_NAME, RANGE_LENGTH, 1, 64)
        val comment = form.stringNotNull(ObjAttr.COMMENT)
        if (comment.length > 512) errors.putError(ObjAttr.COMMENT, RANGE_LENGTH, 1, 512)
        if (response.isValid) baseService.exec {
            val existsType = technologicalEntityTypeService.findByName(name)
            existsType?.let {
                type -> if (type.id != formId) throw AlertUIException("Наименование должно быть уникальным")
            }
            val stage = formId?.let {
                technologicalEntityTypeService.read(it) ?: throw AlertUIException("Наименование ТД не найдено")
            } ?: TechnologicalEntityType()
            stage.apply {
                this.name = name
                this.shortName = shortName
                multi = form.boolNotNull(ObjAttr.MULTI)
                this.comment = comment
            }
            technologicalEntityTypeService.save(stage)
        }
        return response
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) {
        baseService.exec {
            if (technologicalEntityService.existsByEntityType(id)) throw AlertUIException("Данное наименование связано с элементами технологической документации")
            technologicalEntityTypeService.deleteById(id)
        }
    }
}
