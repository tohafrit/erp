package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ServiceType

interface ServiceTypeRepository : JpaRepository<ServiceType, Long>