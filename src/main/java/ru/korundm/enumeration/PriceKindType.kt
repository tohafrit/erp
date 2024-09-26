package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class PriceKindType(
    val id: Long,
    val property: String,
    val description: String
) : EnumConvertible<Long> {

    PRELIMINARY(1, "priceKind.preliminary", "Предварительная"),
    FINAL(2, "priceKind.onProtocol", "Окончательная"),
    // TODO Для договоров типа "ППЭ": вид цены для lot-a и allotment-a выводить "Экспортная"
    // TODO Для договоров остальных типов: вид цены для lot-a и allotment-a выводить "Фиксированная" (erp_en(ru)US(RU).properties = priceKind.fixed)
    EXPORT(4, "priceKind.export", "Экспортная / Фиксированная"),
    STATEMENT(8, "priceKind.statement", "По ведомости");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<PriceKindType, Long>(PriceKindType::class.java)
}