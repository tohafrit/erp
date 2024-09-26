package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.enumeration.ContractType
import ru.korundm.enumeration.Performer
import ru.korundm.helper.RowCountable
import ru.korundm.util.KtCommonUtil.contractFullNumber
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения договоров
 */
@Entity
@Table(name = "contracts")
data class Contract(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "contract")
    @GenericGenerator(name = "contract", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Convert(converter = ContractType.CustomConverter::class)
    @Column(name = "type", nullable = false)
    var type: ContractType? = null // тип контракта

    @Column(name = "number")
    var number = 0 // порядковый номер договора в течение года

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    var customer: Company? = null // организация-заказчик

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Convert(converter = Performer.CustomConverter::class)
    @Column(name = "performer", nullable = false)
    var performer: Performer? = null // организация-исполнитель

    @OneToMany(mappedBy = "contract", cascade = [CascadeType.ALL])
    @OrderBy("id asc")
    var sectionList = mutableListOf<ContractSection>() // секции контракта

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    val fullNumber // полный номер договора
        get() = contractFullNumber(number, performer?.id, type?.id, sectionList.first().year)

    val isActive // активность договора (true - активный, если false - то архивный)
        get() = sectionList.first().archiveDate == null

    override fun rowCount() = rowCount
}