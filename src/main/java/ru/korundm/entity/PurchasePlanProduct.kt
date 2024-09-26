package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant
import ru.korundm.helper.RowCountable
import javax.persistence.*

/**
 * Сущность с описанием таблицы изделий закупочной ведомости
 */
@Entity
@Table(name = "purchase_plan_product")
data class PurchasePlanProduct(
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_plan_id", nullable = false)
    var purchasePlan: PurchasePlan? = null // закупочная ведомость

    @Formula(BaseConstant.PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}