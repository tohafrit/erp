package ru.korundm.form

import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.ValidatorErrors
import ru.korundm.helper.Validatable

class OfficeSupplyListFilterForm(
    var id: Long? = null, // идентификатор
    val article: String = "", // артикул
    val name: String = "", // наименование
    val active: Boolean? = null, // активность
    val onlySecretaries: Boolean? = null // только для секретарей
)

class EditOfficeSupplyForm(
    var id: Long? = null, // идентификатор
    var article: String = "", // артикул
    var name: String = "", // наименование
    var active: Boolean = true, // активность
    var onlySecretaries: Boolean = false // только для секретарей
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (article.isBlank() || article.length > 64) {
            errors.putError(::article.name, ValidatorMsg.RANGE_LENGTH, 1, 64)
        }
        if (name.isBlank() || name.length > 512) {
            errors.putError(::name.name, ValidatorMsg.RANGE_LENGTH, 1, 512)
        }
    }
}