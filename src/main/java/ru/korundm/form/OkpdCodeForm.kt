package ru.korundm.form

import ru.korundm.constant.ValidatorMsg
import ru.korundm.entity.OkpdCode
import ru.korundm.entity.OkpdCode.Type.PRODUCT
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors

class OkpdCodeListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var type: OkpdCode.Type = PRODUCT,
    var typeId: Long? = null, // id сущности-типа
    var code: String = "" // код
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        code = code.trim()
        if (code.length > 32 || code.isEmpty()) errors.putError(::code.name, ValidatorMsg.RANGE_LENGTH, 1, 32)
        if (typeId == null) errors.putError(::typeId.name, ValidatorMsg.REQUIRED)
    }
}