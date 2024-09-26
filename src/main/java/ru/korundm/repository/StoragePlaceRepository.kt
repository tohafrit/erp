package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.StoragePlace

interface StoragePlaceRepository : JpaRepository<StoragePlace, Long>