package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Launch

interface LaunchRepository : JpaRepository<Launch, Long> {

    fun existsByLaunchId(launchId: Long): Boolean
    fun findAllByLaunchIdOrderByYearDescNumberDesc(launchId: Long?): List<Launch>
    fun findAllByApprovalDateIsNull(): List<Launch>
}