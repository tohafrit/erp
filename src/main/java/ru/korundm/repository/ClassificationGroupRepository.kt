package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.korundm.entity.ClassificationGroup

interface ClassificationGroupRepository : JpaRepository<ClassificationGroup, Long> {

    fun existsByNumber(number: Int): Boolean
    fun existsByNumberAndIdNot(number: Int, id: Long): Boolean
    fun findAllByIdNotIn(idList: List<Long>): List<ClassificationGroup>
    @Query("SELECT id FROM ClassificationGroup")
    fun findAllIdList(): List<Long>
}