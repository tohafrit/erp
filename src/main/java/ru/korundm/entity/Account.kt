package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы accounts
 * @author zhestkov_an
 * Date:   23.03.2021
 */
@Entity
@Table(name = "accounts")
data class Account(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "account")
    @GenericGenerator(name = "account", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Column(name = "code_1c")
    var code1C: String? = null // код в базе 1С

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    var company: Company? = null // организация

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    var bank: Bank? = null // банк

    @Column(name = "account")
    var account: String? = null // расчетный счет

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "government_contract_id")
    var governmentContract: GovernmentContract? = null // государственный контракт

    @Column(name = "note")
    var note: String? = null // комментарий

    @OneToMany(mappedBy = "account")
    var invoiceList = mutableListOf<Invoice>() // счета

    @OneToMany(mappedBy = "separateAccount")
    var sectionList = mutableListOf<ContractSection>() // секции договора

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "account_xref_allotment",
        joinColumns = [JoinColumn(name = "account_id")],
        inverseJoinColumns = [JoinColumn(name = "allotment_id")]
    )
    var allotmentList = mutableListOf<Allotment>() // список частей поставки

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}