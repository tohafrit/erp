package ru.korundm.enumeration

/**
 * Типы служебных символов
 */
enum class ServiceSymbolType(
    val code: String,
    val property: String
) {

    A("A", "А"),
    B("B", "Б"),
    M("M", "М"),
    O("O", "О"),
    R("R", "Р"),
    T("T", "Т");

    companion object {
        fun getByCode(code: String) = values().firstOrNull { it.code == code }
    }
}