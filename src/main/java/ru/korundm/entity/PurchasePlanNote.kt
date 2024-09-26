package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*

/**
 * Сущность с описанием таблицы примечаний закупочной ведомости
 */
@Entity
@Table(name = "purchase_plan")
data class PurchasePlanNote(
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

    @Column(name = "approval_date")
    var approvalDate: LocalDate = LocalDate.MIN // дата утверждения

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    var approvedBy: User? = null // утвердивший пользователь

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_plan_period_id")
    var purchasePlanPeriod: PurchasePlanPeriod? = null // период поставки компонентов

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product? = null // изделие

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}