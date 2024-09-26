package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Contract
import ru.korundm.entity.ContractSection

interface ContractSectionRepository : JpaRepository<ContractSection, Long> {

    fun findFirstByIdentifier(identifier: String): ContractSection
    fun findFirstByContractOrderByIdDesc(contract: Contract): ContractSection
    fun countAllByContract(contract: Contract): Int
    fun findFirstByContract(contract: Contract): ContractSection
}