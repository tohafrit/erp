package ru.korundm.form.edit

import org.springframework.web.multipart.MultipartFile
import ru.korundm.constant.ValidatorMsg
import ru.korundm.entity.FileStorage
import ru.korundm.entity.ProductDecipherment
import ru.korundm.helper.SingularFileStorableType
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import ru.korundm.util.FileStorageUtil.validateFile

class EditDeciphermentForm : Validatable {

    var id: Long? = null
    var comment: String = "" // комментарий
    var fileStorage: FileStorage<ProductDecipherment, SingularFileStorableType>? = null // файл
    var file: MultipartFile? = null // файл

    override fun validate(errors: ValidatorErrors) {
        comment = comment.trim()
        if (comment.length  > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
        validateFile(errors, fileStorage, file, ::file.name)
    }
}