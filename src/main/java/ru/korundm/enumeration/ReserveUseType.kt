package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class ReserveUseType(val id: Long, val property: String) : EnumConvertible<Long> {

    FIRST_PERIOD(1, "Сначала более ранний период"),
    LATER_PERIOD(2, "Сначала более поздний период"),
    EVENLY(3, "Равномерно");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<ReserveUseType, Long>(ReserveUseType::class.java)
}