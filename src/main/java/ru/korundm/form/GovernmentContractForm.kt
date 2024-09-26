package ru.korundm.form

import org.springframework.format.annotation.DateTimeFormat
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import java.time.LocalDate

/** Длина идентификатора государственного контракта  */
private const val IDENTIFIER_LENGTH = 25
class GovernmentContractListEditForm(
    var id: Long? = null,
    var identifier: String? = null, // идентификатор
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    var date: LocalDate? = null, // дата заключения
    var comment: String = "", // комментарий
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (identifier?.isBlank() == false) {
            val identifierTrim = identifier!!.trim { it <= ' ' }
            if (identifierTrim.length != IDENTIFIER_LENGTH) {
                errors.putError(::identifier.name, ValidatorMsg.FIX_DIGITS_LENGTH, IDENTIFIER_LENGTH)
            } else if (!identifierTrim.matches(BaseConstant.ONLY_DIGITAL_PATTERN.toRegex())) {
                errors.putError(::identifier.name, "validator.form.digits")
            }
        }
        if (date == null) {
            errors.putError(::date.name, ValidatorMsg.REQUIRED)
        }
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
    }
}