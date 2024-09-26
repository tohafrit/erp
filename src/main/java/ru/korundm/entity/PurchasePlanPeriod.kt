package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*

/**
 * Сущность с описанием таблицы периодов поставок компонентов
 */
@Entity
@Table(name = "purchase_plan_period")
data class PurchasePlanPeriod(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "number", nullable = false)
    var number = 0 // порядковый номер периода в рамках года

    @Column(name = "create_date", nullable = false)
    var createDate: LocalDate? = null // дата создания периода

    @Column(name = "year", nullable = false)
    var year = 0 // год

    @Column(name = "first_date", nullable = false)
    var firstDate: LocalDate? = null // дата начала периода

    @Column(name = "last_date", nullable = false)
    var lastDate: LocalDate? = null // дата окончания периода

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_plan_id")
    var purchasePlan: PurchasePlan? = null // закупочная ведомость

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}