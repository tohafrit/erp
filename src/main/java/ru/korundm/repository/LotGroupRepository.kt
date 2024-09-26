package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.LotGroup
import ru.korundm.entity.ServiceType

interface LotGroupRepository : JpaRepository<LotGroup, Long> {

    fun existsByServiceType(type: ServiceType): Boolean
}