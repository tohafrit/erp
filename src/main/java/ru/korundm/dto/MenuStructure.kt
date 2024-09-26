package ru.korundm.dto

import java.util.*

/**
 * Класс для хранения элементов структуры состава меню
 * @author zhestkov_an
 * Date:   25.02.2021
 */
class MenuStructure(
    var id: Long? = null,
    var name: String = "", // наименование
    var href: String? = null, // ссылка
    var icon: String? = null, // иконка пунта меню
    var selected: Boolean = false, // пункт выбран
    var childList: List<Any> = ArrayList() // список дочерних пунктов меню
)