package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductSpecReviewJustification

interface ProductSpecReviewJustificationRepository : JpaRepository<ProductSpecReviewJustification, Long> {

    fun findFirstByApprovalDateIsNotNullOrderByApprovalDateDesc(): ProductSpecReviewJustification?
}