package ru.korundm.controller.action.prod

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.FaqService
import ru.korundm.entity.Faq
import ru.korundm.form.edit.EditFaqForm
import ru.korundm.helper.ValidatorResponse

@ActionController([RequestPath.Action.Prod.FAQ])
class FaqActionProdController(
    private val faqService: FaqService
) {

    // Сохранение вопроса
    @PostMapping("/list/edit/save")
    fun listEditSave(form: EditFaqForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            (form.id?.let { faqService.read(it) } ?: Faq()).apply{
                sort = form.sort ?: 0
                question = form.question
                answer = form.answer
                faqService.save(this)
            }
        }
        return response
    }

    // Удаление вопроса
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = faqService.deleteById(id)
}