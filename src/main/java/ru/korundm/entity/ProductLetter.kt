package ru.korundm.entity

import javax.persistence.*

/**
 * Сущность с описанием таблицы литер
 * Date: 24.10.2021
 */
@Entity
@Table(name = "product_letter")
data class ProductLetter(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Column(name = "name", length = 8, unique = true, nullable = false)
    var name = "" // наименование

    @Column(name = "description", length = 512)
    var description: String? = null // описание
}
