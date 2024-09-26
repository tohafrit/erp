package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы изделий (ex. ECOPLAN.PRODUCT)
 */
@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var ver = 0L

    @Column(name = "conditional_name", length = 128)
    var conditionalName: String? = null // условное наименование

    @Column(name = "tech_spec_name", length = 256)
    var techSpecName: String? = null // наименование по технической спецификации

    @Column(name = "production_name", length = 256)
    var productionName: String? = null // производственное наименование

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    var type: ProductType? = null // краткая техническая характеристика

    @Column(name = "decimal_number", length = 128)
    var decimalNumber: String? = null // технические условия изделия

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "letter_id")
    var letter: ProductLetter? = null // литера

    @Column(name = "archive_date")
    var archiveDate: LocalDateTime? = null // дата отправки в архив

    @Column(name = "position", precision = 6)
    var position: Int? = null // позиция

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "lead_id")
    var lead: User? = null // ведущий

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "classification_group_id")
    var classificationGroup: ClassificationGroup? = null // классификационная группа

    @Column(name = "comment", length = 1024)
    var comment: String? = null // комментарий

    @Column(name = "price")
    var price: BigDecimal? = null // согласованная с ПЗ цена изделия

    @Column(name = "export_price")
    var exportPrice: BigDecimal? = null // экспортная цена

    @Column(name = "exam_act", length = 64)
    var examAct: String? = null // акт периодичесих испытаний

    @Column(name = "exam_act_date")
    var examActDate: LocalDate? = null // дата акта периодичесих испытаний

    @Column(name = "family_decimal_number", length = 128)
    var familyDecimalNumber: String? = null // децимальный номер

    @Column(name = "suffix", length = 64)
    var suffix: String? = null // суффикс

    @Column(name = "template_path")
    var templatePath: String? = null // каталог шаблонов паспорта

    @Column(name = "serial")
    var serial = false // серийное

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount

    @OneToMany(mappedBy = "product")
    var launchProductList = mutableListOf<LaunchProduct>()

    @OneToMany(mappedBy = "product")
    var lotGroupList = mutableListOf<LotGroup>()

    @OneToMany(mappedBy = "product")
    var launchProductStructList = mutableListOf<LaunchProductStruct>()

    @OneToMany(mappedBy = "product")
    @OrderBy("major asc, minor asc, modification asc")
    var bomList = mutableListOf<Bom>()

    @OneToMany(mappedBy = "subProduct")
    var productionLotSpecList = mutableListOf<ProductionLotSpec>()

    @OneToMany(mappedBy = "product")
    @OrderBy("protocolDate desc")
    var productChargesProtocolList = mutableListOf<ProductChargesProtocol>()

    @OneToMany(mappedBy = "product")
    var productLabourReferenceList = mutableListOf<ProductLabourReference>()

    @OneToMany(mappedBy = "product")
    var bomSpecItemList = mutableListOf<BomSpecItem>()

    /**
     * Метод показывает является ли изделие ЭВМ
     * @return true - изделие типа ЭВМ, иначе - false
     */
    val isEvm
        get() = type!!.id == EVM_ID

    companion object {
        /** Идентификатор ЭВМ  */
        private const val EVM_ID = 1L
    }
}