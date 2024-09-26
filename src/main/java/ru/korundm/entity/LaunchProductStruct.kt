package ru.korundm.entity

import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения информации о составе изделий в запуске
 */
@Entity
@Table(name = "launch_product_struct")
data class LaunchProductStruct(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var ver = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "launch_product_id", nullable = false)
    var launchProduct: LaunchProduct? = null // изделие в запуске

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null // изделие

    @Column(name = "amount")
    var amount = 0 // количество
}