package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.TechnologicalEntity

interface TechnologicalEntityRepository : JpaRepository<TechnologicalEntity, Long> {

    fun existsByEntityType_Id(entityTypeId: Long): Boolean
}
