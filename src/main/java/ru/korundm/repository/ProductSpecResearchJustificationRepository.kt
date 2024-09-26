package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductSpecResearchJustification

interface ProductSpecResearchJustificationRepository : JpaRepository<ProductSpecResearchJustification, Long> {

    fun findFirstByApprovalDateIsNotNullOrderByApprovalDateDesc(): ProductSpecResearchJustification?
}