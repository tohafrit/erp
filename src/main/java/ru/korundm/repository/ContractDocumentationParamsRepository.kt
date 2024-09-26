package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ContractDocumentationParam

interface ContractDocumentationParamsRepository : JpaRepository<ContractDocumentationParam, Long> {

    fun findAllByContractSectionId(sectionId: Long): List<ContractDocumentationParam>
}