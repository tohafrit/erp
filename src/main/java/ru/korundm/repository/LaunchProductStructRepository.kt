package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.LaunchProductStruct

interface LaunchProductStructRepository : JpaRepository<LaunchProductStruct, Long> {

    fun deleteAllByLaunchProductVersionIdAndLaunchProductVersionApproveDateIsNullAndProductId(versionId: Long, productId: Long)
    fun findAllByLaunchProductVersionIdAndLaunchProductVersionApproveDateIsNullAndProductId(versionId: Long, productId: Long): List<LaunchProductStruct>
    fun deleteAllByLaunchProductId(id: Long)
}