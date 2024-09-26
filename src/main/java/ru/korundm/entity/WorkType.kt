package ru.korundm.entity

import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения информации о видах работ
 */
@Entity
@Table(name = "work_type")
data class WorkType(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @Column(name = "name", length = 128, unique = true)
    var name = "" // наименование

    @Column(name = "separate_delivery")
    var separateDelivery = false // отдельная поставка

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @ManyToMany(cascade = [CascadeType.ALL], mappedBy = "workTypeList")
    var operationMaterialList = mutableListOf<OperationMaterial>() // список материалов
}