package ru.korundm.entity

import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения вхождений трудоемкости изготовления и проверки изделия
 */
@Entity
@Table(name = "product_labour_intensity_entry")
data class ProductLabourIntensityEntry(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "labour_intensity_id", nullable = false)
    var labourIntensity: ProductLabourIntensity? = null // трудоемкость изделия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null // изделие

    @Column(name = "create_date")
    var createDate: LocalDate = LocalDate.MIN // дата создания

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User? = null // кем создан

    @Column(name = "approval_date")
    var approvalDate: LocalDate? = null // дата утверждения

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "approved_by")
    var approvedBy: User? = null // кем утверждено

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий
}