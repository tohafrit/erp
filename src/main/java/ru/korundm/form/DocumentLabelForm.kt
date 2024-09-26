package ru.korundm.form

import ru.korundm.constant.ValidatorMsg
import ru.korundm.entity.User
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors

class DocumentLabelListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var label: String = "", // метка
    var employeePosition: String = "", // должность сотрудника в документе (паспорте изделия)
    var employee: User? = null, // ведущий договор
    var comment: String = "", // комментарий
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        comment = comment.trim()
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
        if(label.isBlank()) errors.putError(::label.name, ValidatorMsg.REQUIRED)
        if (employeePosition.isBlank()) errors.putError(::employeePosition.name, ValidatorMsg.REQUIRED)
        if (employee == null) errors.putError(::employee.name, ValidatorMsg.REQUIRED)
    }
}