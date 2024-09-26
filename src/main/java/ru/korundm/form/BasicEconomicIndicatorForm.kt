package ru.korundm.form

import org.springframework.format.annotation.DateTimeFormat
import ru.korundm.constant.BaseConstant.DATE_PATTERN
import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import java.time.LocalDate

class BasicEconomicIndicatorListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var name: String = "", // наименование
    var docName: String = "", // наименование документа обоснования
    @DateTimeFormat(pattern = DATE_PATTERN)
    var approvalDate: LocalDate? = null, // дата утверждения
    var additionalSalary: Double? = null, // дополнительная заработная плата
    var socialInsurance: Double? = null, // отчисления на соц. страхование
    var overheadCosts: Double? = null, // накладные расходы
    var productionCosts: Double? = null, // общепроизводственные расходы
    var householdExpenses: Double? = null // общехозяйственные расходы
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        name = name.trim()
        if (name.isEmpty() || name.length > 128) errors.putError(::name.name, ValidatorMsg.RANGE_LENGTH, 1, 128)
        docName = docName.trim()
        if (docName.isEmpty() || docName.length > 128) errors.putError(::docName.name, ValidatorMsg.RANGE_LENGTH, 1, 128)
        if (approvalDate == null) errors.putError(::approvalDate.name, ValidatorMsg.REQUIRED)
        if (additionalSalary == null || additionalSalary == .0) errors.putError(::additionalSalary.name, ValidatorMsg.REQUIRED)
        if (socialInsurance == null || socialInsurance == .0) errors.putError(::socialInsurance.name, ValidatorMsg.REQUIRED)
        if (overheadCosts == null || overheadCosts == .0) errors.putError(::overheadCosts.name, ValidatorMsg.REQUIRED)
        if (productionCosts == null || productionCosts == .0) errors.putError(::productionCosts.name, ValidatorMsg.REQUIRED)
        if (householdExpenses == null || householdExpenses == .0) errors.putError(::householdExpenses.name, ValidatorMsg.REQUIRED)
    }
}