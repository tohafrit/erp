package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.korundm.entity.ProductDecipherment

interface ProductDeciphermentRepository : JpaRepository<ProductDecipherment, Long> {

    fun findFirstByPeriodIdAndTypeId(periodId: Long, typeId: Long): ProductDecipherment?
    fun existsByPeriodIdAndTypeIdIn(periodId: Long, typeIdList: List<Long>): Boolean
    @Query("SELECT DISTINCT type.id FROM ProductDecipherment WHERE period.id = ?1")
    fun findTypeIdListByPeriodId(periodId: Long): List<Long>
    fun findAllByPeriodIdOrderByTypeSortAsc(periodId: Long): List<ProductDecipherment>
    fun findAllByApprovedAndPeriodIdOrderByTypeSortAsc(approved: Boolean, periodId: Long): List<ProductDecipherment>
}