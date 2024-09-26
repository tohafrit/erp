package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductType

interface ProductTypeRepository : JpaRepository<ProductType, Long>