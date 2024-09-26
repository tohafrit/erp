package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Subdivision

interface SubdivisionRepository : JpaRepository<Subdivision, Long> {

    fun findFirstByOnecId(onecId: String): Subdivision?
    fun findAllByFormedIsTrueAndDisbandedIsFalseAndParentIdOrderBySort(parentId: String): List<Subdivision>
}