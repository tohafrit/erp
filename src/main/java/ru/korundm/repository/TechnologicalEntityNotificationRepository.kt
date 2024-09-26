package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.TechnologicalEntityNotification


interface TechnologicalEntityNotificationRepository : JpaRepository<TechnologicalEntityNotification, Long>