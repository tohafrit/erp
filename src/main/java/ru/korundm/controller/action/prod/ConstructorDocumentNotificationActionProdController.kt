package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.*
import ru.korundm.entity.*
import ru.korundm.helper.*
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest

@ActionController([RequestPath.Action.Prod.CONSTRUCTOR_DOCUMENT_NOTIFICATION])
class ConstructorDocumentNotificationActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val productService: ProductService,
    private val constructorDocumentNotificationService: ConstructorDocumentNotificationService,
    private val userService: UserService
) {

    // Загрузка сущностей
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        data class Item(
            val id: Long?,
            val docNumber: String?,
            val releaseOn: LocalDate?,
            val termChangeOn: LocalDate?,
            val reason: String?,
            val reserveIndication: Boolean,
            val introductionIndication: String?,
            val product: String?,
            val user: String?
        )
        return TabrOut.instance(input, constructorDocumentNotificationService.findTableData(input, form)) {
            Item(
                it.id,
                it.docNumber,
                it.releaseOn,
                it.termChangeOn,
                it.reason,
                it.reserveIndication,
                it.introductionIndication,
                "${it.applicabilityProduct?.conditionalName} (${it.applicabilityProduct?.decimalNumber})",
                it.leadProductUser?.userOfficialName
            )
        }
    }

    // Загрузка изделий для добавления применяемости
    @GetMapping("/list/edit/add-product/load")
    fun listEditAddProductLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val conditionalName: String?, // условное наименование
            val decimalNumber: String?, // технические условия изделия
            val typeName: String? // краткая техническая характеристика
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val productList = productService.findTableData(input, form)
        return TabrOut.instance(input, productList) {
            Item(
                it.id,
                it.conditionalName,
                it.decimalNumber,
                it.type?.name
            )
        }
    }

    // Загрузка извещений
    @GetMapping("/list/edit/add-notification/load")
    fun listEditAddNotificationLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val docNumber: String?,
            val product: String?
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val notificationList = constructorDocumentNotificationService.findTableData(input, form)
        return TabrOut.instance(input, notificationList) {
            Item(
                it.id,
                it.docNumber,
                "${it.applicabilityProduct?.conditionalName} (${it.applicabilityProduct?.decimalNumber})"
            )
        }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val docNumber = form.string(ObjAttr.DOC_NUMBER)
        if (docNumber.isNullOrEmpty() || docNumber.length > 128) errors.putError(ObjAttr.DOC_NUMBER, ValidatorMsg.RANGE_LENGTH, 1, 128)
        val releaseOn = form.date(ObjAttr.RELEASE_ON)
        if (releaseOn == null) errors.putError(ObjAttr.RELEASE_ON, ValidatorMsg.REQUIRED)
        val termChangeOn = form.date(ObjAttr.TERM_CHANGE_ON)
        if (termChangeOn == null) errors.putError(ObjAttr.TERM_CHANGE_ON, ValidatorMsg.REQUIRED)
        val reason = form.string(ObjAttr.REASON)
        if (reason.isNullOrEmpty() || reason.length > 512) errors.putError(ObjAttr.REASON, ValidatorMsg.RANGE_LENGTH, 1, 512)
        val introductionIndication = form.string(ObjAttr.INTRODUCTION_INDICATION)
        if (introductionIndication.isNullOrEmpty() || introductionIndication.length > 128) errors.putError(ObjAttr.INTRODUCTION_INDICATION, ValidatorMsg.RANGE_LENGTH, 1, 128)
        val productApplicabilityIdList = form.listLong(ObjAttr.PRODUCT_APPLICABILITY_ID_LIST)
        if (productApplicabilityIdList.isEmpty()) errors.putError(ObjAttr.PRODUCT_APPLICABILITY_ID_LIST, ValidatorMsg.REQUIRED)
        val notificationIdList = form.listLong(ObjAttr.NOTIFICATION_ID_LIST)

        if (response.isValid) baseService.exec {
            val formId = form.long(ObjAttr.ID)
            val constructorDocumentNotification = formId?.let { constructorDocumentNotificationService.read(formId) } ?: ConstructorDocumentNotification()
            constructorDocumentNotification.apply {
                this.docNumber = docNumber
                this.releaseOn = releaseOn
                this.termChangeOn = termChangeOn
                this.reason = reason
                this.reserveIndication = form.bool(ObjAttr.RESERVE_INDICATION) ?: false
                this.introductionIndication = introductionIndication
                this.applicabilityProduct = productService.read(productApplicabilityIdList[0])
                this.parent = if (notificationIdList.isNotEmpty()) constructorDocumentNotificationService.read(notificationIdList[0]) else null
                this.leadProductUser = userService.read(form.longNotNull(ObjAttr.LEAD_PRODUCT_USER))
            }
            constructorDocumentNotificationService.save(constructorDocumentNotification)

            if (formId == null) response.putAttribute(ObjAttr.ID, constructorDocumentNotification.id)
        }
        return response
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = constructorDocumentNotificationService.deleteById(id)
}