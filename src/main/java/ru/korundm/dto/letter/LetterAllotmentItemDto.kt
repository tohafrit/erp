package ru.korundm.dto.letter

import java.time.LocalDate

class LetterAllotmentItemDto(
    var id: Long? = null, // идентификатор contractSection
    var contractNumber: Int = 0, // порядковый номер основного договора
    var performer: Long = 0, // исполнитель договора
    var type: Long = 0, // тип договора
    var year: Int = 0, // год создания
    var number: Int = 0, // номер секции договора
    var name: String = "", // заказчик
    var productName: String? = "", // комплектность поставки
    var deliveryDate: LocalDate? = LocalDate.MIN, // дата поставки
    var orderIndex: Long? = 0, //пункт ведомости поставки
    var acceptType: Long = 0, // идентификатор типа приемки
    var specialTestType: Long? = 0, // идентификатор типа специальной проверки
    var amount: Long? = 0, // количество
    //
    var groupMain: String? = "", // название главной группы группировки столбцов
    var sectionFullNumber: String? = "", // название главной группы группировки столбцов
    var acceptTypeCode: String? = "", // тип приемки
    var specialTestTypeCode: String? = "" // тип специальной проверки
)