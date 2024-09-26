package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant
import ru.korundm.helper.RowCountable
import javax.persistence.*

/**
 * Сущность с описанием таблицы functionality
 */
@Entity
@Table(name = "functionality")
class Functionality(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null // идентификатор
) : RowCountable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_symbol_id", nullable = false)
    var serviceSymbol: ServiceSymbol? = null // служебный символ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technological_entity_id")
    var technologicalEntity: TechnologicalEntity? = null // технологическая документация

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functionality_id")
    var parent: Functionality? = null // родительская функциональность

    @OneToMany(mappedBy = "parent")
    @OrderBy("sort ASC, oFunctionality.contentSort ASC")
    var childList = mutableListOf<Functionality>() // список функциональностей

    @Embedded
    var aFunctionality: AFunctionality? = null

    @Embedded
    var bFunctionality: BFunctionality? = null

    @Embedded
    var oFunctionality: OFunctionality? = null

    @Embedded
    var rFunctionality: RFunctionality? = null

    @Embedded
    var tFunctionality: TFunctionality? = null

    @Embedded
    var mFunctionality: MFunctionality? = null

    @Column(name = "sort", nullable = false)
    var sort = 0 // сортировка

    @Formula(BaseConstant.PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}

@Embeddable
class AFunctionality {
    @ManyToMany(targetEntity = ProductionArea::class, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "functionality_xref_production_area",
        joinColumns = [JoinColumn(name = "functionality_id")],
        inverseJoinColumns = [JoinColumn(name = "production_area_id")]
    )
    var productionAreaList = mutableListOf<ProductionArea>() // список участков

//    TODO Замена technological_entity_star
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "functionality_note_id")
//    var technologicalEntityStar: TechnologicalEntityStar? = null // примечание

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_type_id")
    var workType: WorkType? = null // операция

    @Column(name = "work_type_number", length = 64)
    var number: String? = null // номер операции

    @Column(name = "work_type_name_comment", length = 512)
    var nameComment: String? = null // комментарий к названию операции

    @Column(name = "description", length = 1024)
    var description: String? = null // комментарий к операции

    @ManyToMany(targetEntity = LaborProtectionInstruction::class, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "functionality_xref_labor_protection",
        joinColumns = [JoinColumn(name = "functionality_id")],
        inverseJoinColumns = [JoinColumn(name = "labor_protection_instruction_id")]
    )
    var laborProtectionInstructionList = mutableListOf<LaborProtectionInstruction>() // список ИОТ
}

@Embeddable
class BFunctionality {
    @ManyToMany(targetEntity = Equipment::class, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "functionality_xref_equipment",
        joinColumns = [JoinColumn(name = "functionality_id")],
        inverseJoinColumns = [JoinColumn(name = "equipment_id")]
    )
    var equipmentList = mutableListOf<Equipment>() // список оборудования

    @Column(name = "koid")
    var koid: Int? = null // коэффициент одновременной обработки деталей

    @Column(name = "ksht")
    var ksht: String? = null // коэффициент штучного времени при многостаночном обслуживании

    @Column(name = "tpz")
    var tpz: String? = null // подготовительно-заключительное время обработки детали (деталей)

    @Column(name = "tsht")
    var tsht: String? = null // штучное время обработки детали (деталей)
}

@Embeddable
class OFunctionality {
    @Column(name = "content")
    var content: String? = null // содержание операции

    @Column(name = "content_sort")
    var contentSort: Int? = null // сортировка содержания операций
}

@Embeddable
class RFunctionality {
    @Column(name = "mode")
    var mode: String? = null // режим проведения операции
}

@Embeddable
class TFunctionality {
    @ManyToMany(targetEntity = TechnologicalTool::class, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "functionality_xref_technological_tool",
        joinColumns = [JoinColumn(name = "functionality_id")],
        inverseJoinColumns = [JoinColumn(name = "technological_tool_id")]
    )
    var technologicalToolList = mutableListOf<TechnologicalTool>() // список технологической оснастки
}

@Embeddable
class MFunctionality {
    @ManyToMany(targetEntity = OperationMaterial::class, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "functionality_xref_operation_material",
        joinColumns = [JoinColumn(name = "functionality_id")],
        inverseJoinColumns = [JoinColumn(name = "operation_material_id")]
    )
    var operationMaterialList = mutableListOf<OperationMaterial>() // список материалов
}

fun Functionality.copy() =  Functionality().apply {
    technologicalEntity = this@copy.technologicalEntity
    serviceSymbol = this@copy.serviceSymbol
    parent = this@copy.parent
    sort = this@copy.sort
}