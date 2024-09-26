package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.PurchasePlan

interface PurchasePlanRepository : JpaRepository<PurchasePlan, Long>