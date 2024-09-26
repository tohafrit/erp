package ru.korundm.entity

import ru.korundm.enumeration.ProductDeciphermentTypeEnum
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения типов расшифровок цены изделия
 * @author mazur_ea
 * Date:   07.07.2021
 */
@Entity
@Table(name = "product_decipherment_type")
data class ProductDeciphermentType(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Column(name = "sort")
    var sort = 0 // сортировка

    @Column(name = "name", length = 128, unique = true)
    var name = "" // наименование

    val enum
        get() = ProductDeciphermentTypeEnum.getById(id)
}