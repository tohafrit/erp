package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductSpecReviewPrice

interface ProductSpecReviewPriceRepository : JpaRepository<ProductSpecReviewPrice, Long> {

    fun deleteByJustificationId(id: Long)
    fun findAllByJustificationId(id: Long): List<ProductSpecReviewPrice>
    fun findFirstByJustificationIdAndGroupId(justificationId: Long, groupId: Long): ProductSpecReviewPrice?
    fun deleteAllByIdIn(idList: List<Long>)
}