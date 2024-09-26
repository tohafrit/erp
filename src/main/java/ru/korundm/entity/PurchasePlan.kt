package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.enumeration.BomVersionType
import ru.korundm.enumeration.ReserveUseType
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*

/**
 * Сущность с описанием таблицы закупочной ведомости
 */
@Entity
@Table(name = "purchase_plan")
data class PurchasePlan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "name", length = 128, nullable = false)
    var name = "" // наименование

    @Column(name = "create_date", nullable = false)
    var createDate: LocalDate? = null // дата создания

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User? = null // кем создана

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "launch_id")
    var launch: Launch? = null // запуск

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_launch_id")
    var previousLaunch: Launch? = null // предыдущий запуск

    @Convert(converter = BomVersionType.CustomConverter::class)
    @Column(name = "bom_version_type", nullable = false)
    var bomVersionType: BomVersionType? = null // тип версий закупочных спецификаций

    @Column(name = "on_the_way_last_date")
    var onTheWayLastDate: LocalDate? = null // дата отсечки (дата крайнего срока поставки для импорта данных о товарах в пути)

    @Convert(converter = ReserveUseType.CustomConverter::class)
    @Column(name = "reserve_use_type", nullable = false)
    var reserveUseType: ReserveUseType? = null // тип учета запасов компонентов

    @Column(name = "approval_date")
    var approvalDate: LocalDate? = null // дата утверждения

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    var approvedBy: User? = null // утвердивший пользователь

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_plan_note_id")
    var purchasePlanNote: PurchasePlanNote? = null // примечание закупочной ведомости

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}