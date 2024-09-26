package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.FileSystemStorage

interface FileSystemStorageRepository : JpaRepository<FileSystemStorage, Long>