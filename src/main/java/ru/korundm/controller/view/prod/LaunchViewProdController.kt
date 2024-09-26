package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.controller.action.prod.LaunchActionProdController.Companion.verifyCanAddLaunch
import ru.korundm.dao.LaunchService
import ru.korundm.dao.ProductTypeService
import ru.korundm.dao.UserService
import ru.korundm.dto.DropdownOption
import ru.korundm.exception.AlertUIException
import java.time.LocalDate
import ru.korundm.form.LaunchListAdditionalEditForm as ListAdditionalEditForm
import ru.korundm.form.LaunchListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.LAUNCH])
class LaunchViewProdController(
    private val launchService: LaunchService,
    private val userService: UserService,
    private val productTypeService: ProductTypeService
) {

    @GetMapping("/list")
    fun list(model: ModelMap) = "prod/include/launch/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        model.addAttribute("userList", userService.all.map { user -> DropdownOption(user.id, user.userOfficialName) }.toList())
        model.addAttribute("productTypeList", productTypeService.all.map { type -> DropdownOption(type.id, type.name) }.toList())
        return "prod/include/launch/list/filter"
    }

    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        verifyCanAddLaunch(id, launchService)
        val launch = id?.let { launchService.read(it) ?: throw AlertUIException("Запуск не найден") }
        model.addAttribute("form", ListEditForm(id).apply {
            launch?.let { version = it.version }
            year = launch?.year ?: LocalDate.now().year
            number = launch?.number ?: launchService.getLastNumber(year)
            comment = launch?.comment ?: ""
        })
        return "prod/include/launch/list/edit"
    }

    @GetMapping("/list/additional")
    fun listAdditional(model: ModelMap, id: Long): String {
        model.addAttribute("id", id)
        return "prod/include/launch/list/additional"
    }

    @GetMapping("/list/additional/edit")
    fun listAdditionalEdit(
        model: ModelMap,
        id: Long?,
        launchId: Long
    ): String {
        val launch = id?.let { launchService.read(it) ?: throw AlertUIException("Запуск не найден") }
        val parentId = launch?.launch?.id ?: launchId
        val parentLaunch = launchService.read(parentId) ?: throw AlertUIException("Запуск-родитель не найден")
        verifyCanAddLaunch(id, parentLaunch, launchService)
        model.addAttribute("form", ListAdditionalEditForm(id).apply {
            launch?.let { version = it.version }
            this.launchId = parentLaunch.id
            this.launchNumber = parentLaunch.number
            year = parentLaunch.year
            number = launch?.number ?: launchService.getLastNumber(year, this.launchId)
            comment = launch?.comment ?: ""
        })
        return "prod/include/launch/list/additional/edit"
    }

    @GetMapping("/detail")
    fun detail(model: ModelMap, id: Long): String {
        val launch = id.let { launchService.read(it) ?: throw AlertUIException("Запуск не найден") }
        model.addAttribute("id", id)
        model.addAttribute("launchName", "${launch.numberInYear}${launch.approvalDate?.let { " от ${DATE_FORMATTER.format(it)}" } ?: ""}")
        return "prod/include/launch/detail"
    }
}