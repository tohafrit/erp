package ru.korundm.integration.pacs.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.korundm.integration.pacs.entity.PACSAccessPoint

@Repository
interface PACSAccessPointRepository : JpaRepository<PACSAccessPoint, Long> {

    fun findAllByDoorIndex(doorIndex: Int)
}