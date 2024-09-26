package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Functionality
import ru.korundm.entity.TechnologicalEntity

interface FunctionalityRepository : JpaRepository<Functionality, Long> {

    fun getFirstByTechnologicalEntity_IdAndServiceSymbol_Code(entityId: Long, code: String)
    fun findAllByTechnologicalEntity(entity: TechnologicalEntity): List<Functionality>
    fun removeAllByTechnologicalEntityAndParentAndServiceSymbol_Code(entity: TechnologicalEntity, functionality: Functionality?, code: String)
    fun findAllByTechnologicalEntity_IdAndParentIsNullOrderBySort(entityId: Long): List<Functionality>
    fun findAllByTechnologicalEntity_IdAndServiceSymbol_CodeOrderBySortDesc(entityId: Long, code: String): List<Functionality>
    fun existsByParent_IdAndServiceSymbol_Code(parentId: Long, code: String): Boolean
}
