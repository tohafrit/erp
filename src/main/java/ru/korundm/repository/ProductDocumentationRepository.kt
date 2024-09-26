package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductDocumentation

interface ProductDocumentationRepository : JpaRepository<ProductDocumentation, Long>