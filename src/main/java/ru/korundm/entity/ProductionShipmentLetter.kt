package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.enumeration.Performer
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения писем на производство
 */
@Entity
@Table(name = "production_shipment_letter")
data class ProductionShipmentLetter(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "number")
    var number = 0 // порядковый номер письма в рамках года

    @Column(name = "year")
    var year = 0 // год

    @Column(name = "create_date", nullable = false)
    var createDate: LocalDate? = null // дата создания

    @Column(name = "send_to_warehouse_date")
    var sendToWarehouseDate: LocalDate? = null // дата отправки на склад

    @Column(name = "send_to_production_date")
    var sendToProductionDate: LocalDate? = null // дата отправки в производство

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @OneToMany(mappedBy = "letter")
    var matValueList = mutableListOf<MatValue>()

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount

    val fullNumber // полный номер письма на производство
        get() = "СЛ-${Performer.OAOKORUND.prefix}-$number"
}