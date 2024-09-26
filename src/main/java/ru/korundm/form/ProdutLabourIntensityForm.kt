package ru.korundm.form

import org.springframework.format.annotation.DateTimeFormat
import ru.korundm.constant.BaseConstant.DATE_PATTERN
import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import java.time.LocalDate

class ProductLabourIntensityListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var name: String = "", // наименование
    var approved: String? = null, // кол-во утвержденных общих трудоемкостей
    @DateTimeFormat(pattern = DATE_PATTERN)
    var createDate: LocalDate? = null, // дата добавления
    var createdBy: String? = null, // кем создан
    var comment: String = "" // комментарий
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        name = name.trim()
        if (name.isEmpty() || name.length > 128) errors.putError(::name.name, ValidatorMsg.RANGE_LENGTH, 1, 128)
        if (id == null && createDate == null) errors.putError(::createDate.name, ValidatorMsg.REQUIRED)
        comment = comment.trim()
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}

class ProductLabourIntensityDetailEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var comment: String = "" // комментарий
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        comment = comment.trim()
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}