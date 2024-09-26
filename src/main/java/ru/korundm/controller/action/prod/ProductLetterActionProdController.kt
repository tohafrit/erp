package ru.korundm.controller.action.prod

import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.constant.ValidatorMsg.RANGE_LENGTH
import ru.korundm.dao.*
import ru.korundm.entity.ProductLetter
import ru.korundm.entity.TechnologicalEntityType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.ValidatorErrors
import ru.korundm.helper.ValidatorResponse

@ActionController([RequestPath.Action.Prod.PRODUCT_LETTER])
class ProductLetterActionProdController(
    private val baseService: BaseService,
    private val productLetterService: ProductLetterService
) {

    // Загрузка списка
    @GetMapping("/list/load")
    fun listLoad() = productLetterService.all

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)

        val formId = form.long(ObjAttr.ID)
        val name = form.stringNotNull(ObjAttr.NAME)
        if (name.isBlank() || name.length > 256) errors.putError(ObjAttr.NAME, RANGE_LENGTH, 1, 256)
        val description = form.stringNotNull(ObjAttr.DESCRIPTION)
        if (description.length > 512) errors.putError(ObjAttr.DESCRIPTION, RANGE_LENGTH, 1, 512)
        if (response.isValid) baseService.exec {
            val existsType = productLetterService.findByName(name)
            existsType?.let {
                type -> if (type.id != formId) throw AlertUIException("Наименование должно быть уникальным")
            }
            val stage = formId?.let {
                productLetterService.read(it) ?: throw AlertUIException("Литера не найдена")
            } ?: ProductLetter()
            stage.apply {
                this.name = name
                this.description = description
            }
            productLetterService.save(stage)
        }
        return response
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = productLetterService.deleteById(id)
}
