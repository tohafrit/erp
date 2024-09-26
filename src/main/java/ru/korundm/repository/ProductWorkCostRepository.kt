package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductWorkCost

interface ProductWorkCostRepository : JpaRepository<ProductWorkCost, Long> {

    fun findAllByJustificationId(id: Long): List<ProductWorkCost>
    fun deleteByJustificationId(id: Long)
}