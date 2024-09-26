package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения материальных ценностей
 */
@Entity
@Table(name = "mat_value")
data class MatValue(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "serial_number", unique = true, length = 12)
    var serialNumber: String? = null // серийный номер

    @Column(name = "permit_for_presentation_date")
    var permitForPresentationDate: LocalDate? = null // дата отправки письма на производство

    @Column(name = "technical_control_date")
    var technicalControlDate: LocalDate? = null // дата прохождения ОТК

    @Column(name = "packed_date")
    var packedDate: LocalDate? = null // дата упаковки

    @Column(name = "internal_waybill_date")
    var internalWaybillDate: LocalDate? = null // дата МСН

    @Column(name = "permit_for_shipment_date")
    var permitForShipmentDate: LocalDate? = null // дата разрешения на отгрузку

    @Column(name = "shipment_waybill_date")
    var shipmentWaybillDate: LocalDate? = null // дата накладной на отгрузку

    @Column(name = "shipment_checked")
    var shipmentChecked = false // отметка о том, что экземпляр изделия отсутствует в ячейке СГП

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "letter_id", nullable = false)
    var letter: ProductionShipmentLetter? = null // письмо на производство

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "present_log_record_id")
    var presentLogRecord: PresentLogRecord? = null // предъявление

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "allotment_id", nullable = false)
    var allotment: Allotment? = null // часть поставки

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "internal_waybill_id")
    var internalWaybill: InternalWaybill? = null // внутренняя межскладская накладная

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "shipment_waybill_id")
    var shipmentWaybill: ShipmentWaybill? = null // внешнияя накладная на отгрузку

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "storage_cell_id")
    var cell: StorageCell? = null // ячейка хранения на складе

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}