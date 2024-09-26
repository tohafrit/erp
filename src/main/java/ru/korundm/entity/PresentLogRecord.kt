package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.enumeration.ProductAcceptType
import ru.korundm.enumeration.SpecialTestType
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы present_log_record
 * @author zhestkov_an
 * Date:   20.07.2021
 */
@Entity
@Table(name = "present_log_record")
data class PresentLogRecord(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "presentLogRecord")
    @GenericGenerator(name = "presentLogRecord", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "number")
    var number = 0 // порядковый номер в течение календарного года

    @Column(name = "year")
    var year = 0 // год

    @Column(name = "registration_date", nullable = false)
    var registrationDate: LocalDate? = null // дата регистрации

    @Convert(converter = ProductAcceptType.CustomConverter::class)
    @Column(name = "presentation_type")
    var presentationType: ProductAcceptType? = null // тип приёмки изделий

    @Convert(converter = SpecialTestType.CustomConverter::class)
    @Column(name = "special_test_type")
    var specialTestType: SpecialTestType? = null // тип спецпроверки

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "qcd_representative_id", nullable = false)
    var qcdRepresentative: DocumentLabel? = null // представитель ОТК

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "qcd_chief_id", nullable = false)
    var qcdChief: DocumentLabel? = null // начальник ОТК

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "senior_controller_id", nullable = false)
    var seniorController: DocumentLabel? = null // старший контролер

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "head_of_the_enterprise_id", nullable = false)
    var headEnterprise: DocumentLabel? = null // руководитель предприятия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "head_of_the_enterprise_passport_id", nullable = false)
    var headEnterprisePassport: DocumentLabel? = null // руководитель предприятия (паспорт изделия)

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "head_of_warehouse_id", nullable = false)
    var headWarehouse: DocumentLabel? = null // руководитель предприятия (извещение)

    @Column(name = "conformity_statement_number")
    var conformityStatementNumber: String? = null // номер заявления о соответствии

    @Column(name = "conformity_statement_year")
    var conformityStatementYear: Int? = null // год заявления о соответствии

    @Column(name = "conformity_statement_create_date")
    var conformityStatementCreateDate: LocalDate? = null // дата создания заявления о соответствии

    @Column(name = "conformity_statement_validity")
    var conformityStatementValidity: LocalDate? = null // срок действия

    @Column(name = "conformity_statement_transfer_date")
    var conformityStatementTransferDate: LocalDate? = null // дата передачи

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "signatory_conformity_statement_id")
    var signatoryConformityStatement: User? = null // подписант заявления о соответствии

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Column(name = "notice_number", length = 128)
    var noticeNumber: String? = null // номер извещения

    @Column(name = "notice_create_date")
    var noticeCreateDate: LocalDate? = null // дата создания извещения

    @OneToMany(mappedBy = "presentLogRecord")
    var matValueList = mutableListOf<MatValue>()

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}