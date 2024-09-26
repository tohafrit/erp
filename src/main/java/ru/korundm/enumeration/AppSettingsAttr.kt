package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter as PersistenceConverter

enum class AppSettingsAttr(val id: Long) : EnumConvertible<Long> {

    PRODUCT_PRICE_UNLOCK_TIME(1);

    override fun toValue() = id

    @PersistenceConverter
    class Converter : EnumConverter<AppSettingsAttr, Long>(AppSettingsAttr::class.java)
}