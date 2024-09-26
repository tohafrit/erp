package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Company
import ru.korundm.entity.CompanyType
import ru.korundm.enumeration.CompanyTypeEnum

interface CompanyTypeRepository : JpaRepository<CompanyType, Long> {

    fun findFirstByType(type: CompanyTypeEnum): CompanyType
}