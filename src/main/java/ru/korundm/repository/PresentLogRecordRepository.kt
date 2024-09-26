package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.PresentLogRecord

interface PresentLogRecordRepository : JpaRepository<PresentLogRecord, Long> {

    fun findTopByOrderByRegistrationDateDesc(): PresentLogRecord
}