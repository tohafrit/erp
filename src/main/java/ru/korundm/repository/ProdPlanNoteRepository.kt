package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProdPlanNote

interface ProdPlanNoteRepository : JpaRepository<ProdPlanNote, Long>