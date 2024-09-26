package ru.korundm.integration.pacs.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.korundm.integration.pacs.entity.PACSPassage
import ru.korundm.integration.pacs.entity.PACSUser
import java.time.LocalDateTime

@Repository
interface PACSPassageRepository : JpaRepository<PACSPassage, Long> {

    fun findTopByUserAndTimeAfterOrderByTimeDesc(user: PACSUser, time: LocalDateTime): PACSPassage?
}