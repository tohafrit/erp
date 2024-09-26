package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Account

interface AccountRepository : JpaRepository<Account, Long> {

    fun findFirstByAccount(account: String): Account?
    fun findAllByCompanyId(id: Long): List<Account>
    fun existsByCompanyIdAndId(companyId: Long, id: Long): Boolean
}