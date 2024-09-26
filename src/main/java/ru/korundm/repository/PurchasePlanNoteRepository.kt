package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.PurchasePlanNote

interface PurchasePlanNoteRepository : JpaRepository<PurchasePlanNote, Long>