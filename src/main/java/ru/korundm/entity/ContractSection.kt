package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import ru.korundm.util.KtCommonUtil.contractFullNumber
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения секций договора
 */
@Entity
@Table(name = "contract_sections")
data class ContractSection(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    var contract: Contract? = null // контракт

    @Column(name = "number")
    var number = 0 // номер секции договра (дополнения)

    @Column(name = "year")
    var year = 0 // год

    @Column(name = "create_date", nullable = false)
    var createDate: LocalDate? = null // дата создания секции договора (дополнения)

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Column(name = "archive_date")
    var archiveDate: LocalDate? = null // дата помещения в архив

    @Column(name = "external_number")
    var externalNumber: String? = null // внешний номер

    @Column(name = "identifier")
    var identifier: String? = null // идентификатор гос. контракта

    @Column(name = "send_to_client_date")
    var sendToClientDate: LocalDate? = null // дата передачи в ПЗ

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "manager_id")
    var manager: User? = null // ведущий договор

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "separate_account_id")
    var separateAccount: Account? = null // отдельный банковский счет ОБС

    @OneToMany(mappedBy = "contractSection")
    var invoiceList = mutableListOf<Invoice>() // счета секции

    @OneToMany(mappedBy = "contractSection")
    var paymentList = mutableListOf<Payment>() // платежи секции

    @OneToMany(mappedBy = "contractSection")
    var lotGroupList = mutableListOf<LotGroup>() // партии секции

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount

    val fullNumber // полный номер договора и дополнительного соглашения, если оно есть
        get() = contractFullNumber(contract?.number, contract?.performer?.id, contract?.type?.id, year, number)
}