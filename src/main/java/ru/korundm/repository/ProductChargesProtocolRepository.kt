package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductChargesProtocol

interface ProductChargesProtocolRepository : JpaRepository<ProductChargesProtocol, Long>