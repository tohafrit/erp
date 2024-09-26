package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Invoice

interface InvoiceRepository : JpaRepository<Invoice, Long> {

    fun findAllByContractSectionId(sectionId: Long): List <Invoice>
}