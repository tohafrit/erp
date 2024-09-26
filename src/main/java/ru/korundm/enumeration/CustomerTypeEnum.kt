package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class CustomerTypeEnum(
    val id: Long,
    val description: String
)  : EnumConvertible<Long> {

    EXTERNAL(1, "Внешний"),
    INTERNAL(2, "Внутренний");

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<CustomerTypeEnum, Long>(CustomerTypeEnum::class.java)
}