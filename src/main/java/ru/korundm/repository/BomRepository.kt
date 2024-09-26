package ru.korundm.repository

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.korundm.entity.Bom

interface BomRepository : JpaRepository<Bom, Long> {

    fun findAllByProductIdAndMajorNot(productId: Long, major: Int, sort: Sort): List<Bom>
    @Query("SELECT MAX(b.descriptor) FROM Bom b")
    fun maxDescriptor(): Long?
    fun findFirstByProductIdOrderByMajorDescMinorDescModificationDesc(productId: Long): Bom?
}