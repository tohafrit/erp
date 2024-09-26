package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.ProductLetterService
import ru.korundm.dao.TechnologicalEntityTypeService
import ru.korundm.exception.AlertUIException

@ViewController([RequestPath.View.Prod.PRODUCT_LETTER])
class ProductLetterViewProdController(
    private val productLetterService: ProductLetterService
) {

    @GetMapping("/list")
    fun list() = "prod/include/product-letter/list"

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val productLetter = id?.let { productLetterService.read(id) ?: throw AlertUIException("Литера не найдена") }
        model.addAttribute("id", productLetter?.id)
        model.addAttribute("name", productLetter?.name)
        model.addAttribute("description", productLetter?.description)
        return "prod/include/product-letter/list/edit"
    }
}
