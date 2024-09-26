package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductLabourIntensityEntry

interface ProductLabourIntensityEntryRepository : JpaRepository<ProductLabourIntensityEntry, Long> {

    fun findFirstByLabourIntensityIdAndProductId(labourIntensityId: Long, productId: Long): ProductLabourIntensityEntry?
    fun existsByProductIdAndApprovalDateIsNotNull(productId: Long): Boolean
    fun deleteAllByLabourIntensityId(id: Long)
    fun existsByLabourIntensityIdAndApprovalDateIsNotNull(id: Long): Boolean
    fun deleteAllByIdIn(idList: List<Long>)
}