package ru.korundm.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения внешних накладных на отгрузку
 */
@Entity
@Table(name = "shipment_waybill")
data class ShipmentWaybill(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var ver = 0L

    @Column(name = "number")
    var number = 0 // номер

    @Column(name = "year")
    var year = 0 // год

    @Column(name = "create_date")
    var createDate: LocalDate = LocalDate.MIN // дата создания

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "contract_section_id", nullable = false)
    var contractSection: ContractSection? = null // секция договора

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "account_id")
    var account: Account? = null // счет

    @Column(name = "shipment_date")
    var shipmentDate: LocalDate? = null // дата отгрузки

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "consignee_id")
    var consignee: Company? = null // грузополучатель

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "give_user_id")
    var giveUser: User? = null // отпуск произвел

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "permit_user_id")
    var permitUser: User? = null // отпуск разрешил

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "accountant_user_id")
    var accountantUser: User? = null // главный бухгалтер

    @Column(name = "receiver", length = 64)
    var receiver: String? = null

    /*@ManyToOne(fetch = LAZY)
    @JoinColumn(name = "receiver_id")
    var receiver: ContractorEmployee? = null // сотрудник контрагента, получившего изделия*/

    @Column(name = "letter_of_attorney", length = 128)
    var letterOfAttorney: String? = null // доверенность

    @Column(name = "transmittal_letter", length = 128)
    var transmittalLetter: String? = null // сопроводительное письмо

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @OneToMany(mappedBy = "shipmentWaybill")
    var matValueList = mutableListOf<MatValue>() // список мат. ценностей
}