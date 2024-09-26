package ru.korundm.dto.prod

import ru.korundm.enumeration.ContractType
import ru.korundm.enumeration.Performer
import ru.korundm.helper.RowCountable
import ru.korundm.util.KtCommonUtil.contractFullNumber
import ru.korundm.util.KtCommonUtil.userFullName
import java.time.LocalDate

class InternalWaybillListDto(
    val id: Long,
    val number: Int, // номер
    val createDate: LocalDate, // дата создания
    val acceptDate: LocalDate?, // дата принятия
    val giveUserLastName: String?,
    val giveUserFirstName: String?,
    val giveUserMiddleName: String?,
    val acceptUserLastName: String?,
    val acceptUserFirstName: String?,
    val acceptUserMiddleName: String?,
    val storagePlace: String?, // место назначения
    val comment: String?, // комментарий
    val rowCount: Long = 0 // счетчик строк для пагинации
) : RowCountable {
    val giveUser
        get() = userFullName(giveUserLastName, giveUserFirstName, giveUserMiddleName)
    val acceptUser
        get() = userFullName(acceptUserLastName, acceptUserFirstName, acceptUserMiddleName)
    val fullNumber
        get() = "${number.toString().padStart(3, '0')}/АО"
    val canAccept
        get() = acceptDate == null
    override fun rowCount() = rowCount
}

class InternalWaybillMatValueDto(
    val id: Long,
    val product: String?, // изделие
    val serialNumber: String?, // серийный номер
    val cell: String?, // ячейка
    val letterId: Long?, // id письма
    val contractId: Long?, // id договора
    val sectionId: Long?, // id секции
    val contractNum: Int?, // номер договора
    val contractPerformer: Performer?, // организация-исполнитель договора
    val contractType: ContractType?, // тип договора
    val sectionYear: Int?, // год секции договора
    val sectionNum: Int?, // номер секции договора
    val customer: String?, // заказчик
    val notice: String?, // извещение
    val noticeDate: LocalDate?, // дата извещения
    val statement: String?, // номер заявления
    val letterNumber: Int?, // номер письма
    val location: String?, // местонахождение
    val rowCount: Long = 0 // счетчик строк для пагинации
) : RowCountable {
    val contract
        get() = contractFullNumber(contractNum, contractPerformer?.id, contractType?.id, sectionYear, sectionNum)
    val letter
        get() = "СЛ-${Performer.OAOKORUND.prefix}-$letterNumber"
    override fun rowCount() = rowCount
}

class InternalWaybillMatValueAddDto(
    val id: Long, // id предъявления
    val productId: Long, // id изделия
    val notice: String?, // извещение
    val product: String?, // изделие
    val amount: Int?, // кол-во
    val serialNumber: String?, // серийные номера
    val noticeDate: LocalDate?, // дата извещения
    val packDate: LocalDate?, // дата упаковки
    val techControlDate: LocalDate?, // дата прохождения ОТК
    val rowCount: Long = 0 // номер строки для пагинации
) : RowCountable {
    val packed
        get() = packDate != null
    val techControlled
        get() = techControlDate != null
    override fun rowCount() = rowCount
}