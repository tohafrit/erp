package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class TechnologicalToolType(val id: Long, val property: String) : EnumConvertible<Long> {

    TOOLING(1, "technologicalToolType.tooling"),
    INSTRUMENT(2, "technologicalToolType.instrument");

    companion object {
        fun getById(id: Long): TechnologicalToolType = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<TechnologicalToolType, Long>(TechnologicalToolType::class.java)
}