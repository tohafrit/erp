package ru.korundm.entity

import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы кратких технических характеристик изделия (ex. ECOPLAN.T_PRODUCT_TYPE)
 * @author surov_pv
 * Date:   07.02.2019
 */
@Entity
@Table(name = "product_types")
data class ProductType(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Column(name = "name", length = 64, unique = true)
    var name = "" // наименование

    @Column(name = "order_index")
    var orderIndex = 0 // индекс сортировки

    @Column(name = "description", length = 512)
    var description: String? = null // описание
}