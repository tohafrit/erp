package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Gratitude

interface GratitudeRepository : JpaRepository<Gratitude, Long>