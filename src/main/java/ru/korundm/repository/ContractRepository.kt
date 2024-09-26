package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Contract

interface ContractRepository : JpaRepository<Contract, Long> {

    fun findFirstByOrderByIdDesc(): Contract
}