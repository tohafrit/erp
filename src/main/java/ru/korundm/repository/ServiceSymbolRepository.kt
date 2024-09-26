package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ServiceSymbol

interface ServiceSymbolRepository : JpaRepository<ServiceSymbol, Long> {

    fun findFirstByCode(code: String): ServiceSymbol?
    fun findAllByTechnologicalProcess(technicalProcess: Boolean): List<ServiceSymbol>
    fun findAllByOperationCard(operationCard: Boolean): List<ServiceSymbol>
    fun findAllByRouteMap(routeMap: Boolean): List<ServiceSymbol>
}
