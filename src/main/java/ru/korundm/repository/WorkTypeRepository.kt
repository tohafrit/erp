package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.korundm.entity.WorkType

interface WorkTypeRepository : JpaRepository<WorkType, Long> {

    fun existsByName(name: String): Boolean
    fun findFirstByName(name: String): WorkType?
    fun existsByNameAndIdNot(name: String, id: Long): Boolean
    fun findAllByIdNotIn(idList: List<Long>): List<WorkType>
    @Query("SELECT id FROM WorkType")
    fun findAllIdList(): List<Long>
}