package ru.korundm.entity.listener

import ru.korundm.entity.FileSystemStorage
import ru.korundm.util.FileStorageUtil.delete
import ru.korundm.util.FileStorageUtil.write
import javax.persistence.PostPersist
import javax.persistence.PreRemove

/**
 * Слушатель для колбэков при работе с сущностью [FileSystemStorage]
 * @author mazur_ea
 * Date:   28.03.2021
 */
class FileSystemStorageListener {

    @PostPersist
    fun postPersist(fss: FileSystemStorage) = write(fss)

    @PreRemove
    fun preRemove(fss: FileSystemStorage) = delete(fss)
}