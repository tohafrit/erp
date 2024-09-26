package ru.korundm.entity

import ru.korundm.helper.FileStorable
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения обоснований стоимости работ по изготовлению изделия
 */
@Entity
@Table(name = "product_work_cost_justification")
data class ProductWorkCostJustification(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : FileStorable<ProductWorkCostJustification> {

    @Version
    var version = 0L

    @Column(name = "name", length = 128)
    var name: String = "" // наименование

    @Column(name = "approval_date")
    var approvalDate: LocalDate = LocalDate.MIN // дата утверждения

    @Column(name = "create_date")
    var createDate: LocalDate = LocalDate.MIN // дата создания

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    override fun storableId() = this.id

    override fun storableClass() = ProductWorkCostJustification::class
}