package ru.korundm.repository.view

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.view.LaunchNoteProductView

interface LaunchNoteProductViewRepository : JpaRepository<LaunchNoteProductView, Long>