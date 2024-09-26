package ru.korundm.entity

import javax.persistence.*

@Entity
@Table(name = "technological_entity_applicability")
data class TechnologicalEntityApplicability(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null // идентификатор
) {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", nullable = false)
    var technologicalEntity: TechnologicalEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product? = null
}
