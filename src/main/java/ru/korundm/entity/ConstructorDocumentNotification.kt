package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*

/**
 * Сущность с описанием таблицы с извещениями об изменениях КД
 */
@Entity
@Table(name = "constructor_document_notification")
data class ConstructorDocumentNotification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "constructorDocumentNotification")
    @GenericGenerator(name = "constructorDocumentNotification", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Column(name = "doc_number", length = 128, nullable = false)
    var docNumber: String? = null // номер

    @Column(name = "release_on", nullable = false)
    var releaseOn: LocalDate? = null // дата выпуска

    @Column(name = "term_change_on", nullable = false)
    var termChangeOn: LocalDate? = null // срок изменения

    @Column(name = "reason", length = 512, nullable = false)
    var reason: String? = null // причина

    @Column(name = "reserve_indication")
    var reserveIndication = false // указание о заделе

    @Column(name = "introduction_indication", length = 128, nullable = false)
    var introductionIndication: String? = null // указание о внедрении

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicability_product_id", nullable = false)
    var applicabilityProduct: Product? = null // изделие применяемости

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_product_user_id", nullable = false)
    var leadProductUser: User? = null // ведущий изделия

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_notification_id")
    var parent: ConstructorDocumentNotification? = null // родительская КД

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var childList = mutableListOf<ConstructorDocumentNotification>() // список КД

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}