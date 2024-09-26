package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.TechnologicalEntityTypeService
import ru.korundm.exception.AlertUIException

@ViewController([RequestPath.View.Prod.TECHNOLOGICAL_ENTITY_TYPE])
class TechnologicalEntityTypeViewProdController(
    private val technologicalEntityTypeService: TechnologicalEntityTypeService
) {

    @GetMapping("/list")
    fun list() = "prod/include/technological-entity-type/list"

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val stage = id?.let { technologicalEntityTypeService.read(id) ?: throw AlertUIException("Наименование ТД не найдено") }
        model.addAttribute("id", stage?.id)
        model.addAttribute("name", stage?.name)
        model.addAttribute("shortName", stage?.shortName)
        model.addAttribute("multi", stage?.multi)
        model.addAttribute("comment", stage?.comment)
        return "prod/include/technological-entity-type/list/edit"
    }
}
