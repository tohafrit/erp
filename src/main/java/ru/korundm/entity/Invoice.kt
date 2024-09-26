package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.enumeration.InvoiceStatus
import ru.korundm.enumeration.InvoiceType
import ru.korundm.helper.RowCountable
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы invoices
 * @author zhestkov_an
 * Date:   19.03.2021
 */
@Entity
@Table(name = "invoices")
data class Invoice(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "invoice")
    @GenericGenerator(name = "invoice", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "contract_section_id", nullable = false)
    var contractSection: ContractSection? = null // дополнение (секция) договора

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    var account: Account? = null // расчетный счет

    @Column(name = "number")
    var number: Long? = null // номер счета

    @Column(name = "created_date")
    var createdDate: LocalDate? = null // дата создания счета

    @Column(name = "price")
    var price: BigDecimal = BigDecimal.ZERO // сумма с ндс

    @Column(name = "note")
    var note: String? = null // комментарий

    @Convert(converter = InvoiceStatus.CustomConverter::class)
    @Column(name = "status", nullable = false)
    var status: InvoiceStatus? = null // статус счета

    @Convert(converter = InvoiceType.CustomConverter::class)
    @Column(name = "type", nullable = false)
    var type: InvoiceType? = null // тип счета

    @Column(name = "date_valid_before", nullable = false)
    var dateValidBefore: LocalDate? = null // счет действителен до

    @Column(name = "production_finish_date", nullable = false)
    var productionFinishDate: LocalDate? = null // срок изготовления изделий

    @Column(name = "paid")
    var paid: BigDecimal? = null // сумма, на которую оплачен счет

    @ManyToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinTable(
        name = "rel_invoice_allotment",
        joinColumns = [JoinColumn(name = "invoice_id")],
        inverseJoinColumns = [JoinColumn(name = "allotment_id")]
    )
    var allotmentList = mutableListOf<Allotment>() // список частей поставки

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}