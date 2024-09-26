package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProdPlanProduct

interface ProdPlanProductRepository : JpaRepository<ProdPlanProduct, Long>