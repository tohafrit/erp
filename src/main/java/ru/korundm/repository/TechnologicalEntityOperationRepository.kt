package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.TechnologicalEntityOperation

interface TechnologicalEntityOperationRepository : JpaRepository<TechnologicalEntityOperation, Long>
