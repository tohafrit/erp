package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductPriceAnalysisVal

interface ProductPriceAnalysisValRepository : JpaRepository<ProductPriceAnalysisVal, Long> {

    fun deleteAllByPeriodId(periodId: Long)
    fun findFirstByPeriodId(periodId: Long): ProductPriceAnalysisVal?
}