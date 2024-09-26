package ru.korundm.entity.listener

import org.springframework.beans.factory.annotation.Autowired
import ru.korundm.dao.FileSystemStorageService
import ru.korundm.entity.FileStorage
import ru.korundm.entity.FileSystemStorage
import ru.korundm.helper.AutowireHelper.autowire
import java.time.LocalDateTime
import javax.persistence.PrePersist

/**
 * Слушатель для колбэков при работе с сущностью [FileStorage]
 * @author mazur_ea
 * Date:   19.02.2021
 */
class FileStorageListener {

    @Autowired
    lateinit var fileSystemStorageService: FileSystemStorageService

    @PrePersist
    fun prePersist(fs: FileStorage<*, *>) = fs.run {
        autowire(this@FileStorageListener)
        createdOn = LocalDateTime.now()
        fileSystem = fileSystemStorageService.save(FileSystemStorage(this))
    }
}