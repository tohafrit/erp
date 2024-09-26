package ru.korundm.controller.view.prod

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant
import ru.korundm.constant.RequestPath
import ru.korundm.dao.ConstructorDocumentNotificationService
import ru.korundm.dao.UserService
import ru.korundm.dto.DropdownOption
import ru.korundm.entity.ConstructorDocumentNotification
import ru.korundm.exception.AlertUIException

@ViewController([RequestPath.View.Prod.CONSTRUCTOR_DOCUMENT_NOTIFICATION])
class ConstructorDocumentNotificationViewProdController(
    private val constructorDocumentNotificationService: ConstructorDocumentNotificationService,
    private val userService: UserService,
    private val jsonMapper: ObjectMapper
) {

    @GetMapping("/list")
    fun list() = "prod/include/constructor-document-notification/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        model.addAttribute("userList", userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) })
        return "prod/include/constructor-document-notification/list/filter"
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val notification = id?.let { constructorDocumentNotificationService.read(id) ?: throw AlertUIException("Извещение об изменении КД не найдено") }
        model.addAttribute("id", notification?.id)
        model.addAttribute("docNumber", notification?.docNumber)
        model.addAttribute("releaseOn", notification?.releaseOn?.format(BaseConstant.DATE_FORMATTER))
        model.addAttribute("termChangeOn", notification?.termChangeOn?.format(BaseConstant.DATE_FORMATTER))
        model.addAttribute("reason", notification?.reason)
        model.addAttribute("reserveIndication", notification?.reserveIndication)
        model.addAttribute("introductionIndication", notification?.introductionIndication)
        notification?.applicabilityProduct?.let {
            data class ProductItem(
                val id: Long?,
                val conditionalName: String?,
                val decimalNumber: String?
            )
            val jsonString = jsonMapper.writeValueAsString(
                listOf(ProductItem(it.id, it.conditionalName, it.decimalNumber))
            )
            model.addAttribute("productApplicabilityList", jsonString)
        } ?: model.addAttribute("productApplicabilityList", "[]")
        notification?.parent?.let {
            data class NotificationItem(
                val id: Long?,
                val docNumber: String?,
                val product: String?
            )
            val jsonString = jsonMapper.writeValueAsString(
                listOf(
                    NotificationItem(
                        it.id,
                        it.docNumber,
                        "${it.applicabilityProduct?.conditionalName} (${it.applicabilityProduct?.decimalNumber})"
                    )
                )
            )
            model.addAttribute("notificationList", jsonString)
        } ?: model.addAttribute("notificationList", "[]")
        model.addAttribute("leadProductUser", notification?.leadProductUser?.id)
        model.addAttribute("userList", userService.activeAll.map { DropdownOption(it.id, it.userOfficialName) })
        return "prod/include/constructor-document-notification/list/edit"
    }

    // Добавление применяемости
    @GetMapping("/list/edit/add-product")
    fun listEditAddProduct(
        model: ModelMap,
        @RequestParam productApplicabilityIdList: List<Long>
    ): String {
        model.addAttribute("productApplicabilityIdList", productApplicabilityIdList.joinToString())
        return "prod/include/constructor-document-notification/list/edit/addProduct"
    }

    @GetMapping("/list/edit/add-product/filter")
    fun listEditAddProductFilter() = "prod/include/constructor-document-notification/list/edit/add-product/filter"

    // Добавление извещения
    @GetMapping("/list/edit/add-notification")
    fun listEditAddNotification(
        model: ModelMap,
        @RequestParam notificationIdList: List<Long>,
        @RequestParam currentNotificationId: Long?
    ): String {
        val finalIdList = currentNotificationId?.let { notificationIdList + it } ?: notificationIdList
        model.addAttribute("notificationIdList", finalIdList.joinToString())
        return "prod/include/constructor-document-notification/list/edit/addNotification"
    }

    @GetMapping("/list/edit/add-notification/filter")
    fun listEditAddNotificationFilter() = "prod/include/constructor-document-notification/list/edit/add-notification/filter"

    // Окно со списком извещений
    @GetMapping("/list/child-notification")
    fun listChildNotification(model: ModelMap, id: Long): String {
        data class NotificationStructure(
            val id: Long?,
            val docNumber: String?,
            val releaseOn: String?,
            val termChangeOn: String?,
            val reason: String?,
            val reserveIndication: Boolean,
            val introductionIndication: String?,
            val product: String?,
            val user: String?,
            @JsonProperty("_children")
            var childList: List<Any> = mutableListOf()
        )
        // Метод рекурсивного построения извещений
        fun buildNotificationStructure(
            structureList: MutableList<Any>,
            notificationList: List<ConstructorDocumentNotification>
        ) {
            notificationList.forEach { notification ->
                val structureItem = with(notification) {
                    NotificationStructure(
                        id, docNumber, releaseOn?.format(BaseConstant.DATE_FORMATTER),
                        termChangeOn?.format(BaseConstant.DATE_FORMATTER), reason,
                        reserveIndication, introductionIndication,
                        "${applicabilityProduct?.conditionalName} (${applicabilityProduct?.decimalNumber})",
                        leadProductUser?.userOfficialName
                    )
                }
                val childList: List<ConstructorDocumentNotification> = notification.childList
                if (childList.isNotEmpty()) {
                    val subStructureList = mutableListOf<Any>()
                    buildNotificationStructure(subStructureList, childList)
                    if (subStructureList.isNotEmpty()) {
                        structureItem.childList = subStructureList
                    }
                }
                structureList.add(structureItem)
            }
        }

        val constructorDocumentNotification = constructorDocumentNotificationService.read(id)
        val childNotificationList = mutableListOf<Any>()
        buildNotificationStructure(childNotificationList, constructorDocumentNotification.childList)
        model.addAttribute("childNotificationList", jsonMapper.writeValueAsString(childNotificationList))
        return "prod/include/constructor-document-notification/list/childNotification"
    }
}
