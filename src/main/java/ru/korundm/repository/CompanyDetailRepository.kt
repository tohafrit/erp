package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.CompanyDetail

interface CompanyDetailRepository : JpaRepository<CompanyDetail, Long>