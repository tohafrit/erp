package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class LotGroupKind(
    val id: Long,
    val property: String,
    val description: String
) : EnumConvertible<Long> {

    MANUFACTURING(1, "lotGroupKind.manufacturing", "Изготовление"),
    REPAIRS(2, "lotGroupKind.repairs", "Ремонт"),
    REWORK(4, "lotGroupKind.rework", "Доработка");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<LotGroupKind, Long>(LotGroupKind::class.java)
}