package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductLabourIntensity

interface ProductLabourIntensityRepository : JpaRepository<ProductLabourIntensity, Long> {

    fun getFirstByEntryListProductIdOrderByEntryListApprovalDateDesc(productId: Long): ProductLabourIntensity?
}