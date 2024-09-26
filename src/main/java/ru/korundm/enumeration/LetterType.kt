package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class LetterType(
    val id: Long,
    val property: String
) : EnumConvertible<Long> {

    PRODUCTION_LETTERS(1, "letterType.productionLetters"),
    LETTERS_TO_THE_WAREHOUSE(2, "letterType.lettersToTheWarehouse");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
    }

    override fun toValue() = id

    @Converter
    class CustomConverter : EnumConverter<LetterType, Long>(LetterType::class.java)
}