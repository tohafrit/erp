package ru.korundm.entity

import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы product_charges_protocol
 * @author zhestkov_an
 * Date:   12.03.2021
 */
@Entity
@Table(name = "product_charges_protocol")
data class ProductChargesProtocol(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "productChargesProtocol")
    @GenericGenerator(name = "productChargesProtocol", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "document_id")
    var document: Document? = null // документ

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null // изделие

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company? = null // компания

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "price_protocol_id")
    var priceProtocol: ProductDecipherment? = null // форма 1 Протокол цены из расчета цены

    @Column(name = "protocol_number")
    var protocolNumber: String? = null // номер протокола

    @Column(name = "protocol_date")
    var protocolDate: LocalDateTime? = null // дата протокола

    @Column(name = "protocol_note")
    var protocolNote: String? = null // комментарий к протоколу

    @Column(name = "price")
    var price: BigDecimal = BigDecimal.ZERO // цена

    @Column(name = "price_unpack")
    var priceUnpack: BigDecimal = BigDecimal.ZERO // цена(без упаковки)

    @Column(name = "material")
    var material = 0.0 // сырье и основные материалы

    @Column(name = "add_material")
    var addMaterial = 0.0 // вспомогательные материалы

    @Column(name = "half_unit")
    var halfUnit = 0.0 // покупные полуфабрикаты

    @Column(name = "components_own")
    var componentsOwn = 0.0 // составляющая цены: Собствен Компл Изд

    @Column(name = "purchased_component")
    var purchasedComponent = 0.0 // составляющая цены : ПКИ (теперь это сумма 2-х)

    @Column(name = "package")
    var pack = 0.0 // тара (невозвратная) и упаковка

    @Column(name = "remainder")
    var remainder = 0.0 // возвратные отходы (вычитаются)

    @Column(name = "launch_cost_eq",)
    var launchCostEQ = 0.0 // затраты на подготовку и освоение новых производств, цехов и агрегатов

    @Column(name = "launch_cost_prod")
    var launchCostProd = 0.0 // затраты на подготовку и освоение новых видов продукции и новых технологических процессов

    @Column(name = "gear_cost")
    var gearCost = 0.0 // затраты на специальную технологическую оснастку

    @Column(name = "partner_charges")
    var partnerCharges = 0.0 // составляющая цены: контрагентам

    @Column(name = "transport")
    var transport = 0.0 // транспортно-заготовительные расходы

    @Column(name = "fuel")
    var fuel = 0.0 // топливо на технологические цели

    @Column(name = "energy")
    var energy = 0.0 // энергия на технологические цели

    @Column(name = "special_equip_charges")
    var specialEquipCharges = 0.0 // составляющая цены : Спецоборудование

    @OneToMany(mappedBy = "productChargesProtocol")
    var productLabourReferenceList = mutableListOf<ProductLabourReference>()
}