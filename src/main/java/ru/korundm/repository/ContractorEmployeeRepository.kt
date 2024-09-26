package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ContractorEmployee

interface ContractorEmployeeRepository : JpaRepository<ContractorEmployee, Long>