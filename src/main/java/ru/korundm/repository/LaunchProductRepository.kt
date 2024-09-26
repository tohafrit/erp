package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.LaunchProduct

interface LaunchProductRepository : JpaRepository<LaunchProduct, Long> {

    fun findAllByVersionApproveDateIsNullAndVersionId(versionId: Long): List<LaunchProduct>
    fun findFirstByLaunchIdAndProductId(launchId: Long, productId: Long): LaunchProduct?
}