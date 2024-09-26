package ru.korundm.dto.prod

import ru.korundm.helper.RowCountable

class TechnologicalEntityListItem(
    val id: Long,
    val entityNumber: String, // номер сущности
    val setNumber: String, // номер комплекта
    val rowCount: Long = 0 // счетчик строк для пагинации
) : RowCountable {
    override fun rowCount() = rowCount
}
