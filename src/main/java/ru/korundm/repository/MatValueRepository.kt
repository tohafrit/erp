package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.*

interface MatValueRepository : JpaRepository<MatValue, Long> {

    fun findBySerialNumber(serialNumber: String): MatValue?
    fun findAllByLetter(letter: ProductionShipmentLetter): List<MatValue>
    fun findAllByPresentLogRecord(logRecord: PresentLogRecord): List<MatValue>
    fun findAllByAllotment(allotment: Allotment): List<MatValue>
    fun findAllByInternalWaybillId(id: Long): List<MatValue>
    fun findAllByShipmentWaybillId(id: Long): List<MatValue>
    fun findAllByPresentLogRecordIdIn(idList: List<Long>): List<MatValue>
    fun findAllByAllotmentLotLotGroupProduct(product: Product): List<MatValue>
    fun existsByInternalWaybillId(id: Long): Boolean
    fun existsByShipmentWaybillId(id: Long): Boolean
}