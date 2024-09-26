package ru.korundm.entity

import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения ОКП/ОКПД2 кодов
 * @author mazur_ea
 * Date:   06.07.2021
 */
@Entity
@Table(name = "okpd_code")
data class OkpdCode(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_type_id", unique = true)
    var productType: ProductType? = null // тип изделия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "component_group_id", unique = true)
    var componentGroup: ComponentGroup? = null // группа компонентов

    @Column(name = "code", length = 32, nullable = false)
    var code: String = "" // код

    enum class Type {
        PRODUCT, COMPONENT
    }
}