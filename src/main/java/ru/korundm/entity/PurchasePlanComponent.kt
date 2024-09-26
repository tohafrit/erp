package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant
import ru.korundm.helper.RowCountable
import javax.persistence.*

/**
 * Сущность с описанием таблицы компонентов закупочной ведомости
 */
@Entity
@Table(name = "purchase_plan_component")
data class PurchasePlanComponent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "for_contract_amount")
    var forContractAmount = 0 // количество по договору

    @Column(name = "reserved_for_assembled_amount")
    var reservedForAssembledAmount = 0 // задел по договору

    @Column(name = "reserved_for_contract_amount")
    var reservedForContractAmount = 0 // задел для сборки

    @Column(name = "warehouse_amount")
    var warehouseAmount = 0 // кол-во фактических свободных остатков компонента на складе

    @Column(name = "warehouse_edit_amount")
    var warehouseEditAmount = 0 //

    @Column(name = "responsible_storage_amount")
    var responsibleStorageAmount = 0 // кол-во экземпляров компонента, находящееся на ответственном хранении

    @Column(name = "unaccepted_amount")
    var unacceptedAmount = 0 // кол-во экземпляров компонента, которые не приняты на склад

    @Column(name = "on_the_way_amount")
    var onTheWayAmount = 0 // кол-во экземпляров компонента, которое находится в пути и поступит на склад к указанной дате

    @Column(name = "regulations_reserve_amount")
    var regulationsReserveAmount = 0 // кол-во экземпляров компонента, составляющее регламентный запас

    @Column(name = "insurance_reserve_amount")
    var insuranceReserveAmount = 0 // кол-во экземпляров компонента, составляющее страховой запас

    @Column(name = "proactive_reserve_amount")
    var proactiveReserveAmount = 0 // кол-во экземпляров компонента, составляющее страховой запас

    @Column(name = "irreducible_reserve_amount")
    var irreducibleReserveAmount = 0 // кол-во экземпляров компонента, составляющее не снижаемый запас

    @Column(name = "smto_amount")
    var smtoAmount = 0 // кол-во СМТО

    @Column(name = "smto_comment", length = 256)
    var smtoComment: String? = null // СМТО комментарий

    @Column(name = "opp_amount")
    var oppAmount = 0 // кол-во ОПП

    @Column(name = "opp_comment", length = 256)
    var oppComment: String? = null // ОПП комментарий

    @Column(name = "packing_norm")
    var packingNorm: Int? = null // норма упаковки

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_plan_product_id", nullable = false)
    var purchasePlanProduct: PurchasePlanProduct? = null // изделие закупочной ведомости

    @Formula(BaseConstant.PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}