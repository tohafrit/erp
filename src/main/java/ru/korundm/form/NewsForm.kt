package ru.korundm.form

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.multipart.MultipartFile
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.ValidatorMsg
import ru.korundm.entity.FileStorage
import ru.korundm.entity.News
import ru.korundm.helper.*
import ru.korundm.util.CommonUtil.dateTimeFromMoreThenTo
import ru.korundm.util.FileStorageUtil
import java.time.LocalDateTime

class NewsListFilterForm(
    val title: String = "", // заголовок
    val previewText: String = "", // анонс
    val detailText: String = "", // содержимое
    @JsonFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    val dateCreatedFrom: LocalDateTime? = null, // дата создания с
    @JsonFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    val dateCreatedTo: LocalDateTime? = null, // дата создания по
    val topStatus: Boolean = false // закреплённая
)

class NewsEditForm : Validatable {
    var id: Long? = null
    var title: String = "" // заголовок
    var previewText: String = "" // текст анонса
    var detailText: String = "" // текст содержимого
    var topStatus: Boolean = false // статус закрепления новости вверху списка
    var typeId: Long? = null // идентификатор типа новости

    @DateTimeFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    var dateEventFrom: LocalDateTime? = null // дата события с
    @DateTimeFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    var dateEventTo: LocalDateTime? = null // дата события по
    @DateTimeFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    var dateActiveFrom: LocalDateTime? = null // дата активности с
    @DateTimeFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    var dateActiveTo: LocalDateTime? = null // дата активности по

    var fileStorage: FileStorage<News, SingularFileStorableType>? = null // файл
    var file: MultipartFile? = null // файл

    override fun validate(errors: ValidatorErrors) {
        if (title.isBlank() || title.length > 255) {
            errors.putError(::title.name, ValidatorMsg.RANGE_LENGTH, 1, 255)
        }
        if (previewText.isBlank() || previewText.length > 255) {
            errors.putError(::previewText.name, ValidatorMsg.RANGE_LENGTH, 1, 255)
        }
        if (dateTimeFromMoreThenTo(dateActiveFrom, dateActiveTo)) {
            errors.putError(::dateActiveTo.name, ValidatorMsg.DATE_MUST_BE_MORE)
        }
        if (dateTimeFromMoreThenTo(dateEventFrom, dateEventTo)) {
            errors.putError(::dateEventTo.name, ValidatorMsg.DATE_MUST_BE_MORE)
        }
        FileStorageUtil.validateFile(errors, fileStorage, file, ::file.name)
        if (file?.isEmpty == false && file?.let { FileStorageUtil.contentType(it) } != FileSystemStorageContentType.IMAGE) {
            errors.putError(::file.name, "validator.form.isNotImage")
        }
    }
}