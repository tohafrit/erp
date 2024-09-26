package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.InternalWaybill

interface InternalWaybillRepository : JpaRepository<InternalWaybill, Long>