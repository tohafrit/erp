package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.OnecUser

interface OnecUserRepository : JpaRepository<OnecUser, Long> {

    fun findFirstByOnecId(onecId: String): OnecUser?
    fun findAllByActiveIsTrue(): List<OnecUser>
}