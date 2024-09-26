package ru.korundm.entity

import ru.korundm.entity.listener.FileSystemStorageListener
import ru.korundm.helper.FileSystemStorageContentType
import ru.korundm.helper.FileSystemStorageContentType.Converter
import ru.korundm.helper.FileSystemStorageContentType.DOCUMENT
import ru.korundm.util.FileStorageUtil.contentType
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения информации о файлах в файловой системе
 * @author mazur_ea
 * Date:   28.03.2021
 */
@Entity
@Table(name = "file_system_storage")
@EntityListeners(FileSystemStorageListener::class)
data class FileSystemStorage (
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null
) {

    constructor(fileStorage: FileStorage<*, *>) : this() {
        this.fileStorage = fileStorage
        this.type = contentType(fileStorage.file)
    }

    @Convert(converter = Converter::class)
    @Column(name = "type", nullable = false, updatable = false)
    var type: FileSystemStorageContentType = DOCUMENT // тип
        private set

    @Transient
    lateinit var fileStorage: FileStorage<*, *> // файл хранилища
        private set
}