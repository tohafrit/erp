package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.PurchasePlanProduct

interface PurchasePlanProductRepository : JpaRepository<PurchasePlanProduct, Long>