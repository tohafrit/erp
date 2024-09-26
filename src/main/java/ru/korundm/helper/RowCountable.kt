package ru.korundm.helper

/**
 * Интерфейс для сущностей со счетчиком записей
 */
interface RowCountable {

    fun rowCount(): Long
}