package ru.korundm.entity

import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY
import ru.korundm.constant.BaseConstant.PART_QUERY_COUNT_OVER
import ru.korundm.helper.RowCountable
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения базовых планово-экономических показателей
 * @author pakhunov_an
 * Date:   13.05.2020
 */
@Entity
@Table(name = "basic_economic_indicator")
data class BasicEconomicIndicator(
    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "basicEconomicIndicator")
    @GenericGenerator(name = "basicEconomicIndicator", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) : RowCountable {

    @Version
    var version = 0L

    @Column(name = "name", length = 128)
    var name: String = "" // наименование

    @Column(name = "doc_name", length = 128)
    var docName: String = "" // наименование документа обоснования

    @Column(name = "approval_date")
    var approvalDate: LocalDate = LocalDate.MIN // дата утверждения

    @Column(name = "additional_salary", precision = 10, scale = 2)
    var additionalSalary = .0 // дополнительная заработная плата

    @Column(name = "social_insurance", precision = 10, scale = 2)
    var socialInsurance = .0 // отчисления на соц. страхование

    @Column(name = "overhead_costs", precision = 10, scale = 2)
    var overheadCosts = .0 // накладные расходы

    @Column(name = "production_costs", precision = 10, scale = 2)
    var productionCosts = .0 // общепроизводственные расходы

    @Column(name = "household_expenses", precision = 10, scale = 2)
    var householdExpenses = .0 // общехозяйственные расходы

    @Formula(PART_QUERY_COUNT_OVER)
    var rowCount = 0L

    override fun rowCount() = rowCount
}