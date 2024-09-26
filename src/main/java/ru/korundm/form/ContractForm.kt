package ru.korundm.form

import com.fasterxml.jackson.annotation.JsonFormat
import org.apache.commons.lang3.StringUtils
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.multipart.MultipartFile
import ru.korundm.constant.BaseConstant.DATE_PATTERN
import ru.korundm.constant.BaseConstant.ONE_LONG
import ru.korundm.constant.BaseConstant.ONLY_DIGITAL_PATTERN
import ru.korundm.constant.ValidatorMsg
import ru.korundm.entity.*
import ru.korundm.enumeration.*
import ru.korundm.helper.SingularFileStorableType
import ru.korundm.helper.Validatable
import ru.korundm.helper.ValidatorErrors
import ru.korundm.util.FileStorageUtil.validateFile
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/** Длина идентификатора государственного контракта  */
private const val IDENTIFIER_LENGTH = 25
class ContractEditForm(
    var id: Long? = null,
    var customerId: Long? = null, // идентификатор заказчика
    var customerTypeId: Long = CustomerTypeEnum.EXTERNAL.id, // тип заказчика
    var contractType: ContractType? = null,// тип контракта
    @DateTimeFormat(pattern = DATE_PATTERN)
    var createDate: LocalDate? = null, // дата создания
    var archive: Boolean = false, // архивный
    var comment: String = "", // комментарий
    @DateTimeFormat(pattern = DATE_PATTERN)
    var sendToClientDate: LocalDate? = null, // дата отправки заказчику
    var identifier: String? = null, // номер идентификатора
    var externalNumber: String? = null, // внешний номер
    var account: Account? = null, // расчетный счет
    @DateTimeFormat(pattern = DATE_PATTERN)
    var archiveDate: LocalDate? = null, // дата помещения в архив
    var manager: User? = null, // ведущий договор
    var classDisable: String = "" //
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        comment = comment.trim()
        if (comment.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
        if (customerId == null) errors.putError(::customerId.name, ValidatorMsg.REQUIRED)
        if (identifier?.isBlank() == false) {
            val identifierTrim = identifier!!.trim { it <= ' ' }
            if (identifierTrim.length != IDENTIFIER_LENGTH) {
                errors.putError(::identifier.name, ValidatorMsg.FIX_DIGITS_LENGTH, IDENTIFIER_LENGTH)
            } else if (!identifierTrim.matches(ONLY_DIGITAL_PATTERN.toRegex())) {
                errors.putError(::identifier.name, "validator.form.digits")
            }
        }
        if (createDate == null) errors.putError(::createDate.name, ValidatorMsg.REQUIRED)
    }
}

class ContractEditSectionForm(
    var id: Long? = null,
    var sectionNumber: Int? = null, // номер секции договора
    @DateTimeFormat(pattern = DATE_PATTERN)
    var createDate: LocalDate? = null, // дата создания
    var externalNumber: String? = null, // внешний номер
    var archive: Boolean = true, // архивный
    var comment: String? = null, // комментарий
    var identifier: String? = null, // номер идентификатора
    @DateTimeFormat(pattern = DATE_PATTERN)
    var archiveDate: LocalDate? = null, // дата помещения в архив
    @DateTimeFormat(pattern = DATE_PATTERN)
    var sendToClientDate: LocalDate? = null, // дата отправки заказчику
    var manager: User? = null // ведущий договор
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (identifier?.isBlank() == false) {
            val identifierTrim = identifier!!.trim { it <= ' ' }
            if (identifierTrim.length != IDENTIFIER_LENGTH) {
                errors.putError(::identifier.name, ValidatorMsg.FIX_DIGITS_LENGTH, IDENTIFIER_LENGTH)
            } else if (!identifierTrim.matches(ONLY_DIGITAL_PATTERN.toRegex())) {
                errors.putError(::identifier.name, "validator.form.digits")
            }
        }
    }
}

