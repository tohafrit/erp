package ru.korundm.entity

import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения стоимости работ по изготовлению изделия
 */
@Entity
@Table(name = "product_work_cost")
data class ProductWorkCost(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "justification_id", nullable = false)
    var justification: ProductWorkCostJustification? = null // обоснование

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "work_type_id", nullable = false)
    var workType: WorkType? = null // тип работы

    @Column(name = "cost", precision = 10, scale = 2)
    var cost = .0 // стоимость
}