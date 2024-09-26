package ru.korundm.controller.action.corp

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.SubdivisionService
import ru.korundm.entity.Subdivision
import ru.korundm.integration.onec.Constant.NULL
import ru.korundm.integration.onec.OnecUtil.isNotNull

@ActionController([RequestPath.Action.Corp.SUBDIVISION])
class SubdivisionActionCorpController(
    private val subdivisionService: SubdivisionService
) {
    // Загрузка штатного расписания
    @GetMapping("/list/load")
    fun listLoad(): List<*> = recursiveSubdivision(subdivisionService.getAllActive(NULL))

    /**
     * Метод для формирования списка подразделений с вложенными пунктами
     * @param subdivisionList список родительских подразделений
     * @return полное штатное расписание
     */
    private fun recursiveSubdivision(subdivisionList: List<Subdivision>): List<Item> = subdivisionList.map { subdivision ->
        Item(subdivision.name).apply {
            if (isNotNull(subdivision.onecId)) {
                val childList = subdivisionService.getAllActive(subdivision.onecId)
                if (childList.isNotEmpty()) {
                    this.childList = recursiveSubdivision(childList) as MutableList<Item>
                }
            }
        }
    }

    class Item(
        val name: String, // наименование
    ) {
        @JsonProperty("_children")
        var childList = mutableListOf<Item>() // список подразделений
    }
}