package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant
import ru.korundm.helper.RowCountable
import javax.persistence.*


@Entity
@Table(name = "technological_entity")
data class TechnologicalEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null // идентификатор
) : RowCountable {

    @Column(name = "entity_number", length = 128, nullable = false)
    var entityNumber = "" // номер документации

    @Column(name = "set_number", length = 128, nullable = false)
    var setNumber = "" // номер комплекта

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_type_id")
    var entityType: TechnologicalEntityType? = null // наименование ТД

    @OneToMany(mappedBy = "technologicalEntity")
    var technologicalEntityApplicabilityList = mutableListOf<TechnologicalEntityApplicability>() // список применяемостей

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_letter_id")
    var productLetter: ProductLetter? = null // литера

    @OneToOne(mappedBy = "technologicalEntity")
    var technologicalEntityReconciliation: TechnologicalEntityReconciliation? = null // тэг по документации

    @OneToMany(mappedBy = "technologicalEntity")
    @OrderBy("id desc")
    var technologicalEntityNotificationList = mutableListOf<TechnologicalEntityNotification>() // список извещений

    val lastNotification: TechnologicalEntityNotification? // последнее уведомление об изменении, если список не пустой, иначе null
        get() = technologicalEntityNotificationList.firstOrNull()

    @Formula(BaseConstant.PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount

    @OneToMany(mappedBy = "technologicalEntity")
    @OrderBy("sort asc")
    var operationList = mutableListOf<TechnologicalEntityOperation>() // список операций

    /*@OneToMany(
        mappedBy = "functionalityTechnologicalProcess.technologicalProcess",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @Where(clause = "technological_process_operation_id is null")
    var functionalityList = mutableListOf<Functionality>() // список функциональностей

    @OneToMany(mappedBy = "technologicalProcess", cascade = [CascadeType.ALL])
    @OrderBy("name asc")
    var technologicalProcessStarList = mutableListOf<TechnologicalProcessStar>() // список примечаний*/
}
