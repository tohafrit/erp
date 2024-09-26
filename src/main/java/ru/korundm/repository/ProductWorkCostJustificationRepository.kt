package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductWorkCostJustification

interface ProductWorkCostJustificationRepository : JpaRepository<ProductWorkCostJustification, Long> {

    fun findFirstByApprovalDateIsNotNullOrderByApprovalDateDesc(): ProductWorkCostJustification?
}