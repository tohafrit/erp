package ru.korundm.entity

import org.springframework.web.multipart.MultipartFile
import ru.korundm.entity.listener.FileStorageListener
import ru.korundm.helper.FileStorable
import ru.korundm.helper.FileStorableType
import ru.korundm.helper.FileStorageType
import ru.korundm.helper.FileStorageType.Converter
import ru.korundm.util.FileStorageUtil.encodeURLHash
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранимых файлов загрузки
 * @author mazur_ea
 * Date:   21.01.2019
 */
@Entity
@Table(name = "file_storage")
@EntityListeners(FileStorageListener::class)
data class FileStorage<E : FileStorable<E>, T : FileStorableType>(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long? = null
) {

    constructor(entity: E, type: FileStorageType<E, T>, file: MultipartFile) : this() {
        this.entityId = entity.storableId() ?: throw IllegalArgumentException("${::entityId.name} is null")
        this.type = type
        this.name = file.originalFilename ?: file.name
        this.file = if (file.isEmpty) throw IllegalArgumentException("${this::file.name} is empty") else file
   }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "file_system_id", nullable = false, updatable = false)
    var fileSystem: FileSystemStorage? = null // файл в файловой системе

    @Column(name = "entity_id", nullable = false, updatable = false)
    var entityId: Long? = null // сущность
        private set

    @Convert(converter = Converter::class)
    @Column(name = "type", nullable = false, updatable = false)
    var type: FileStorageType<*, *>? = null // тип
        private set

    @Column(name = "name", length = 256, nullable = false, updatable = false)
    var name: String = "" // имя
        private set

    @Column(name = "create_date", nullable = false, updatable = false)
    var createdOn: LocalDateTime? = null // создан

    @Transient
    lateinit var file: MultipartFile // файл сохранения
        private set

    val urlHash // хеш URL
        get() = encodeURLHash(this.id)
}