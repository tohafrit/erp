package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class ProductAcceptType(
    val id: Long,
    val property: String,
    val code: String
) : EnumConvertible<Long> {

    OTK(1, "productAcceptType.otk", "ОТК"),
    PZ(2, "productAcceptType.pz", "ПЗ"),
    PZ_MANUFACTURER(4, "productAcceptType.pzManufacturer", "ПЗ изг"),
    PERIODIC_TEST(8, "productAcceptType.periodicTest", "Исп");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<ProductAcceptType, Long>(ProductAcceptType::class.java)
}