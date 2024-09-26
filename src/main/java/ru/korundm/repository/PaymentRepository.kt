package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Payment

interface PaymentRepository : JpaRepository<Payment, Long> {

    fun findAllByContractSectionId(id: Long): List<Payment>
    fun findAllByInvoiceId(id: Long): List<Payment>
}