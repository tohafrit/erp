package ru.korundm.entity

import java.math.BigDecimal
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

/**
 * Сущность с описанием таблицы хранения значений коэффициентов для анализа цены изделия
 * @author mertsalova_uv
 * Date:   19.10.2021
 */
@Entity
@Table(name = "product_price_analysis_val")
data class ProductPriceAnalysisVal(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long? = null
) {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "period_id", nullable = false)
    var period: ProductDeciphermentPeriod? = null // период

    @Column(name = "additional_salary")
    var additionalSalary: BigDecimal = BigDecimal.ZERO // дополнительная заработная плата

    @Column(name = "social_security_contribution")
    var socialSecurityContribution: BigDecimal = BigDecimal.ZERO // отчисление на социальное страхование

    @Column(name = "general_production_cost")
    var generalProductionCost: BigDecimal = BigDecimal.ZERO  // общепроизводственные затраты

    @Column(name = "general_operation_cost")
    var generalOperationCost: BigDecimal = BigDecimal.ZERO  // общехозяйственные затраты

    @Column(name = "deflator_coefficient")
    var deflatorCoefficient: BigDecimal = BigDecimal.ZERO  // коэф.дефлятор (ИЦП)
}