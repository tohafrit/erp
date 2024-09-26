package ru.korundm.entity

import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "technological_entity_agreed")
data class TechnologicalEntityAgreed(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null // идентификатор
) {

    @Column(name = "agreed")
    var agreed: Boolean = false // согласовано

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreed_by")
    var agreedBy: User? = null // кто согласовал

    @Column(name = "agreed_on")
    var agreedOn: LocalDateTime? = null // когда согласовали

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technological_entity_reconciliation_id")
    var technologicalEntityReconciliation: TechnologicalEntityReconciliation? = null // согласование
}
