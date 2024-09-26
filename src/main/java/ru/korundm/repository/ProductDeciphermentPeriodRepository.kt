package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductDeciphermentPeriod

interface ProductDeciphermentPeriodRepository : JpaRepository<ProductDeciphermentPeriod, Long> {

    fun existsByProductIdAndPricePeriodId(productId: Long, pricePeriodId: Long): Boolean
    fun findFirstByProductIdOrderByPricePeriodStartDateDesc(productId: Long): ProductDeciphermentPeriod?
    fun existsByPricePeriodId(pricePeriodId: Long): Boolean
}