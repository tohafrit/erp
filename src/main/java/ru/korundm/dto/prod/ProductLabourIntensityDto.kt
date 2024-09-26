package ru.korundm.dto.prod

class ProductLabourIntensityExcelImportDto {

    var resultList = mutableListOf<Result>()
    var errorList = mutableListOf<Error>()

    data class Result(
        var id: Int = 0,
        var productId: Long? = null,
        var entryId: Long? = null, // id общей трудоемкости
        var entryOpId: Long? = null, // id вида работ, который связан с общей трудоемкостью
        var opId: Long? = null, // id вида работы
        var productName: String = "",
        var productDecNumber: String = "",
        var opName: String = "", // Наименование вида работы
        var oldVal: Double = .0, // значение трудоемкости по виду работы в системе
        var newVal: Double = .0, // значение трудоемкости, полученное из разбора документа
        var finalVal: Double = .0 // значение трудоемкости, выбранное пользователем в качестве добавляемого в систему
    ) {
        val isValid
            get() = entryId == null || oldVal == newVal
    }

    data class Error(var msg: String = "")
}

class ProductLabourIntensityExcelImportProductDto {

    var id = 0L
    var entryId: Long? = null // id трудоемкости
    var operationList = emptyList<Operation>() // список видов работ

    data class Operation(
        var id: Long? = null, // id вида работы со значением в системе
        var opId: Long = 0L, // id вида работы
        var oldVal: Double = .0, // значение трудоемкости по виду работы в системе
        var newVal: Double = .0, // значение трудоемкости, полученное из разбора документа
        var finalVal: Double = .0 // значение трудоемкости, выбранное пользователем в качестве добавляемого в систему
    )
}