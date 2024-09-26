package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.LaunchService
import ru.korundm.dao.PurchasePlanService
import ru.korundm.dao.UserService
import ru.korundm.dto.DropdownOption
import ru.korundm.entity.Launch
import ru.korundm.enumeration.BomVersionType
import ru.korundm.enumeration.ReserveUseType
import ru.korundm.exception.AlertUIException
import ru.korundm.util.KtCommonUtil.getUser
import java.time.LocalDate
import javax.servlet.http.HttpSession

@ViewController([RequestPath.View.Prod.PURCHASE_PLAN])
class PurchasePlanViewProdController(
    private val purchasePlanService: PurchasePlanService,
    private val userService: UserService,
    private val launchService: LaunchService
) {

    @GetMapping("/list")
    fun list() = "prod/include/purchase-plan/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
        model.addAttribute("versionTypeList", BomVersionType.values())
        model.addAttribute("reserveUseTypeList", ReserveUseType.values())
        bindModelLaunchStruct(model)
        return "prod/include/purchase-plan/list/filter"
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

    // Редактирование/добавление
    @GetMapping("/list/edit")
    fun listEdit(
        session: HttpSession,
        model: ModelMap,
        id: Long?
    ): String {
        val purchasePlan = id?.let { purchasePlanService.read(it) ?: throw AlertUIException("Закупочная ведомость не найдена") }
        model.addAttribute("id", purchasePlan?.id)
        model.addAttribute("version", purchasePlan?.version)
        model.addAttribute("name", purchasePlan?.name)
        model.addAttribute("createDate", (id?.let { purchasePlan?.createDate } ?: LocalDate.now()).format(DATE_FORMATTER))
        model.addAttribute("userList", userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) })
        model.addAttribute("createdById", id?.let { purchasePlan?.createdBy?.id } ?: session.getUser().id)
        bindModelLaunchStruct(model)
        model.addAttribute("launchId", id?.let { purchasePlan?.launch?.id } ?: launchService.findLastLaunch()?.id)
        model.addAttribute("previousLaunchId", id?.let { purchasePlan?.previousLaunch?.id } ?: launchService.findLastLaunch()?.id)
        model.addAttribute("versionTypeList", BomVersionType.values())
        model.addAttribute("onTheWayLastDate", purchasePlan?.onTheWayLastDate?.format(DATE_FORMATTER))
        model.addAttribute("reserveUseTypeList", ReserveUseType.values())
        model.addAttribute("versionTypeId", id?.let { purchasePlan?.bomVersionType?.id } ?: BomVersionType.LAST_ADDED.id)
        model.addAttribute("reserveUseTypeId", id?.let { purchasePlan?.reserveUseType?.id } ?: ReserveUseType.FIRST_PERIOD.id)
        model.addAttribute("comment", purchasePlan?.comment)
        return "prod/include/purchase-plan/list/edit"
    }
}