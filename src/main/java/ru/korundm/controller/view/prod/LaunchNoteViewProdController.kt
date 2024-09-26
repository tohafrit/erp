package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.LaunchNoteService
import ru.korundm.dao.LaunchService
import ru.korundm.dao.UserService
import ru.korundm.dto.DropdownOption
import ru.korundm.entity.Launch
import ru.korundm.exception.AlertUIException
import java.time.LocalDate
import ru.korundm.form.LaunchNoteListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.LAUNCH_NOTE])
class LaunchNoteViewProdController(
    private val launchNoteService: LaunchNoteService,
    private val userService: UserService,
    private val launchService: LaunchService
) {

    @GetMapping("/list")
    fun list(model: ModelMap) = "prod/include/launch-note/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        model.addAttribute("userList", userService.all.map { user -> DropdownOption(user.id, user.userOfficialName) }.toList())
        return "prod/include/launch-note/list/filter"
    }

    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val note = id?.let { launchNoteService.read(it) ?: throw AlertUIException("Служебная записка не найдена") }
        model.addAttribute("form", ListEditForm(id).apply {
            note?.let { version = it.version }
            year = note?.year ?: LocalDate.now().year
            number = note?.number ?: launchNoteService.getLastNumber(year)
            createDate = note?.createDate?.format(DATE_FORMATTER)
            createdBy = note?.createdBy?.userOfficialName
            agreementDate = note?.agreementDate?.format(DATE_FORMATTER)
            agreedBy = note?.agreedBy?.userOfficialName
            comment = note?.comment ?: ""
        })
        return "prod/include/launch-note/list/edit"
    }

    @GetMapping("/list/product")
    fun listProduct(model: ModelMap, id: Long): String {
        model.addAttribute("id", id)
        return "prod/include/launch-note/list/product"
    }

    @GetMapping("/list/product/filter")
    fun listProductFilter(model: ModelMap): String {
        bindModelLaunchStruct(model)
        return "prod/include/launch-note/list/product/filter"
    }

    private fun bindModelLaunchStruct(model: ModelMap) {
        data class Item(
            var id: Long? = null,
            var name: String = "",
            var parent: Item? = null,
            var childList: List<Item> = emptyList()
        )
        val launchList = mutableListOf<Item>()
        lateinit var buildStruct: (
            list: List<Launch>,
            structList: MutableList<Item>,
            parent: Item?
        ) -> Unit
        buildStruct = { list, structList, parent ->
            list.forEach {
                val subStructList = mutableListOf<Item>()
                val item = Item(it.id, it.numberInYear, parent, subStructList)
                structList += item
                buildStruct(it.launchList, subStructList, item)
            }
        }
        buildStruct(launchService.findAllSortedYearNumberDesc(), launchList, null)
        model.addAttribute("launchList", launchList)
    }
}