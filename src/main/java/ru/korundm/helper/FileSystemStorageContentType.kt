package ru.korundm.helper

import ru.korundm.entity.FileSystemStorage
import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter as PersistenceConverter

/**
 * Типы контента файлов в файловой системе [FileSystemStorage]
 * @author mazur_ea
 * Date:   28.03.2021
 */
enum class FileSystemStorageContentType(
    private val id: Int,
    val directory: String
) : EnumConvertible<Int> {

    DOCUMENT(1, "document"),
    IMAGE(2, "image");

    override fun toValue() = id

    @PersistenceConverter
    class Converter : EnumConverter<FileSystemStorageContentType, Int>(FileSystemStorageContentType::class.java)
}