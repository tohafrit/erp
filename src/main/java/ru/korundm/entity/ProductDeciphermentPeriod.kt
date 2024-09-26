package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения отчетных периодов расшифровок цены изделия
 * @author mazur_ea
 * Date:   07.07.2021
 */
@Entity
@Table(name = "product_decipherment_period")
data class ProductDeciphermentPeriod(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null // изделие

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "price_period_id", nullable = false)
    var pricePeriod: ProductPricePeriod? = null // период действия цены

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "prev_period_id")
    var prevPeriod: ProductDeciphermentPeriod? = null // предыдущий период расчета цены (отченый период)

    @Column(name = "end_date")
    var endDate: LocalDate? = null // дата окончания

    @Column(name = "price_wo_pack")
    var priceWoPack: Double? = null // цена без упаковки

    @Column(name = "price_pack")
    var pricePack: Double? = null // цена с упаковкой

    @Column(name = "price_pack_research")
    var pricePackResearch: Double? = null // цена с упаковкой и СИ

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}