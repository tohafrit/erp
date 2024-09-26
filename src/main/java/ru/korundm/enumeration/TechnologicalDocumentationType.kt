package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class TechnologicalDocumentationType(val id: Long, val property: String) : EnumConvertible<Long> {

    TECHNOLOGICAL_SHEET(1, "Карта техпроцессов"),
    ROUTE_SHEET(2, "Маршрутная карта"),
    OPERATION_SHEET(3, "Операционная карта");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<TechnologicalDocumentationType, Long>(TechnologicalDocumentationType::class.java)
}