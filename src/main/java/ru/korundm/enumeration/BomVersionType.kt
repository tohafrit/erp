package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class BomVersionType(val id: Long, val property: String) : EnumConvertible<Long> {

    LAST_ADDED(1, "Последние добавленные"),
    LAST_APPROVED(2, "Последние утвержденные"),
    APPROVED_FOR_LAUNCH(3, "Утвержденные к запуску");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<BomVersionType, Long>(BomVersionType::class.java)
}