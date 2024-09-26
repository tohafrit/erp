package ru.korundm.entity

import ru.korundm.enumeration.ProductDeciphermentAttr
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения значений атрибутов расшифровки цены изделия
 * @author mazur_ea
 * Date:   07.07.2021
 */
@Entity
@Table(name = "product_decipherment_attr_val")
data class ProductDeciphermentAttrVal(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @Version
    var version = 0L

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "decipherment_id", nullable = false)
    var decipherment: ProductDecipherment? = null // расшифровка

    @Convert(converter = ProductDeciphermentAttr.Converter::class)
    @Column(name = "attribute", nullable = false)
    var attribute: ProductDeciphermentAttr? = null // атрибут

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null // пользователь

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_labour_intensity_id")
    var productLabourIntensity: ProductLabourIntensity? = null // список расчетов трудоемкости

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_work_cost_justification_id")
    var productWorkCostJustification: ProductWorkCostJustification? = null // обоснование стоимости работ по изготовлению изделия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_spec_research_justification_id")
    var productSpecResearchJustification: ProductSpecResearchJustification? = null // обоснование цен на специальные исследования изделий

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_spec_review_justification_id")
    var productSpecReviewJustification: ProductSpecReviewJustification? = null // обоснование цен на специальные проверки изделий

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "company_id")
    var company: Company? = null // организация

    @Column(name = "string_val", length = 1024)
    var stringVal: String? = null // строковое значение

    @Column(name = "long_val")
    var longVal: Long? = null // long значение

    @Column(name = "bool_val")
    var boolVal: Boolean? = null // булево значение

    @Column(name = "date_val")
    var dateVal: LocalDate? = null // значение даты

    @Column(name = "datetime_val")
    var datetimeVal: LocalDateTime? = null // значение даты/времени

    @Column(name = "json_val")
    var jsonVal: String? = null // JSON значение

    @Column(name = "decimal_val")
    var decimalVal: BigDecimal? = null // BigDecimal значение
}