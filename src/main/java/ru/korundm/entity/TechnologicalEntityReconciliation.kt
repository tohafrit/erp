package ru.korundm.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "technological_entity_reconciliation")
data class TechnologicalEntityReconciliation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null // идентификатор
) {

    @OneToOne
    @JoinColumn(name = "technological_entity_id", referencedColumnName = "id")
    var technologicalEntity: TechnologicalEntity? = null // технологическая документация

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    var approvedBy: User? = null // утвердил

    @Column(name = "approved_on")
    var approvedOn: LocalDateTime? = null // когда утвердил

    @Column(name = "approved")
    var approved: Boolean = false // утверждено

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designed_by")
    var designedBy: User? = null // разработал

    @Column(name = "designed_on")
    var designedOn: LocalDateTime? = null // когда разработал

    @Column(name = "designed")
    var designed: Boolean = false // разработано

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by")
    var checkedBy: User? = null // проверил

    @Column(name = "checked_on")
    var checkedOn: LocalDateTime? = null // когда проверил

    @Column(name = "checked")
    var checked: Boolean = false // проверено

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metrologist_by")
    var metrologistBy: User? = null // метролог

    @Column(name = "metrologist_on")
    var metrologistOn: LocalDateTime? = null // когда метролог одобрил

    @Column(name = "metrologist")
    var metrologist: Boolean = false // одобрено метрологом

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "normocontroller_by")
    var normocontrollerBy: User? = null // одобривший нормоконтролер

    @Column(name = "normocontroller_on")
    var normocontrollerOn: LocalDateTime? = null // когда нормоконтролер одобрил

    @Column(name = "normocontroller")
    var normocontroller: Boolean = false // одобрено нормоконтролером

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "military_by")
    var militaryBy: User? = null // кто утвердил в ПЗ

    @Column(name = "military_on")
    var militaryOn: LocalDateTime? = null // когда утвердили в ПЗ

    @Column(name = "military")
    var military: Boolean = false // утверждено в ПЗ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technological_chief_by")
    var technologicalChiefBy: User? = null // начальник отдела технологов

    @Column(name = "technological_chief_on")
    var technologicalChiefOn: LocalDateTime? = null // когда согласовали с начальником отдела технологов

    @Column(name = "technological_chief")
    var technologicalChief: Boolean = false // согласовано (начальник отдела технологов)

    @OneToMany(mappedBy = "technologicalEntityReconciliation")
    var agreedList = mutableListOf<TechnologicalEntityAgreed>() // список согласующих
}
