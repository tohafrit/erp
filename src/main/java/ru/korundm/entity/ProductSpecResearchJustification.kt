package ru.korundm.entity

import ru.korundm.helper.FileStorable
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения обоснований цен на специальные исследования изделий
 */
@Entity
@Table(name = "product_spec_research_justification")
data class ProductSpecResearchJustification(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : FileStorable<ProductSpecResearchJustification> {

    @Version
    var version = 0L

    @Column(name = "name", length = 128)
    var name = "" // наименование

    @Column(name = "approval_date")
    var approvalDate: LocalDate = LocalDate.MIN // дата утверждения

    @Column(name = "create_date")
    var createDate: LocalDate = LocalDate.MIN // дата создания

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    override fun storableId() = this.id

    override fun storableClass() = ProductSpecResearchJustification::class
}