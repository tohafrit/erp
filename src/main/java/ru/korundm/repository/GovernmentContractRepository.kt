package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.GovernmentContract

interface GovernmentContractRepository : JpaRepository<GovernmentContract, Long> {

    fun findFirstByIdentifier(identifier: String): GovernmentContract?
}