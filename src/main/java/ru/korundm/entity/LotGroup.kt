package ru.korundm.entity

import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы lot_groups
 * @author zhestkov_an
 * Date:   11.03.2021
 */
@Entity
@Table(name = "lot_groups")
data class LotGroup(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "lotGroup")
    @GenericGenerator(name = "lotGroup", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "contract_section_id", nullable = false)
    var contractSection: ContractSection? = null // секция договора

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null // изделие

    @Column(name = "order_index")
    var orderIndex: Long? = null // номер позиции в договоре

    @Column(name = "note")
    var note: String? = null // комментарий

//    @Convert(converter = LotGroupKind.CustomConverter::class)
//    @Column(name = "kind")
//    var kind: LotGroupKind? = null // вид работ

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "service_type_id", nullable = false)
    var serviceType: ServiceType? = null // тип услуги

    @Column(name = "bom")
    var bom: Long? = null //

/*    @OneToMany(mappedBy = "lotGroup")
    var presentLogRecordList = mutableListOf<PresentLogRecord>() // предъявления*/

    @OneToMany(mappedBy = "lotGroup")
    var lotList = mutableListOf<Lot>() // атрибуты ведомости
}