package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения параметров для сформированных документов по договорам
 */
@Entity
@Table(name = "contract_documentation_param")
data class ContractDocumentationParam(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Column(name = "name", length = 256)
    var name = "" // название файла

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "contract_section_id", nullable = false)
    var contractSection: ContractSection? = null // раздел договора

    @Column(name = "params")
    var params: String? = null // параметры в формате json

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}