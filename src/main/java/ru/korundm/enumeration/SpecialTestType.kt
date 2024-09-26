package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class SpecialTestType(
    val id: Long,
    val property: String,
    val code: String
) : EnumConvertible<Long> {

    WITHOUT_CHECKS(0, "specialTestType.withoutChecks", "-"),
    SPECIAL_CHECK(1, "specialTestType.specialCheck", "СП"),
    SPECIAL_STUDIES(2, "specialTestType.specialStudies", "СИ"),
    SPECIAL_CHECK_AND_SPECIAL_STUDIES(3, "specialTestType.specialCheckAndSpecialStudies", "СП и СИ");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<SpecialTestType, Long>(SpecialTestType::class.java)
}