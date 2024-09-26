package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.StorageCell

interface StorageCellRepository : JpaRepository<StorageCell, Long>