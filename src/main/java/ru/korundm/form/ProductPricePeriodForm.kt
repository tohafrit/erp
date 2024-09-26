package ru.korundm.form

import org.springframework.format.annotation.DateTimeFormat
import ru.korundm.constant.BaseConstant.DATE_PATTERN
import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import java.time.LocalDate

class ProductPricePeriodListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var name: String = "", // наименование
    @DateTimeFormat(pattern = DATE_PATTERN)
    var startDate: LocalDate? = null, // дата начала
    var comment: String = "" // комментарий
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        name = name.trim()
        comment = comment.trim()
        if (name.length > 128 || name.isEmpty()) errors.putError(::name.name, ValidatorMsg.RANGE_LENGTH, 1, 128)
        if (startDate == null) errors.putError(::startDate.name, ValidatorMsg.REQUIRED, 1, 128)
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}