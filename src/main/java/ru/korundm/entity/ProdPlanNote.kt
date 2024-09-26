package ru.korundm.entity

import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(name = "prod_plan_note")
data class ProdPlanNote(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L
}