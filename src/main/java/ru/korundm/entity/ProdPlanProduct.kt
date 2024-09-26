package ru.korundm.entity

import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(name = "prod_plan_product")
data class ProdPlanProduct(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "launch_product_id", nullable = false)
    var launchProduct: LaunchProduct? = null // изделие в запуске

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    var plan: ProdPlan? = null

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "plan_note_id", nullable = false)
    var planNote: ProdPlanNote? = null
}