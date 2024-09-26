package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.TechnologicalEntityAgreed

interface TechnologicalEntityAgreedRepository : JpaRepository<TechnologicalEntityAgreed, Long>
