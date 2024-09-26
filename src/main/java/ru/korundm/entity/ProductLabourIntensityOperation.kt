package ru.korundm.entity

import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения значений трудоемкости по каждому виду работы для вхождений трудоемкости изготовления и проверки изделия
 */
@Entity
@Table(name = "product_labour_intensity_operation")
data class ProductLabourIntensityOperation(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "entry_id", nullable = false)
    var entry: ProductLabourIntensityEntry? = null // вхождение трудоемкости изделия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    var operation: WorkType? = null // вид работы

    @Column(name = "value", precision = 10, scale = 2)
    var value = .0 // значение
}