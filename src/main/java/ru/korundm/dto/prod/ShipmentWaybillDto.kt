package ru.korundm.dto.prod

import ru.korundm.enumeration.ContractType
import ru.korundm.enumeration.Performer
import ru.korundm.helper.RowCountable
import ru.korundm.util.KtCommonUtil.contractFullNumber
import ru.korundm.util.KtCommonUtil.currencyFormat
import ru.korundm.util.KtCommonUtil.padStartZero
import ru.korundm.util.KtCommonUtil.userFullName
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate

class ShipmentWaybillListDto(
    val id: Long? = null,
    val number: Int? = null, // номер
    val createDate: LocalDate? = null, // дата создания
    val shipmentDate: LocalDate? = null,// дата отгрузки
    val externalNumber: String? = null, // внешний номер договора
    val contractNumber: Int? = null, // номер договора
    val contractPerformer: Long? = null, // организация-исполнитель договора
    val contractType: Long? = null, // тип договора
    val sectionYear: Int? = null, // год секции договра
    val sectionNumber: Int? = null, // номер секции договра
    val account: String? = null, // расчетный счет
    val payer: String? = null, // плательщик
    val consignee: String? = null, // грузополучатель
    val vat: String? = null, // НДС
    val totalWoVat: Double? = null, // всего по накладной
    val totalVat: Double? = null, // всего с учетом НДС
    val giveUserLastName: String? = null,
    val giveUserFirstName: String? = null,
    val giveUserMiddleName: String? = null,
    val permitUserLastName: String? = null,
    val permitUserFirstName: String? = null,
    val permitUserMiddleName: String? = null,
    val accountantUserLastName: String? = null,
    val accountantUserFirstName: String? = null,
    val accountantUserMiddleName: String? = null,
    val transmittalLetter: String? = null, // сопроводительное письмо
    val receiver: String? = null,
    val letterOfAttorney: String? = null, // доверенность
    val comment: String? = null, // комментарий
    val rowCount: Long = 0 // счетчик строк для пагинации
) : RowCountable {
    val contract
        get() = "${if (externalNumber.isNullOrBlank()) "" else "${externalNumber}/"}${contractFullNumber(contractNumber, contractPerformer, contractType, sectionYear, sectionNumber)}"
    val giveUser // отпуск произвел
        get() = userFullName(giveUserLastName, giveUserFirstName, giveUserMiddleName)
    val permitUser // отпуск разрешил
        get() = userFullName(permitUserLastName, permitUserFirstName, permitUserMiddleName)
    val accountantUser // главный бухгалтер
        get() = userFullName(accountantUserLastName, accountantUserFirstName, accountantUserMiddleName)
    val canShipment// флаг возможности выполнения отгрузки
        get() = shipmentDate == null
    override fun rowCount() = rowCount
}

class ShipmentWaybillListAddContractDto(
    val id: Long?,
    val contractNumber: Int?, // номер договора
    val contractPerformer: Performer?, // организация-исполнитель договора
    val contractType: ContractType?, // тип договора
    val sectionYear: Int?, // год секции договра
    val sectionNumber: Int?, // номер секции договра
    val externalNumber: String?, // внутренний номер договора
    val createDate: LocalDate?, // дата создания
    val customer: String?, // заказчик
    val customerId: Long?, // id заказчика
    val ready: Boolean?, // готовность к отгрузке
    val rowCount: Long = 0 // счетчик строк для пагинации
) : RowCountable {
    val number
        get() = contractFullNumber(contractNumber, contractPerformer?.id, contractType?.id, sectionYear, sectionNumber)
    val fullNumber
        get() = "${if (externalNumber.isNullOrBlank()) "" else "${externalNumber}/"}$number"
    override fun rowCount() = rowCount
}

class ShipmentWaybillMatValueDto(
    val id: Long? = null,
    val productId: Long? = null,
    val productName: String? = null, // изделие
    val serialNumber: String? = null, // серийный номер
    val amount: Int = 0, // кол-во
    val price: Double = .0, // цена
    val shipped: Boolean = false, // отгружено
    val checked: Boolean = false, // проверено
    val rowCount: Long = 0 // счетчик строк для пагинации
) : RowCountable {
    val cost
        get() = (amount.toBigDecimal() * price.toBigDecimal()).setScale(2, HALF_UP).toDouble().currencyFormat()
    override fun rowCount() = rowCount
}

class ShipmentWaybillCheckShipmentDto(
    val id: Long?,
    val productId: Long?,
    val productName: String?, // изделие
    val serialNumber: String?, // серийный номер
    val cell: String?, // ячейка
    val checked: Boolean // проверено
)

class ShipmentWaybillMatValueAddDto(
    val id: Long? = null, // id предъявления
    val product: String? = null, // изделие
    val notice: String? = null, // извещение
    val intWaybillNumber: Int? = null, // номер МСН
    val amount: Int? = null, // кол-во
    val serialNumber: String? = null, // серийные номер
    val ready: Boolean? = null // готовность к отгрузке
) {
    val internalWaybill
        get() = "${intWaybillNumber.toString().padStartZero(3)}/АО"
}