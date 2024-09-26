package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ShipmentWaybill

interface ShipmentWaybillRepository : JpaRepository<ShipmentWaybill, Long>