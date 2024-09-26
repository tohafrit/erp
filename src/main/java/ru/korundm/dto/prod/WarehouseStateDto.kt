package ru.korundm.dto.prod

import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.enumeration.ContractType
import ru.korundm.enumeration.Performer
import ru.korundm.enumeration.ProductAcceptType
import ru.korundm.helper.RowCountable
import ru.korundm.util.KtCommonUtil.blankIfNullOr
import ru.korundm.util.KtCommonUtil.contractFullNumber
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.padStartZero
import java.time.LocalDate

class WarehouseStateListDto(
    val id: Long?,
    val product: String?, // изделие
    val amount: Int? // кол-во
)

class WarehouseStateMatValueDto(
    val id: Long? = null,
    val serialNumber: String? = null, // серийный номер
    val contractNumber: Int? = null, // номер договора
    val contractPerformer: Long? = null, // организация-исполнитель договора
    val contractType: Long? = null, // тип договора
    val sectionYear: Int? = null, // год секции договора
    val sectionNumber: Int? = null, // номер секции договора
    val customer: String? = null, // заказчик
    val letter: Int? = null, // номер письма
    val acceptTypeId: Long? = null, // тип приемки
    val noticeNumber: String? = null, // номер извещения
    val noticeDate: LocalDate? = null, // дата извещения
    val internalWaybillNumber: Int? = null, // номер МСН
    val acceptDate: LocalDate? = null, // дата принятия
    val price: Double? = null, // цена
    val shipmentDate: LocalDate? = null, // дата отгрузки
    val cell: String? = null, // ячейка
    val place: String? = null, // местоположение
    val shipped: Boolean? = null, // отгружено
    val rowCount: Long = 0 // счетчик строк для пагинации
) : RowCountable {
    val internalWaybill
        get() = "${internalWaybillNumber.toString().padStartZero(3)}/АО"
    val contract
        get() = contractFullNumber(contractNumber, contractPerformer, contractType, sectionYear, sectionNumber)
    val acceptType
        get() = ProductAcceptType.getById(acceptTypeId!!).code
    val notice
        get() = noticeNumber.blankIfNullOr{ "$it от ${noticeDate?.format(DATE_FORMATTER)}" }
    override fun rowCount() = rowCount
}

class WarehouseStateResidueDocDto(
    val productId: Long,
    val productName: String, // изделие
    val serialNumber: String?, // серийный номер
    val customer: String?, // заказчик
    val contractNumber: Int, // номер договора
    val contractPerformer: Performer, // организация-исполнитель договора
    val contractType: ContractType, // тип договора
    val sectionYear: Int, // год секции договора
    val sectionNumber: Int, // номер секции договора
    val cell: String?, // ячейка
    val noticeNumber: String?, // номер извещения
    val noticeDate: LocalDate? // дата извещения
) {
    val contract
        get() = contractFullNumber(contractNumber, contractPerformer.id, contractType.id, sectionYear, sectionNumber)
    val notice
        get() = noticeNumber.blankIfNullOr{ "$it от ${noticeDate?.format(DATE_FORMATTER)}" }
}

class WarehouseStateListFirstReportProductDto(
    val id: Long?,
    val product: String?, // изделие
    val type: String?, // краткая тех. хар-ка
    val serial: Boolean?, // серийное
    val rowCount: Long = 0 // счетчик строк для пагинации
) : RowCountable {
    override fun rowCount() = rowCount
}

class WarehouseStateReportFirstDocDto(
    val contractNumber: Int, // номер договора
    val contractPerformer: Performer, // организация-исполнитель договора
    val contractType: ContractType, // тип договора
    val sectionYear: Int, // год секции договора
    val sectionNumber: Int, // номер секции договора
    val acceptDate: LocalDate? = null, // дата принятия
    val internalWaybillNumber: Int? = null, // номер МСН
    val plrNumber: Int? = null, // номер предъявления
    val plrDate: LocalDate? = null, // дата предъявления
    val serialNumber: String?, // серийный номер
    val shipmentDate: LocalDate? = null, // дата отгрузки
    val shipmentWaybillNumber: Int? = null, // номер документа отгрузки
    val customer: String? = null // заказчик
) {
    val contract
        get() = contractFullNumber(contractNumber, contractPerformer.id, contractType.id, sectionYear, sectionNumber)
    val internalWaybill
        get() = "№${internalWaybillNumber.toString().padStartZero(3)}/АО"
    val presentation
        get() = "№$plrNumber от ${plrDate?.format(DATE_FORMATTER) ?: ""}"
    val shipmentWaybill
        get() = shipmentWaybillNumber?.let { "№$it" } ?: ""
}

class WarehouseStateReportSecondDocDto(
    val productName: String?, // изделие
    val serialNumber: String?, // серийный номер
    val externalNumber: String?, // внешний номер договора
    val contractNumber: Int, // номер договора
    val contractPerformer: Performer, // организация-исполнитель договора
    val contractType: ContractType, // тип договора
    val sectionYear: Int, // год секции договора
    val sectionNumber: Int, // номер секции договора
    val customer: String?, // заказчик
    val shipmentDate: LocalDate? // дата отгрузки
) {
    val orderNumber
        get() = externalNumber.nullIfBlank() ?: contractFullNumber(contractNumber, contractPerformer.id, contractType.id, sectionYear, sectionNumber)
}