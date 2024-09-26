package ru.korundm.enumeration

import ru.korundm.helper.converter.EnumConverter
import ru.korundm.helper.converter.EnumConvertible
import javax.persistence.Converter as PersistenceConverter

/**
 * Причины изменений для ТП, МК и ОК
 */
enum class ReasonType(
    val id: Long,
    val code: String,
    val property: String
) : EnumConvertible<Long> {

    REASON_1(1, "00", "Отработка документации"),
    REASON_2(2, "01", "Введение конструктивных улучшений и усовершенствований"),
    REASON_3(3, "02", "Введение технологических улучшений и усовершенствований"),
    REASON_4(4, "03", "Введение улучшений и усовершенствований в результате стандартизации и унификации"),
    REASON_5(5, "04", "Внедрение и изменение стандартов и технических условий"),
    REASON_6(6, "05", "По результатам испытаний"),
    REASON_7(7, "06", "Отработка документов с изменением литеры"),
    REASON_8(8, "07", "Устранение ошибок"),
    REASON_9(9, "08", "Улучшение качества"),
    REASON_10(10, "09", "Требование заказчика"),
    REASON_11(11, "10", "Улучшение схемы"),
    REASON_12(12, "11", "Улучшение электрического монтажа"),
    REASON_13(13, "12", "Изменение средств технологического оснащения"),
    REASON_14(14, "13", "Изменение условий труда"),
    REASON_15(15, "14", "Введение новых технологических процессов (операций)"),
    REASON_16(16, "15", "Замена исходной заготовки"),
    REASON_17(17, "16", "Изменение норм расхода материалов");

    companion object {
        fun getById(id: Long): ReasonType? = values().firstOrNull { it.id == id }
    }

    override fun toValue() = id

    @PersistenceConverter
    class CustomConverter : EnumConverter<ReasonType, Long>(ReasonType::class.java)
}
