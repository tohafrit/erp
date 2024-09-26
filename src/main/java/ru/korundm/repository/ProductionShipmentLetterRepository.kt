package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductionShipmentLetter
import java.time.LocalDate

interface ProductionShipmentLetterRepository : JpaRepository<ProductionShipmentLetter, Long> {

    fun findFirstByCreateDateGreaterThanEqualAndCreateDateLessThanEqualOrderByNumberDesc(dateFrom: LocalDate, dateTo: LocalDate) : ProductionShipmentLetter
}