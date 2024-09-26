package ru.korundm.form

import org.springframework.web.multipart.MultipartFile
import ru.korundm.constant.ValidatorMsg
import ru.korundm.entity.FileStorage
import ru.korundm.entity.Gratitude
import ru.korundm.helper.ValidatorErrors
import ru.korundm.helper.Validatable
import ru.korundm.helper.SingularFileStorableType
import ru.korundm.util.FileStorageUtil.validateFile
import java.time.LocalDate

class GratitudeEditForm(
    var id: Long? = null, // идентификатор
    var companyId: Long? = null, // идентификатор компании
    var date: LocalDate? = null, // дата
    var fileStorage: FileStorage<Gratitude, SingularFileStorableType>? = null, // файл
    var file: MultipartFile? = null // файл
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        validateFile(errors, fileStorage, file, ::file.name, true)
        if (date == null) {
            errors.putError(::date.name, ValidatorMsg.REQUIRED)
        }
    }
}