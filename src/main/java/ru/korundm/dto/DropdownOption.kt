package ru.korundm.dto

/**
 * Класс для хранения данных пункта выпадающего списка
 * @author mazur_ea
 * Date:   20.12.2020
 */
class DropdownOption(
    var id: Long? = 0,
    var value: String? = "",
    var selected: Boolean = false
)