class ContractProductsFilterForm(
    var conditionalName: String = "", // условное наименование
    @JsonFormat(pattern = DATE_PATTERN)
    var deliveryDateFrom: LocalDate? = null, // дата поставки с
    @JsonFormat(pattern = DATE_PATTERN)
    var deliveryDateTo: LocalDate? = null, // дата поставки по
    var typeIdList: List<Long> = emptyList(), // список идентификаторов кратких технических хар-к
    var excludeProductIdList: List<Long> = emptyList(), // список изделий для исключения из поиска
    var excludeProductTypeIdList: List<Long> = emptyList() // список типов изделий для исключения из поиска
)

class ContractDeliveryStatementEditProductForm(
    var sectionId: Long? = null,
    var productId: Long? = null, // идентификатор изделия
    var productPrice: BigDecimal? = null,
    var lotGroupId: Long? = null, // идентификатор вида работ
    var serviceTypeId: Long? = null, // тип услуги
    var lotId: Long? = null, // идентификатор Lot
    var amount: Long = ONE_LONG, // количество изделий в lot-e
    var acceptType: ProductAcceptType? = null, // тип приёмки изделий (enum ProductAcceptType.kt)
    var specialTestType: SpecialTestType? = null, // тип спецпроверки (enum SpecialTestType.kt)
    @DateTimeFormat(pattern = DATE_PATTERN)
    var deliveryDate: LocalDate? = null, // дата поставки
    var price: BigDecimal? = null, // цена
    var priceKind: PriceKindType? = null, // тип цены (enum PriceKindType.kt)
    var protocolId: Long? = null, // протокол
    var protocolNumber: String? = null, // номер протокола
    @DateTimeFormat(pattern = DATE_PATTERN)
    var protocolDate: LocalDateTime? = null, // дата протокола
    var productChargesProtocol: Long? = null // протокол
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (amount < 1) errors.putError(::amount.name, ValidatorMsg.REQUIRED)
        if (deliveryDate == null) errors.putError(::deliveryDate.name, ValidatorMsg.REQUIRED)
        if (price == null || price!! < BigDecimal.ONE) errors.putError(::price.name, ValidatorMsg.REQUIRED)
    }
}

class ContractInvoicesFilterForm(
    var invoiceNumber: Long? = null, // номер счета
    @JsonFormat(pattern = DATE_PATTERN)
    var invoiceDateFrom: LocalDate? = null, // дата счета с
    @JsonFormat(pattern = DATE_PATTERN)
    var invoiceDateTo: LocalDate? = null, // дата счета по
    var invoiceStatusList: List<Long> = emptyList() // список статусов счетов
)

class ContractEditInvoiceForm(
    var id: Long? = null,
    var sectionId: Long? = null,
    var invoiceType: Long? = null, // тип счета (enum InvoiceType.kt)
    var percentAdvance: Int? = null, // целое число (% от аванса)
    var forAmountInvoice: BigDecimal? = null, // сумма для типа счета "на выставленную сумму"
    @DateTimeFormat(pattern = DATE_PATTERN)
    var goodThroughDate: LocalDate? = null, // дата, до которой действителен счет
    @DateTimeFormat(pattern = DATE_PATTERN)
    var productionDate: LocalDate? = null, // ориентировочная дата, к которой будут изготовлены изделия, указанные в счете
    var invoiceAmount: BigDecimal? = null, // сумма счета в формате [руб,коп]
    var accountId: Long? = null, // идентификатор расчетного счета
    var bank: Bank? = null,
    @DateTimeFormat(pattern = DATE_PATTERN)
    var invoiceDate: LocalDate? = null, // дата создания счета
    var paidAmount: BigDecimal? = null, // сумма, на которую оплачен счет
    var note: String? = null, // комментарий
    var invoiceStatus: Long? = null, // статус счета (enum InvoiceStatus.kt)
    var invoiceNumber: Long? = null, // номер счета
    var documentNote: String? = null, // документ
    var preliminaryPriceId: Long? = null, // идентификатор предварительной цены
    var allotmentIdList: String = "", // json строка со списком идентификаторов выбранных allotment-ов
    var invoiceForAmountDialog: Boolean = false // флаг отображения модального окна о продолжении добавления счета (или отмены добавления счета)
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (invoiceType == InvoiceType.INVOICE_FOR_AMOUNT.id && (forAmountInvoice == null || forAmountInvoice!! < BigDecimal.ONE)) {
            errors.putError(::forAmountInvoice.name, ValidatorMsg.REQUIRED)
        }
        if (invoiceNumber == null || invoiceNumber!! < 1) errors.putError(::invoiceNumber.name, ValidatorMsg.REQUIRED)
        if (invoiceDate == null) errors.putError(::invoiceDate.name, ValidatorMsg.REQUIRED)
        if (goodThroughDate == null) errors.putError(::goodThroughDate.name, ValidatorMsg.REQUIRED)
        if (productionDate == null) errors.putError(::productionDate.name, ValidatorMsg.REQUIRED)
        if (accountId == null) errors.putError(::accountId.name, ValidatorMsg.REQUIRED)
    }
}

