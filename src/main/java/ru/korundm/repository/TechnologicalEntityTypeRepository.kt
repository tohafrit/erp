package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.TechnologicalEntityType

interface TechnologicalEntityTypeRepository : JpaRepository<TechnologicalEntityType, Long> {

    fun findFirstByName(name: String): TechnologicalEntityType?
}
