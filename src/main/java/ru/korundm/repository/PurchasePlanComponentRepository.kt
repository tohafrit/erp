package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.PurchasePlanComponent

interface PurchasePlanComponentRepository : JpaRepository<PurchasePlanComponent, Long>