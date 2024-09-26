package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.TechnologicalEntity
import ru.korundm.entity.TechnologicalEntityApplicability

interface TechnologicalEntityApplicabilityRepository : JpaRepository<TechnologicalEntityApplicability, Long> {

    fun deleteAllByTechnologicalEntity(technologicalEntity: TechnologicalEntity)
}