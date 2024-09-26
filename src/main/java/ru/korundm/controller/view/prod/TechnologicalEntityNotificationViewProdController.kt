package ru.korundm.controller.view.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.RequestPath
import ru.korundm.dao.TechnologicalEntityNotificationService
import ru.korundm.dao.UserService
import ru.korundm.dto.DropdownOption
import ru.korundm.enumeration.ReasonType
import ru.korundm.exception.AlertUIException

@ViewController([RequestPath.View.Prod.TECHNOLOGICAL_ENTITY_NOTIFICATION])
class TechnologicalEntityNotificationViewProdController(
    private val technologicalEntityNotificationService: TechnologicalEntityNotificationService,
    private val userService: UserService,
    private val jsonMapper: ObjectMapper
) {

    @GetMapping("/list")
    fun list() = "prod/include/technological-entity-notification/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        model.addAttribute("userList", userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) })
        model.addAttribute("reasonTypeList", ReasonType.values().map { DropdownOption(it.id, "${it.code} - ${it.property}") })
        return "prod/include/technological-entity-notification/list/filter"
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val notification = id?.let { technologicalEntityNotificationService.read(id) ?: throw AlertUIException("Извещение об изменении ТД не найдено") }
        model.addAttribute("id", notification?.id)
        model.addAttribute("docNumber", notification?.docNumber)
        model.addAttribute("releaseOn", notification?.releaseOn?.format(BaseConstant.DATE_FORMATTER))
        model.addAttribute("termChangeOn", notification?.termChangeOn?.format(BaseConstant.DATE_FORMATTER))
        model.addAttribute("reasonId", notification?.reason?.id)
        model.addAttribute("text", notification?.text)
        model.addAttribute("reserveIndication", notification?.reserveIndication)
        model.addAttribute("introductionIndication", notification?.introductionIndication)
        model.addAttribute("techUserId", notification?.techUser?.id)

        data class TechnologicalItem(
            val id: Long?,
            val entityNumber: String?,
            val setNumber: String?
        )
        val technologicalEntityList = notification?.technologicalEntity?.let {
            listOf(TechnologicalItem(it.id, it.entityNumber, it.setNumber))
        } ?: listOf()
        model.addAttribute("technologicalEntityList", jsonMapper.writeValueAsString(technologicalEntityList))

        data class NotificationItem(
            val id: Long?,
            val docNumber: String?,
            val product: String?
        )
        val notificationList = notification?.cdNotificationList?.map { NotificationItem(
            it.id,
            it.docNumber,
            "${it.applicabilityProduct?.conditionalName} (${it.applicabilityProduct?.decimalNumber})"
        ) } ?: listOf()
        model.addAttribute("notificationList", jsonMapper.writeValueAsString(notificationList))

        model.addAttribute("userList", userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) })
        model.addAttribute("reasonTypeList", ReasonType.values().map { DropdownOption(it.id, "${it.code} - ${it.property}") })
        return "prod/include/technological-entity-notification/list/edit"
    }

    // Добавление технологической документации
    @GetMapping("/list/edit/add-technological-entity")
    fun listEditAddTechnologicalEntity(model: ModelMap) = "prod/include/technological-entity-notification/list/edit/addTechnologicalEntity"

    // Добавление извещений об изменении КД
    @GetMapping("/list/edit/add-notification")
    fun listEditAddNotification(model: ModelMap) = "prod/include/technological-entity-notification/list/edit/addNotification"

    @GetMapping("/list/edit/add-technological-entity/filter")
    fun listEditAddTechnologicalEntityFilter() = "prod/include/technological-entity-notification/list/edit/add-technological-entity/filter"

    @GetMapping("/list/edit/add-notification/filter")
    fun listEditAddNotificationFilter() = "prod/include/technological-entity-notification/list/edit/add-notification/filter"
}
