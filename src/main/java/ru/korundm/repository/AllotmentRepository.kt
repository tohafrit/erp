package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Allotment
import ru.korundm.entity.Lot

interface AllotmentRepository : JpaRepository<Allotment, Long> {

    fun countAllByLot(lot: Lot): Int
    fun findDistinctByMatValueListLetterId(letterId: Long): List<Allotment>
    fun findDistinctAllByMatValueListShipmentWaybillId(id: Long): List<Allotment>
}