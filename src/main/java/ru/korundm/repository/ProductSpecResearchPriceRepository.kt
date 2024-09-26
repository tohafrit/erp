package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductSpecResearchPrice

interface ProductSpecResearchPriceRepository : JpaRepository<ProductSpecResearchPrice, Long> {

    fun deleteByJustificationId(id: Long)
    fun findAllByJustificationId(id: Long): List<ProductSpecResearchPrice>
    fun findFirstByJustificationIdAndGroupId(justificationId: Long, groupId: Long): ProductSpecResearchPrice?
    fun deleteAllByIdIn(idList: List<Long>)
}