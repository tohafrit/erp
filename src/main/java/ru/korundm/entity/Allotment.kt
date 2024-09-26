package ru.korundm.entity

import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import ru.korundm.enumeration.PriceKindType
import ru.korundm.enumeration.PriceKindType.*
import ru.korundm.enumeration.ShipmentPermissionKind
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы allotment
 * @author zhestkov_an
 * Date:   17.03.2021
 */
@Entity
@Table(name = "allotment")
data class Allotment(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "allotment")
    @GenericGenerator(name = "allotment", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lot_id")
    var lot: Lot? = null // партия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "launch_product_id")
    var launchProduct: LaunchProduct? = null // изделие в запуске

    @Column(name = "amount")
    var amount = 0L // количество изделий из группы, относящихся к этой партии

    @Column(name = "price")
    var price: BigDecimal = BigDecimal.ZERO // цена изделия

    @Convert(converter = CustomConverter::class)
    @Column(name = "price_kind", nullable = false)
    var priceKind: PriceKindType? = null // тип цены

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "protocol_id")
    var protocol: ProductChargesProtocol? = null // история о затратах на изготовление изделия

    @Column(name = "paid", nullable = false)
    var paid: BigDecimal = BigDecimal.ZERO //

    @Column(name = "shipment_date")
    var shipmentDate: LocalDateTime? = null // дата отгрузки

    @Column(name = "note")
    var note: String? = null // комментарий

    @Column(name = "order_index")
    var orderIndex: Long? = null // для упорядочивания

    @Column(name = "final_price")
    var finalPrice: BigDecimal? = null //

    @Column(name = "shipment_permit_date")
    var shipmentPermitDate: LocalDateTime? = null // дата разрешения на отгрузку

    @Column(name = "intended_shipment_Date")
    var intendedShipmentDate: LocalDateTime? = null //

    @Column(name = "request_id")
    var requestId: Long? = null //

    @Column(name = "transfer_for_wrapping_date")
    var transferForWrappingDate: LocalDateTime? = null // дата передачи на упаковку

    @Column(name = "ready_for_shipment_date")
    var readyForShipmentDate: LocalDateTime? = null // дата готовности к отгрузке

    @Convert(converter = ShipmentPermissionKind.CustomConverter::class)
    @Column(name = "shipment_permission_kind")
    var shipment: ShipmentPermissionKind? = null // тип разрешения на отгрузку

    @Column(name = "advanced_study_date")
    var advancedStudyDate: LocalDateTime? = null //

    @OneToMany(mappedBy = "allotment")
    var matValueList = mutableListOf<MatValue>()

    @ManyToMany(mappedBy = "allotmentList", cascade = [CascadeType.ALL])
    var accountList = mutableListOf<Account>()

    @ManyToMany(mappedBy = "allotmentList")
    var invoiceList = mutableListOf<Invoice>()

    val protocolNumber // номер протокола
        get() = protocol?.protocolNumber

    val protocolDate // дата протокола
        get() = protocol?.protocolDate

    val neededPrice: BigDecimal // нужная цена изделия
        get() = if (priceKind == PRELIMINARY || priceKind == EXPORT) price // Если у allotment тип цены либо "Предварительный" либо "Экспортный":
            else if (priceKind == STATEMENT && lot?.priceKind == FINAL) lot?.protocol?.price ?: BigDecimal.ZERO // Если у allotment тип цены "По ведомости" и у lot тип цены "По протоколу":
            else if (priceKind == FINAL) (if ((protocol?.price ?: BigDecimal.ZERO).toDouble() == .0) protocol?.priceUnpack else protocol?.price) ?: BigDecimal.ZERO // Если у allotment тип цены "По протоколу":
            else lot?.price ?: BigDecimal.ZERO
}