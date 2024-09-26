package ru.korundm.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы для хранения истории перемещения экземпляров изделий
 */
@Entity
@Table(name = "waybill_history")
data class WaybillHistory(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mat_value_id", nullable = false)
    var matValue: MatValue? = null // экземпляр изделия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "internal_id")
    var internal: InternalWaybill? = null // внутренняя МСН

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "shipment_id")
    var shipment: ShipmentWaybill? = null // внешняя накладная на отгрузку

    @Column(name = "create_date")
    var createDate: LocalDate = LocalDate.MIN // дата создания
}