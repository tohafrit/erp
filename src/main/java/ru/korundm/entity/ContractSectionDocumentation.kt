package ru.korundm.entity

import org.hibernate.annotations.Formula
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.FileStorable
import ru.korundm.helper.RowCountable
import javax.persistence.*
import javax.persistence.FetchType.LAZY

/**
 * Сущность с описанием таблицы хранения документации по секциям договора
 */
@Entity
@Table(name = "contract_sections_documentation")
data class ContractSectionDocumentation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : FileStorable<ContractSectionDocumentation>, RowCountable {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    var section: ContractSection? = null // секция договора

    @Column(name = "name", length = 128)
    var name: String = "" // наименование

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount

    override fun storableId() = this.id

    override fun storableClass() = ContractSectionDocumentation::class
}