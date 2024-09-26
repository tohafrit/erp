package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*

/**
 * Сущность с описанием таблицы government_contracts
 * @author zhestkov_an
 * Date:   08.07.2021
 */
@Entity
@Table(name = "government_contracts")
data class GovernmentContract(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "governmentContract")
    @GenericGenerator(name = "governmentContract", strategy = BaseConstant.GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Column(name = "identifier", nullable = false, length = 25)
    var identifier: String? = null // идентификатор

    @Column(name = "date")
    var date: LocalDate? = null // дата заключения

    @Column(name = "comment", length = 256)
    var comment: String? = null // комментарий

    @OneToMany(mappedBy = "governmentContract")
    var accountList = mutableListOf<Account>() // отдельные банковские счета

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}