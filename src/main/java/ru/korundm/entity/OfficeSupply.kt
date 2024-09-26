package ru.korundm.entity

import javax.persistence.*

/**
 * Сущность с описанием таблицы office_supplies
 * @author zhestkov_an
 * Date:   16.02.2021
 */
@Entity
@Table(name = "office_supplies")
data class OfficeSupply(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {
    @Column(name = "article", nullable = false)
    var article = "" // артикул

    @Column(name = "name", nullable = false)
    var name = "" // наименование

    @Column(name = "active", nullable = false)
    var active = false // активность

    @Column(name = "only_secretaries", nullable = false)
    var onlySecretaries = false // только для секретарей
}

@Suppress("unused")
object OfficeSupplyM {
    const val ID = "id"
    const val ARTICLE = "article"
    const val NAME = "name"
    const val ACTIVE = "active"
    const val ONLY_SECRETARIES = "onlySecretaries"
}