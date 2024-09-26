package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.TechnologicalEntity
import ru.korundm.entity.TechnologicalEntityReconciliation


interface TechnologicalEntityReconciliationRepository : JpaRepository<TechnologicalEntityReconciliation, Long> {

    fun findTopByTechnologicalEntityOrderByIdDesc(technologicalEntity: TechnologicalEntity): TechnologicalEntityReconciliation?
}
