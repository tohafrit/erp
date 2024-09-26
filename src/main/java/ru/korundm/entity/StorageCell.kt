package ru.korundm.entity

import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы для хранения информации о ячейках на складе
 * Date:   08.09.2021
 */
@Entity
@Table(name = "storage_cell")
data class StorageCell(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Column(name = "name", length = 4)
    var name = "" // наименование
}