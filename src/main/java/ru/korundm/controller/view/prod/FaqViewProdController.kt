package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.FaqService
import ru.korundm.form.edit.EditFaqForm

@ViewController([RequestPath.View.Prod.FAQ])
class FaqViewProdController(
    private val faqService: FaqService
) {

    // Список вопросов
    @GetMapping("/list")
    fun list(model: ModelMap): String {
        model["faqList"] = faqService.all
        return "prod/include/faq/list"
    }

    // Редактирование вопроса
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val form = EditFaqForm()
        id?.let {
            val faq = faqService.read(it)
            form.id = it
            form.sort = faq.sort
            form.question = faq.question
            form.answer = faq.answer
        }
        model["form"] = form
        return "prod/include/faq/list/edit"
    }
}