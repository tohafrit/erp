package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.WaybillHistory

interface WaybillHistoryRepository : JpaRepository<WaybillHistory, Long> {

    fun deleteAllByShipmentId(id: Long)
    fun deleteAllByInternalId(id: Long)
    fun findAllByMatValueIdOrderByCreateDateAsc(id: Long): List<WaybillHistory>
}