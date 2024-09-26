package ru.korundm.form

import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors

class WorkTypeListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var name: String = "", // наименование
    var separateDelivery: Boolean = false, // отдельная поставка
    var comment: String = "" // комментарий
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        name = name.trim()
        comment = comment.trim()
        if (name.isBlank() || name.length > 128) errors.putError(::name.name, ValidatorMsg.RANGE_LENGTH, 1, 128)
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}