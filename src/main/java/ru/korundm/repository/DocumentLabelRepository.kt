package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.DocumentLabel

interface DocumentLabelRepository : JpaRepository<DocumentLabel, Long> {

    fun findFirstByLabel(label: String): DocumentLabel
    fun findFirstByUserId(userId: Long): DocumentLabel
}