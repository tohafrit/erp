package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения периодов цен изделия
 */
@Entity
@Table(name = "product_price_period")
data class ProductPricePeriod(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "name", length = 128)
    var name: String = "" // наименование

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDate? = null // дата начала

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}