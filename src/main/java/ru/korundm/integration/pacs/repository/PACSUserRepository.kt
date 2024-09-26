package ru.korundm.integration.pacs.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.korundm.integration.pacs.entity.PACSUser

@Repository
interface PACSUserRepository : JpaRepository<PACSUser, Long> {

    fun findTopByNameAndFirstNameAndMidName(name: String, firstName: String, midName: String): PACSUser?
}