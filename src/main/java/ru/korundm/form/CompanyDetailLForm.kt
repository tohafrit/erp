package ru.korundm.form

import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors

class CompanyDetailListFilterForm(
    var id: Long? = null, // идентификатор
    val name: String = "", // название позиции
    val value: String = "", // значение позиции
    val sort: Int? = null // сортировка
)

class EditCompanyDetailForm(
    var id: Long? = null, // идентификатор
    var name: String = "", // название позиции
    var value: String = "", // значение позиции
    var sort: Int? = null // сортировка
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (name.isBlank() || name.length > 256) {
            errors.putError(::name.name, ValidatorMsg.RANGE_LENGTH, 1, 256)
        }
        if (value.isBlank() || value.length > 256) {
            errors.putError(::value.name, ValidatorMsg.RANGE_LENGTH, 1, 256)
        }
        if (sort == null) {
            errors.putError(::sort.name, ValidatorMsg.REQUIRED)
        }
    }
}