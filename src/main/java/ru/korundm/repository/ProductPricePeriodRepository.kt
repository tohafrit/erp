package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductPricePeriod

interface ProductPricePeriodRepository : JpaRepository<ProductPricePeriod, Long>