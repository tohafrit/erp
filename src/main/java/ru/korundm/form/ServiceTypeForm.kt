package ru.korundm.form

import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors

class ServiceTypeListEditForm(
    var id: Long? = null,
    var name: String? = null, // наименование
    var prefix: String? = null, // префикс
    var comment: String = "" // комментарий
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (name?.isBlank() == true) errors.putError(::name.name, ValidatorMsg.REQUIRED)
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}