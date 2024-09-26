package ru.korundm.form

class LetterContractFilterForm(
    var number: String = "", // номер договора
    var customer: String = "", // заказчик
    var pzCopy: Boolean? = null, // передано в ПЗ
    var available: Boolean? = null, // доступные
    var notArchived: Boolean = false, // договор не убран в архив
    var contractTypeIdList: List<Long> = emptyList() // список типов договоров
)