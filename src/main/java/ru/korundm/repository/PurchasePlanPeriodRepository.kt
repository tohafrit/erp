package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.PurchasePlanPeriod

interface PurchasePlanPeriodRepository : JpaRepository<PurchasePlanPeriod, Long>