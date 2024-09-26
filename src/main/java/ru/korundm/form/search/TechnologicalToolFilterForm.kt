package ru.korundm.form.search

import ru.korundm.enumeration.TechnologicalToolType

class TechnologicalToolFilterForm(
    var name: String = "", // наименование
    var type: TechnologicalToolType? = null, // тип
    var productionAreaIdList: List<Long> = mutableListOf() // список участков
)