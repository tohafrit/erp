package ru.korundm.dao

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import ru.korundm.entity.FileSystemStorage
import ru.korundm.repository.FileSystemStorageRepository

interface FileSystemStorageService {
    fun save(obj: FileSystemStorage): FileSystemStorage
    fun delete(obj: FileSystemStorage)
}

@Service
@Transactional
class FileSystemStorageServiceImpl(
    private val fileSystemStorageRepository: FileSystemStorageRepository
) : FileSystemStorageService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun save(obj: FileSystemStorage) = fileSystemStorageRepository.save(obj)

    override fun delete(obj: FileSystemStorage) = fileSystemStorageRepository.delete(obj)
}