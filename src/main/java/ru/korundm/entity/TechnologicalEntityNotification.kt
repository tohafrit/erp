package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.enumeration.ReasonType
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "technological_entity_notification")
data class TechnologicalEntityNotification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null // идентификатор
) : RowCountable {

    @Column(name = "doc_number", length = 128, nullable = false)
    var docNumber = "" // номер

    @Column(name = "release_on", nullable = false)
    var releaseOn: LocalDate? = null // дата выпуска

    @Column(name = "term_change_on", nullable = false)
    var termChangeOn: LocalDate? = null // срок изменения

    @Column(name = "reserve_indication", nullable = false)
    var reserveIndication = false // указание о заделе

    @Column(name = "introduction_indication", length = 128, nullable = false)
    var introductionIndication = "" // указание о внедрении

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tech_user_id", nullable = false)
    var techUser: User? = null // технолог

    @Convert(converter = ReasonType.CustomConverter::class)
    @Column(name = "reason_type", nullable = false)
    var reason: ReasonType? = null // причина изменения

    @Column(name = "text", nullable = false)
    var text = "" // текст извещения

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technological_entity_id", nullable = false)
    var technologicalEntity: TechnologicalEntity? = null // технологическая документация

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "technological_entity_notification_xref_cd_notification",
        joinColumns = [JoinColumn(name = "technological_entity_notification_id")],
        inverseJoinColumns = [JoinColumn(name = "constructor_document_notification_id")]
    )
    var cdNotificationList = mutableListOf<ConstructorDocumentNotification>() // список извещений об изменении КД

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}
