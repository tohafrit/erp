package ru.korundm.controller.action.prod

import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.BaseService
import ru.korundm.dao.LaborProtectionInstructionService
import ru.korundm.entity.LaborProtectionInstruction
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.DynamicObject
import ru.korundm.helper.ValidatorErrors
import ru.korundm.helper.ValidatorResponse

@ActionController([RequestPath.Action.Prod.LABOR_PROTECTION_INSTRUCTION])
class LaborProtectionInstructionActionProdController(
    private val baseService: BaseService,
    private val laborProtectionInstructionService: LaborProtectionInstructionService
) {

    // Загрузка списка
    @GetMapping("/list/load")
    fun listLoad(): List<*> {
        data class Item(
            val id: Long?,
            val name: String,
            val comment: String?
        )
        return laborProtectionInstructionService.all.map { Item(
            it.id,
            "ИОТ №${it.number} ${it.name}",
            it.comment
        ) }
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)

        val formId = form.long(ObjAttr.ID)
        val name = form.stringNotNull(ObjAttr.NAME)
        if (name.isBlank()) errors.putError(ObjAttr.NAME, ValidatorMsg.REQUIRED)
        val number = form.stringNotNull(ObjAttr.NUMBER)
        if (number.isBlank()) errors.putError(ObjAttr.NUMBER, ValidatorMsg.REQUIRED)
        val comment = form.string(ObjAttr.COMMENT)
        if (response.isValid) baseService.exec {
            val laborProtectionInstruction = formId?.let { laborProtectionInstructionService.read(it) ?: throw AlertUIException("ИОТ не найден") } ?: LaborProtectionInstruction()
            laborProtectionInstruction.name = name
            laborProtectionInstruction.number = number
            laborProtectionInstruction.comment = comment
            laborProtectionInstructionService.save(laborProtectionInstruction)
        }
        return response
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = laborProtectionInstructionService.deleteById(id)
}
