package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.LaborProtectionInstructionService
import ru.korundm.exception.AlertUIException

@ViewController([RequestPath.View.Prod.LABOR_PROTECTION_INSTRUCTION])
class LaborProtectionInstructionViewProdController(
    private val laborProtectionInstructionService: LaborProtectionInstructionService
) {

    @GetMapping("/list")
    fun list() = "prod/include/labor-protection-instruction/list"

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val laborProtectionInstruction = id?.let { laborProtectionInstructionService.read(id) ?: throw AlertUIException("ИОТ не найдена") }
        model.addAttribute("id", laborProtectionInstruction?.id)
        model.addAttribute("name", laborProtectionInstruction?.name)
        model.addAttribute("number", laborProtectionInstruction?.number)
        model.addAttribute("comment", laborProtectionInstruction?.comment)
        return "prod/include/labor-protection-instruction/list/edit"
    }
}
