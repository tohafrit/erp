package ru.korundm.form

class CompanyListFilterForm (
    var name: String = "", // наименование
    var typeId: Long? = null, // тип компании
    var inn: String = "", // ИНН
    var kpp: String = "", // КПП
    var location: String = "", // местоположение
    var mailAddress: String = "", // адрес
)