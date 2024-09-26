package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProdPlan

interface ProdPlanRepository : JpaRepository<ProdPlan, Long>