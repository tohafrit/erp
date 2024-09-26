package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Company
import ru.korundm.enumeration.CompanyTypeEnum

interface CompanyRepository : JpaRepository<Company, Long> {

    fun findByCompanyTypeListTypeIn(type: List<CompanyTypeEnum>): List<Company>
    fun findFirstByName(name: String): Company?
}