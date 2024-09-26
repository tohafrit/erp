package ru.korundm.form.search

class ProductListFilterForm {

    var conditionalName: String = "" // условное наименование
    var techSpecName: String = "" // наименование по ТС
    var decimalNumber: String = "" // ТУ изделия
    var position: String = "" // позиция
    var descriptor: Int? = null // идентификатор
    var prefix: String = "" // префикс
    var comment: String = "" // комментарий
    var active = true // выпускаемые изделия
    var archive = false // устаревшие изделия
    var serial: Boolean? = null // серийное
    var typeIdList: List<Long> = ArrayList() // список идентификаторов кратких технических хар-к
    var letterIdList: List<Long> = ArrayList() // список идентификаторов литер
    var leadIdList: List<Long> = ArrayList() // список идентификаторов ведущих
    var classificationGroupIdList: List<Long> = ArrayList() // список идентификаторов классификационных групп
    var excludeProductIdList: List<Long> = ArrayList() // список изделий для исключения из поиска
    var excludeProductTypeIdList: List<Long> = ArrayList() // список типов изделий для исключения из поиска
}
