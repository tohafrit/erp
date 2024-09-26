package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.TechnologicalEntityStar


interface TechnologicalEntityStarRepository : JpaRepository<TechnologicalEntityStar, Long>
