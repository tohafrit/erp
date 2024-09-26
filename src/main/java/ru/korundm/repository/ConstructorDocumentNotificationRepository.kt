package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ConstructorDocumentNotification

interface ConstructorDocumentNotificationRepository: JpaRepository<ConstructorDocumentNotification, Long> {

    fun findAllByApplicabilityProductIdOrderByTermChangeOnDesc(id: Long): List<ConstructorDocumentNotification>
}