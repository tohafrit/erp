package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.ProductLetter

interface ProductLetterRepository : JpaRepository<ProductLetter, Long> {

    fun findFirstByName(name: String): ProductLetter?
}
