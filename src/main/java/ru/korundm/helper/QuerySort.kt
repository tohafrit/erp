package ru.korundm.helper

import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.Sort.Direction.ASC
import ru.korundm.constant.BaseConstant.COMMA

/**
 * Вспомогательный класс для составления строки сортировки в нативных запросах
 * @author mazur_ea
 * Date:   03.04.2021
 */
class QuerySort {

    private val values = mutableMapOf<String, Direction>()

    operator fun set(field: String, value: Direction) = values.set(field, value)

    fun set(field: String) = set(field, ASC)

    operator fun get(field: String) = values[field] ?: ASC

    fun queryString(includeOrderBy: Boolean = false, prefixComma: Boolean = false) =
        if (values.isNotEmpty()) "${if (includeOrderBy) "ORDER BY " else ""}${if (prefixComma) "$COMMA " else ""}${values.keys.joinToString("$COMMA ") { "CASE WHEN $it IS NULL THEN 1 ELSE 0 END$COMMA $it ${this[it].name}" }}" else ""
}