package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ContractSection
import ru.korundm.entity.Lot

interface LotRepository : JpaRepository<Lot, Long> {

    fun findAllByLotGroupContractSection(contractSection: ContractSection): List<Lot>
    fun existsByProtocolPriceProtocolId(id: Long): Boolean
}