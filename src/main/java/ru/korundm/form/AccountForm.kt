package ru.korundm.form

import org.apache.commons.lang3.StringUtils
import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors

/** Длина номера расчетного счета  */
private const val ACCOUNT_NUMBER_LENGTH = 20
class AccountListEditForm(
    var id: Long? = null,
    var accountNumber: String? = null, // расчетный счет
    var bankId: Long? = null, // идентификатор банка
    var customerId: Long? = null, // идентификатор заказчика
    var comment: String = "" // комментарий
) : Validatable {
    override fun validate(errors: ValidatorErrors) {
        comment = comment.trim()
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
        if (StringUtils.isBlank(accountNumber) && accountNumber!!.length != ACCOUNT_NUMBER_LENGTH) errors.putError(::accountNumber.name, ValidatorMsg.FIX_DIGITS_LENGTH, ACCOUNT_NUMBER_LENGTH)
        if (bankId == null) errors.putError(::bankId.name, ValidatorMsg.REQUIRED)
        if (customerId == null) errors.putError(::customerId.name, ValidatorMsg.REQUIRED)
    }

}