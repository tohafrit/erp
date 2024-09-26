package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.korundm.entity.OkpdCode

interface OkpdCodeRepository : JpaRepository<OkpdCode, Long> {

    fun findFirstByComponentGroupNumber(number: Int): OkpdCode?
    fun findAllByComponentGroupIsNotNull(): List<OkpdCode>
    fun findAllByProductTypeIsNotNull(): List<OkpdCode>
    fun existsByComponentGroupId(id: Long): Boolean
    fun existsByComponentGroupIdAndIdNot(typeId: Long, id: Long): Boolean
    fun existsByProductTypeId(id: Long): Boolean
    fun existsByProductTypeIdAndIdNot(typeId: Long, id: Long): Boolean
    @Query("SELECT DISTINCT componentGroup.id FROM OkpdCode WHERE componentGroup.id IS NOT NULL")
    fun findAllExistsComponentGroupIdList(): List<Long>
    @Query("SELECT DISTINCT productType.id FROM OkpdCode WHERE productType.id IS NOT NULL")
    fun findAllExistsProductTypeIdList(): List<Long>
    fun findFirstByProductTypeId(productTypeId: Long): OkpdCode?
}