package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductLabourIntensityOperation

interface ProductLabourIntensityOperationRepository : JpaRepository<ProductLabourIntensityOperation, Long> {

    fun findAllByEntryId(entryId: Long): List<ProductLabourIntensityOperation>
    fun deleteAllByEntryId(entryId: Long)
    fun findAllByEntryLabourIntensityIdAndEntryProductId(labourIntensityId: Long, productId: Long): List<ProductLabourIntensityOperation>
    fun deleteAllByEntryLabourIntensityId(id: Long)
    fun deleteAllByIdIn(idList: List<Long>)
}