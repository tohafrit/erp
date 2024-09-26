package ru.korundm.form

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.multipart.MultipartFile
import ru.korundm.constant.BaseConstant.DATE_PATTERN
import ru.korundm.constant.ValidatorMsg
import ru.korundm.entity.FileStorage
import ru.korundm.entity.ProductSpecResearchJustification
import ru.korundm.helper.SingularFileStorableType
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import ru.korundm.util.FileStorageUtil.validateFile
import java.time.LocalDate

class ProductSpecResearchJustificationListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var name: String = "",
    @DateTimeFormat(pattern = DATE_PATTERN)
    var approvalDate: LocalDate? = null, // дата утверждения
    var createDate: String? = null, // дата создания
    var comment: String = "", // комментарий
    var groupPrice: String = "", // json строка со списком цен групп
    var fileStorage: FileStorage<ProductSpecResearchJustification, SingularFileStorableType>? = null, // файл
    var file: MultipartFile? = null // файл
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        comment = comment.trim()
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
        name = name.trim()
        if (name.isBlank() || name.length > 128) errors.putError(::name.name, ValidatorMsg.RANGE_LENGTH, 1, 128)
        if (approvalDate == null) errors.putError(::approvalDate.name, ValidatorMsg.REQUIRED)
        validateFile(errors, fileStorage, file, ::file.name)
    }

    class GroupPrice(
        val id: Long? = null,
        val number: Int = 0,
        val name: String = "",
        val price: Double = .0
    )
}