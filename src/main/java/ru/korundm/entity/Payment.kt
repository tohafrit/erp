package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import ru.korundm.helper.RowCountable
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы payments
 * @author zhestkov_an
 * Date:   19.03.2021
 */
@Entity
@Table(name = "payments")
data class Payment(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "payment")
    @GenericGenerator(name = "payment", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    var invoice: Invoice? = null // счет

    @Column(name = "number")
    var number: String? = null // номер платежа

    @Column(name = "date")
    var date: LocalDate? = null // дата платежа

    @Column(name = "amount")
    var amount: BigDecimal = BigDecimal.ZERO // сумма

    @Column(name = "note")
    var note: String? = null // комментарий

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account_id")
    var account: Account? = null // расчетный счет

    @Column(name = "code_1c")
    var code1C: String? = null // код в базе 1С

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "contract_section_id")
    var contractSection: ContractSection? = null // секция контракта

    @Column(name = "advance_invoice_number")
    var advanceInvoiceNumber: String? = null // номер счета-фактуры на аванс

    @Column(name = "advance_invoice_date")
    var advanceInvoiceDate: LocalDate? = null // дата счета-фактуры

    @Formula(BaseConstant.PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}