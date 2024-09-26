package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ValueAddedTax
import java.time.LocalDate

interface ValueAddedTaxRepository : JpaRepository<ValueAddedTax, Long> {

    fun findTop2ByOrderByDateFromDesc(): List<ValueAddedTax>
    fun findTopByDateFromLessThanEqualOrderByDateFromDesc(date: LocalDate): ValueAddedTax?
    fun findTopByOrderByDateFromDesc(): ValueAddedTax?
    fun findDistinctByLotListAllotmentListMatValueListShipmentWaybillId(id: Long): List<ValueAddedTax>
}