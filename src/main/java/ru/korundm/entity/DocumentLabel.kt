package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant
import ru.korundm.helper.RowCountable
import javax.persistence.*

/**
 * Сущность с описанием таблицы хранения меток шаблонов паспортов изделий
 */
@Entity
@Table(name = "document_label")
data class DocumentLabel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "label", length = 128)
    var label: String? = null // название

    @Column(name = "employee_position", length = 128)
    var employeePosition: String? = null // должность сотрудника в документе (паспорте изделия)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null // сотрудник

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Formula(BaseConstant.PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}