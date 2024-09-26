package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.LaunchNote
import ru.korundm.entity.LaunchNoteProduct

interface LaunchNoteProductRepository : JpaRepository<LaunchNoteProduct, Long> {

    fun existsByNote(note: LaunchNote): Boolean
}