class ContractPaidProductsFilterForm(
    var conditionalName: String = "", // условное наименование
    @JsonFormat(pattern = DATE_PATTERN)
    var deliveryDateFrom: LocalDate? = null, // дата поставки с
    @JsonFormat(pattern = DATE_PATTERN)
    var deliveryDateTo: LocalDate? = null // дата поставки по
)

class ContractEditPaymentForm(
    var id: Long? = null,
    var sectionId: Long? = null,
    var number: String? = null, // номер платежного поручения
    @DateTimeFormat(pattern = DATE_PATTERN)
    var date: LocalDate? = null, // дата платежа
    var amount: BigDecimal? = null, // сумма
    var advanceInvoice: String? = null, // счет-фактура на аванс
    var note: String? = null, // комментарий
    var invoice: Invoice? = null, // счета секции
    var accountPayerId: Long? = null, // идентификатор расчетного счета плательщика
    var unallocatedAmount: BigDecimal? = null, // нераспределенная сумма
    var allocatedAmount: BigDecimal? = null, // распределяемая сумма
    var payer: String? = null, // плательщик
    var distributionAlgorithmType: DistributionAlgorithmType? = null // алгоритм распределения
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (date == null) errors.putError(::date.name, ValidatorMsg.REQUIRED)
        if (amount == null) errors.putError(::amount.name, ValidatorMsg.REQUIRED)
        if (payer == null) errors.putError(::payer.name, ValidatorMsg.REQUIRED)
        if (invoice == null) errors.putError(::invoice.name, ValidatorMsg.REQUIRED)
        if (accountPayerId == null) errors.putError(::accountPayerId.name, ValidatorMsg.REQUIRED)
    }
}

class ContractEditPaymentDistributionForm(
    var id: Long? = null,
    var sectionId: Long? = null,
    var unallocatedAmount: BigDecimal? = null, // нераспределенная сумма
    var allocatedAmount: BigDecimal? = null, // распределяемая сумма
    var distributionAlgorithmType: Long? = null, // алгоритм распределения
    var paidProductsTableData: String = "" // json строка - данные таблицы оплачиваемых изделий
) {

    class PaidProductsTableData(
        val id: Long? = null, // идентификатор
        val cost: String? = null, // стоимость
        var paid: String? = null, // оплачено (руб.)
        var percentPaid: String? = null, // оплачено (%)
        var unallocatedAmount: String? = null, // нераспределенная сумма
        var allocatedAmount: String? = null // распределяемая сумма
    )
}

class ContractEditDocumentationForm(
    var id: Long? = null, // идентификатор
    var sectionId: Long? = null, // секция договора
    var name: String? = null, // наименование
    var comment: String? = null, // комментарий
    var fileStorage: FileStorage<ContractSectionDocumentation, SingularFileStorableType>? = null, // файл
    var file: MultipartFile? = null // файл
) : Validatable {

    override fun validate(errors: ValidatorErrors) {
        if (StringUtils.isBlank(name) || name!!.length > 128) errors.putError(::name.name, ValidatorMsg.RANGE_LENGTH, 1, 128)
        if (StringUtils.isNotBlank(comment) && comment!!.length > 256) errors.putError(::comment.name, ValidatorMsg.RANGE_LENGTH, 0, 256)
        validateFile(errors, fileStorage, file, "file", true)
    }
}