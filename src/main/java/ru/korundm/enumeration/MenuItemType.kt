package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class MenuItemType(val id: Long) : EnumConvertible<Long> {

    PROD(1),
    CORP(2),
    CORP_HEADER(3),
    PROD_HEADER(4);

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<MenuItemType, Long>(MenuItemType::class.java)
}