package ru.korundm.entity

import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import ru.korundm.enumeration.PriceKindType
import ru.korundm.enumeration.PriceKindType.*
import ru.korundm.enumeration.ProductAcceptType
import ru.korundm.enumeration.SpecialTestType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы lots
 * @author zhestkov_an
 * Date:   02.03.2021
 */
@Entity
@Table(name = "lots")
data class Lot(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "lot")
    @GenericGenerator(name = "lot", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lot_group_id", nullable = false)
    var lotGroup: LotGroup? = null // позиция ведомости поставки

    @Column(name = "amount")
    var amount = 0L // кол-во экземпляров изделия для позиции ведомости

    @Column(name = "delivery_date", nullable = false)
    var deliveryDate: LocalDate? = null // дата поставки

    @Column(name = "price")
    var price: BigDecimal = BigDecimal.ZERO // цена экземпляра изделия

    @Convert(converter = CustomConverter::class)
    @Column(name = "price_kind")
    var priceKind: PriceKindType? = null // тип цены

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "protocol_id")
    var protocol: ProductChargesProtocol? = null // история о затратах на изготовление изделия

    @Convert(converter = ProductAcceptType.CustomConverter::class)
    @Column(name = "accept_type")
    var acceptType: ProductAcceptType? = null // тип приёмки изделий

    @Convert(converter = SpecialTestType.CustomConverter::class)
    @Column(name = "special_test_type")
    var specialTestType: SpecialTestType? = null // тип спецпроверки

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "vat_id", nullable = false)
    var vat: ValueAddedTax? = null // ставка НДС

    @Column(name = "return_date")
    var returnDate: LocalDateTime? = null

    @Column(name = "contract_stage_id")
    var contractStageID: Long? = null

    @Column(name = "stage_name")
    var stageName: String? = null

    @OneToMany(mappedBy = "lot")
    var allotmentList = mutableListOf<Allotment>() // партии

    val neededPrice: BigDecimal // нужная цена изделия
        get() = if (priceKind == PRELIMINARY || priceKind == EXPORT) price
            else if (priceKind == FINAL) (if ((protocol?.price ?: BigDecimal.ZERO).toDouble() == .0) protocol?.priceUnpack else protocol?.price) ?: BigDecimal.ZERO
            else BigDecimal.ZERO

    val protocolNumber // номер протокола
        get() = protocol?.protocolNumber

    val protocolDate // дата протокола
        get() = protocol?.protocolDate

    val lotGroupCost: BigDecimal // общая стоимость изделий в lot-e без учета НДС
        get() = neededPrice * amount.toBigDecimal()

    val specialTestTypeCode
        get() = specialTestType?.code ?: SpecialTestType.WITHOUT_CHECKS.code
}