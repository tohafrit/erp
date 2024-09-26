package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.FileStorable
import ru.korundm.helper.RowCountable
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения документации по изделиям
 */
@Entity
@Table(name = "product_documentation")
data class ProductDocumentation(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : FileStorable<ProductDocumentation>, RowCountable {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product? = null // изделие

    @Column(name = "name", length = 128)
    var name: String = "" // наименование

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount

    override fun storableId() = this.id

    override fun storableClass() = ProductDocumentation::class
}