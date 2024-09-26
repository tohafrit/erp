package ru.korundm.entity

import javax.persistence.*

/**
 * Сущность с описанием таблицы company_details (реквизиты компании)
 * @author zhestkov_an
 * Date:   10.02.2021
 */
@Entity
@Table(name = "company_details")
data class CompanyDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {
    @Column(name = "name", nullable = false)
    var name = "" // название позиции

    @Column(name = "value", nullable = false)
    var value = "" // значение позиции

    @Column(name = "sort", nullable = false)
    var sort = 0 // сортировка
}

@Suppress("unused")
object CompanyDetailM {
    const val ID = "id"
    const val NAME = "name"
    const val VALUE = "value"
    const val SORT = "sort"
}