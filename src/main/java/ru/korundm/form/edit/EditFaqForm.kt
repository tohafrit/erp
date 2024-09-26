package ru.korundm.form.edit

import org.apache.commons.lang3.StringUtils
import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.ValidatorErrors
import ru.korundm.helper.Validatable

class EditFaqForm(
    var id: Long? = null, // идентификатор
    var sort: Int? = 0, // сортировка
    var question: String = "", // вопрос
    var answer: String = "" // ответ
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (StringUtils.isBlank(question)) {
            errors.putError(::question.name, ValidatorMsg.REQUIRED)
        }
        if (StringUtils.isBlank(answer)) {
            errors.putError(::answer.name, ValidatorMsg.REQUIRED)
        }
        val muchThan = 0
        val lessThan = 65535
        sort?.let {
            if (it < muchThan || it > lessThan) {
                errors.putError(::sort.name, "validator.form.fixRange", muchThan, lessThan)
            }
        }
    }
}