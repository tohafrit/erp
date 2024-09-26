package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductionArea
import ru.korundm.entity.TechnologicalTool
import ru.korundm.enumeration.TechnologicalToolType

interface TechnologicalToolRepository : JpaRepository<TechnologicalTool, Long> {

    fun findAllByTypeAndProductionAreaListIn(
        type: TechnologicalToolType,
        productionAreaList: List<ProductionArea>
    ): List<TechnologicalTool>
}