package ru.korundm.form

import org.springframework.format.annotation.DateTimeFormat
import ru.korundm.constant.BaseConstant.DATE_PATTERN
import ru.korundm.constant.ValidatorMsg
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import ru.korundm.util.KtCommonUtil.dateFromMoreThenTo
import java.math.BigDecimal
import java.time.LocalDate

class ProductionShipmentLetterListEditForm(
    var id: Long? = null,
    var number: Int? = null, // порядковый номер в течение календарного года
    @DateTimeFormat(pattern = DATE_PATTERN)
    var createDate: LocalDate? = null, // дата создания
    @DateTimeFormat(pattern = DATE_PATTERN)
    var sendToWarehouseDate: LocalDate? = null, // дата отправки на склад (дата отгрузки)
    @DateTimeFormat(pattern = DATE_PATTERN)
    var sendToProductionDate: LocalDate? = null, // дата отправки в производство (дата отправки в ОТК)
    var comment: String = "", // комментарий
    var allotmentIdList: String = "", // json строка со списком айдишников выбранных allotment-ов
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        comment = comment.trim()
        allotmentIdList = allotmentIdList.trim()
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
        if (createDate == null) errors.putError(::createDate.name, ValidatorMsg.REQUIRED)
        if (allotmentIdList == "[]") errors.putError(::allotmentIdList.name, ValidatorMsg.REQUIRED)
        sendToProductionDate?.let { if (dateFromMoreThenTo(createDate, it)) errors.putError(::sendToProductionDate.name, ValidatorMsg.DATE_MUST_BE_MORE) }
        sendToWarehouseDate?.let { if (dateFromMoreThenTo(createDate, it)) errors.putError(::sendToWarehouseDate.name, ValidatorMsg.DATE_MUST_BE_MORE) }
    }

    class AllotmentList(
        val id: Long? = null,
        val contractNumber: String? = null, // номер договора
        val productName: String? = null, // изделие
        val groupMain: String = "", // название главной группы группировки столбцов
        val deliveryDate: LocalDate? = LocalDate.MIN, // дата поставки
        val groupSubMain: String = "", // название sub группы группировки столбцов
        val allotmentAmount: Long = 0, // количество изделий в части поставки
        val cost: BigDecimal = BigDecimal.ZERO, // стоимость
        val paid: BigDecimal = BigDecimal.ZERO, // оплачено (руб.)
        val percentPaid: BigDecimal = BigDecimal.ZERO, // оплачено (%)
        val finalPrice: BigDecimal? = BigDecimal.ZERO, // окончательная цена
        val launchNumber: String? = null, // запуск
        val launchAmount: Long = 0 // количество запущенных изделий у allotment-a
    )
}