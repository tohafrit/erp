package ru.korundm.form

import org.springframework.format.annotation.DateTimeFormat
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.ValidatorMsg
import ru.korundm.enumeration.ProductAcceptType
import ru.korundm.enumeration.SpecialTestType
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import java.time.LocalDate

class PresentLogRecordListEditForm(
    var id: Long? = null,
    var version: Long = 0L,
    var number: Int? = null, // порядковый номер в течение календарного года
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    var registrationDate: LocalDate? = null, // дата регистрации
    var maxSerialNumberQuantity: Int? = null, // максимальное кол-во matValue к добавлению
    var allotmentId: Long? = null, // идентификатор allotment-a
    var productId: Long? = null, // идентификатор изделия
    var managerId: Long? = null, // идентификатор подписанта
    var acceptType: ProductAcceptType? = null, // тип приёмки изделий (enum ProductAcceptType.kt)
    var specialTestType: SpecialTestType? = null, // тип спец. проверки (enum SpecialTestType.kt)
    var examAct: String? = null, // № акта периодических испытаний
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    var examActDate: LocalDate? = null, // дата составления Акта ПИ
    var familyDecimalNumber: String? = null, // ТУ семейства
    var suffix: String? = null, // суффикс
    var comment: String = "", // комментарий
    var groupSerialNumber: String = "", // json строка со списком серийных номеров
    var conformityStatement: String = "", // json строка заявления о соответствии
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        comment = comment.trim()
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
        if (registrationDate == null) errors.putError(::registrationDate.name, ValidatorMsg.REQUIRED)
        if (groupSerialNumber == "[]") errors.putError(::groupSerialNumber.name, ValidatorMsg.REQUIRED)
    }

    class GroupSerialNumber(
        val id: Long? = null,
        val groupMain: String = "",
        val groupSubMain: String = "",
        val serialNumber: String = "",
        val allotmentId: Long = 0,
        val productId: Long = 0
    )

    class ConformityStatement(
        val id: Long? = null,
        val conformityStatementNumber: String? = null, // номер заявления о соответствии
        val conformityStatementCreateDate: String? = null, // дата заявления о соответствии
        val conformityStatementValidity: String? = null, // срок действия
        val conformityStatementTransferDate: String? = null, // дата передачи
        val managerId: Long? = null, // идентификатор подписанта заявления о соответствии
        val manager: String? = null // ФИО подписанта
    )
}