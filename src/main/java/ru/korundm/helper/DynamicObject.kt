package ru.korundm.helper

import com.fasterxml.jackson.annotation.JsonAnySetter
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.BaseConstant.DATE_TIME_FORMATTER
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.ONE_LONG
import ru.korundm.constant.BaseConstant.ZERO_INT
import ru.korundm.constant.BaseConstant.ZERO_LONG
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

/**
 * Класс для представления объекта с динамическими атрибутами
 * Может быть использован в качестве объекта десереализации JSON
 * @author mazur_ea
 * Date:   03.04.2021
 */
class DynamicObject {

    private companion object {
        val boolTrueList = listOf("1", "on", "true")
        val boolFalseList = listOf("0", "false")
    }

    private val values = mutableMapOf<String, Any?>()

    @JsonAnySetter
    operator fun set(attr: String, value: Any?) = values.set(attr, value)

    operator fun get(attr: String) = values[attr]

    fun string(attr: String) = parseString(this[attr])

    fun stringNotNull(attr: String, default: String = "") = string(attr) ?: default

    @JvmName("intValue")
    fun int(attr: String) = parseInt(this[attr])

    fun intNotNull(attr: String, default: Int = 0) = int(attr) ?: default

    @JvmName("longValue")
    fun long(attr: String) = parseLong(this[attr])

    fun longNotNull(attr: String, default: Long = 0) = long(attr) ?: default

    fun bool(attr: String) = parseBoolean(this[attr])

    fun boolNotNull(attr: String, default: Boolean = false) = bool(attr) ?: default

    fun date(attr: String) = parseLocalDate(this[attr])

    fun dateNotNull(attr: String, default: LocalDate = LocalDate.now()) = date(attr) ?: default

    fun dateTime(attr: String) = parseLocalDateTime(this[attr])

    fun dateTimeNotNull(attr: String, default: LocalDateTime = LocalDateTime.now()) = dateTime(attr) ?: default

    fun bigDecimal(attr: String) = parseBigDecimal(this[attr])

    fun bigDecimalNotNull(attr: String, default: BigDecimal = BigDecimal.ZERO) = bigDecimal(attr) ?: default

    fun double(attr: String) = parseDouble(this[attr])

    fun doubleNotNull(attr: String, default: Double = .0) = double(attr) ?: default

    fun listString(attr: String) = parseList(attr) { parseString(it) }.filterNotNull()

    fun listInt(attr: String) = parseList(attr) { parseInt(it) }.filterNotNull()

    fun listLong(attr: String) = parseList(attr) { parseLong(it) }.filterNotNull()

    fun listBool(attr: String) = parseList(attr) { parseBoolean(it) }.filterNotNull()

    private fun parseString(value: Any?) = value as? String

    private fun parseInt(value: Any?) = when (value) {
        is Int -> value
        is Long -> value.toInt()
        is String -> value.trim().toIntOrNull()
        else -> null
    }

    private fun parseLong(value: Any?) = when (value) {
        is Long -> value
        is Int -> value.toLong()
        is String -> value.trim().toLongOrNull()
        else -> null
    }

    private fun parseBoolean(value: Any?) = when (value) {
        is Boolean -> value
        is Int -> when (value) {
            ZERO_INT -> false
            ONE_INT -> true
            else -> null
        }
        is Long -> when (value) {
            ZERO_LONG -> false
            ONE_LONG -> true
            else -> null
        }
        is String -> when (value.trim()) {
            in boolTrueList -> true
            in boolFalseList -> false
            else -> null
        }
        else -> null
    }

    private fun parseLocalDate(value: Any?) = when (value) {
        is LocalDate -> value
        is LocalDateTime -> value.toLocalDate()
        is String -> try { LocalDate.parse(value, DATE_FORMATTER) } catch (e: DateTimeParseException) { null }
        else -> null
    }

    private fun parseLocalDateTime(value: Any?) = when (value) {
        is LocalDateTime -> value
        is LocalDate -> value.atStartOfDay()
        is String -> try { LocalDateTime.parse(value, DATE_TIME_FORMATTER) } catch (e: DateTimeParseException) { null }
        else -> null
    }

    private fun parseBigDecimal(value: Any?) = when (value) {
        is BigDecimal -> value
        is Long -> value.toBigDecimal()
        is Int -> value.toBigDecimal()
        is String -> value.trim().toBigDecimalOrNull()
        else -> null
    }

    private fun parseDouble(value: Any?) = when (value) {
        is Double -> value
        is Long -> value.toDouble()
        is Int -> value.toDouble()
        is String -> value.trim().toDoubleOrNull()
        else -> null
    }

    private fun <T> parseList(attr: String, transform: (value: Any?) -> T): List<T> {
        val value = this[attr]
        return transform(value)?.let { listOf(it) } ?: when (value) {
            is List<*> -> value.map(transform).toList()
            is String -> value.split(",").map(transform).toList()
            else -> emptyList()
        }
    }
}