package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Bank

interface BankRepository : JpaRepository<Bank, Long> {

    override fun deleteAll()
}