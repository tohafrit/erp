package ru.korundm.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.korundm.entity.Product

interface ProductRepository : JpaRepository<Product, Long> {

    fun findAllByConditionalName(name: String): List<Product>
    fun findAllByConditionalNameAndDecimalNumber(name: String, number: String): List<Product>
}