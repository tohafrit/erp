package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.FileStorage
import ru.korundm.helper.FileStorable
import ru.korundm.helper.FileStorageType
import ru.korundm.helper.PluralFileStorableType
import ru.korundm.helper.SingularFileStorableType

interface FileStorageRepository : JpaRepository<FileStorage<*, *>, Long> {

    fun <E : FileStorable<E>, T : SingularFileStorableType> findTopByEntityIdAndTypeOrderByIdDesc(entityId: Long, type: FileStorageType<E, T>): FileStorage<E, T>?

    fun <E : FileStorable<E>, T : PluralFileStorableType> findAllByEntityIdAndType(entityId: Long, type: FileStorageType<E, T>): List<FileStorage<E, T>>
}