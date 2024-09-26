package ru.korundm.form

import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import ru.korundm.util.KtCommonUtil.numberInYear

class LaunchListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var year: Int = 0, // год
    var number: Int = 0, // номер
    var comment: String = "" // комментарий
) : Validatable {

    val numberInYear: String // номер в году
        get() = numberInYear(year, number)

    override fun validate(errors: ValidatorErrors) {
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}

class LaunchListAdditionalEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var launchId: Long? = null,
    var launchNumber: Int = 0,
    var year: Int = 0, // год
    var number: Int = 0, // номер
    var comment: String = "" // комментарий
) : Validatable {

    val numberInYear: String // номер в году
        get() = numberInYear(year, number, launchNumber)

    override fun validate(errors: ValidatorErrors) {
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}