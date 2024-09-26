package ru.korundm.form

import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors

class ClassificationGroupListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var number: String = "", // номер
    var characteristic: String = "", // характеристика
    var comment: String = "" // комментарий
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        characteristic = characteristic.trim()
        comment = comment.trim()
        number = number.trim()
        if (number.isBlank() || number.length > 2) errors.putError(::number.name, ValidatorMsg.RANGE_LENGTH, 1, 2)
        if (characteristic.isBlank() || characteristic.length > 128) errors.putError(::characteristic.name, ValidatorMsg.RANGE_LENGTH, 1, 128)
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}