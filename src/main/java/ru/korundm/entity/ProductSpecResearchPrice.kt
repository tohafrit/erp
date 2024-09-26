package ru.korundm.entity

import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения цен на специальные исследования изделий
 */
@Entity
@Table(name = "product_spec_research_price")
data class ProductSpecResearchPrice(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "justification_id", nullable = false)
    var justification: ProductSpecResearchJustification? = null // обоснование

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    var group: ClassificationGroup? = null // классификационная группа изделия

    @Column(name = "price", precision = 10, scale = 2)
    var price = .0 // цена
}