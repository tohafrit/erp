package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.BasicEconomicIndicator

interface BasicEconomicIndicatorRepository : JpaRepository<BasicEconomicIndicator, Long> {

    fun findFirstByOrderByApprovalDateDesc(): BasicEconomicIndicator?
}