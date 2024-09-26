package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductDeciphermentAttrVal
import ru.korundm.enumeration.ProductDeciphermentAttr

interface ProductDeciphermentAttrValRepository : JpaRepository<ProductDeciphermentAttrVal, Long> {

    fun findAllByAttributeInAndDeciphermentTypeIdInAndDeciphermentPeriodId(
        attrList: List<ProductDeciphermentAttr>,
        typeIdList: List<Long>,
        periodId: Long
    ): List<ProductDeciphermentAttrVal>

    fun findAllByAttributeAndDeciphermentTypeIdInAndDeciphermentPeriodId(
        attr: ProductDeciphermentAttr,
        typeIdList: List<Long>,
        periodId: Long
    ): List<ProductDeciphermentAttrVal>

    fun findAllByAttributeAndDeciphermentTypeIdAndDeciphermentPeriodId(
        attr: ProductDeciphermentAttr,
        typeId: Long,
        periodId: Long
    ): List<ProductDeciphermentAttrVal>

    fun findFirstByDeciphermentIdAndAttribute(
        deciphermentId: Long,
        attr: ProductDeciphermentAttr
    ): ProductDeciphermentAttrVal?

    fun existsByProductWorkCostJustificationId(id: Long): Boolean
    fun existsByProductSpecReviewJustificationId(id: Long): Boolean
    fun existsByProductSpecResearchJustificationId(id: Long): Boolean
    fun existsByProductLabourIntensityId(id: Long): Boolean
    fun existsByDeciphermentPeriodProductIdAndProductLabourIntensityId(productId: Long, labourIntensityId: Long): Boolean
    fun deleteAllByDeciphermentId(deciphermentId: Long)
}