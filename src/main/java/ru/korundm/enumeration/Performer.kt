package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter

enum class Performer(
    val id: Long,
    val property: String,
    val code: String,
    val prefix: String
) : EnumConvertible<Long> {

    NIISI(1, "performer.niisi", "SRISA", "НС"),
    KORUND(2, "performer.korund", "KORUND", "КМ"),
    OAOKORUND(4, "performer.oaoKorund", "OAOKORUND", "КБ");

    companion object {
        fun getById(id: Long) = values().first { it.id == id }
        fun getByPrefix(prefix: String) = values().first { it.prefix == prefix }
    }

    override fun toValue()= id

    @Converter
    class CustomConverter : EnumConverter<Performer, Long>(Performer::class.java)
}