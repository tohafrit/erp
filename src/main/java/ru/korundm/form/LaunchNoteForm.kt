package ru.korundm.form

import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import ru.korundm.util.KtCommonUtil.numberInYear

class LaunchNoteListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var year: Int = 0, // год
    var number: Int = 0, // номер
    var createDate: String? = null, // дата создания
    var createdBy: String? = null, // создавший пользователь
    var agreementDate: String? = null, // дата согласования
    var agreedBy: String? = null, // согласовавший пользователь
    var comment: String = "" // комментарий
) : Validatable {

    val numberInYear: String // номер в году
        get() = numberInYear(year, number)

    override fun validate(errors: ValidatorErrors) {
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}