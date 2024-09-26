package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.*
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.*
import ru.korundm.entity.TechnologicalEntityNotification
import ru.korundm.enumeration.ReasonType
import ru.korundm.helper.*
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest

@ActionController([RequestPath.Action.Prod.TECHNOLOGICAL_ENTITY_NOTIFICATION])
class TechnologicalEntityNotificationActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val technologicalEntityNotificationService: TechnologicalEntityNotificationService,
    private val technologicalEntityService: TechnologicalEntityService,
    private val userService: UserService,
    private val constructorDocumentNotificationService: ConstructorDocumentNotificationService
) {

    val locale = LocaleContextHolder.getLocale()

    // Загрузка извещений
    @GetMapping("/list/load")
    fun listLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*>? {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        data class Item(
            val id: Long? = null, // идентификатор
            val docNumber: String? = null, // номер документа
            val releaseOn: LocalDate? = null, // дата выпуска
            val termChangeOn: LocalDate? = null, // срок изменения
            val reserveIndication: Boolean = false, // указание о заделе
            val introductionIndication: String? = null, // указание о внедрении
            val entityNumber: String? = null, // технологическая документация
            val techUser: String? = null, // технолог
            val reason: String? = null, // причина
            val text: String? = null // текст извещения
        )
        return TabrOut.instance(input, technologicalEntityNotificationService.findTableData(input, form))  { Item(
            it.id,
            it.docNumber,
            it.releaseOn,
            it.termChangeOn,
            it.reserveIndication,
            it.introductionIndication,
            it.technologicalEntity?.entityNumber,
            it.techUser?.userOfficialName,
            "${it.reason?.code} - ${it.reason?.property}",
            it.text
        ) }
    }

    // Загрузка извещений по КД
    @GetMapping("/list/edit/add-notification/load")
    fun listEditAddProductLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        data class Item(
            val id: Long?,
            val docNumber: String?, // номер
            val product: String? // изделие применяемости
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, constructorDocumentNotificationService.findTableData(input, form)) { Item(
            it.id,
            it.docNumber,
            "${it.applicabilityProduct?.conditionalName} (${it.applicabilityProduct?.decimalNumber})"
        ) }
    }

    // Загрузка технологической документации
    @GetMapping("/list/edit/add-technological-entity/load")
    fun listEditAddTechnologicalEntityLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*>? {
        data class Item(
            val id: Long?, // идентификатор
            val entityNumber: String?, // номер ТД
            val setNumber: String?, // комплект
            val designedBy: String?, // разработчик
            val designedOn: LocalDate? // дата разработки
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, technologicalEntityService.findTableData(input, form)) { Item(
            it.id,
            it.entityNumber,
            it.setNumber,
            it.technologicalEntityReconciliation?.designedBy?.userOfficialName,
            it.technologicalEntityReconciliation?.designedOn?.toLocalDate()
        ) }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(@RequestPart form: DynamicObject): ValidatorResponse {
        val response = ValidatorResponse()
        val errors = ValidatorErrors(response)
        val docNumber = form.string(ObjAttr.DOC_NUMBER) ?: ""
        if (docNumber.isEmpty()) errors.putError(ObjAttr.DOC_NUMBER, ValidatorMsg.RANGE_LENGTH, 1, 128)
        val releaseOn = form.date(ObjAttr.RELEASE_ON)
        if (releaseOn == null) errors.putError(ObjAttr.RELEASE_ON, ValidatorMsg.REQUIRED)
        val termChangeOn = form.date(ObjAttr.TERM_CHANGE_ON)
        if (termChangeOn == null) errors.putError(ObjAttr.TERM_CHANGE_ON, ValidatorMsg.REQUIRED)
        val text = form.string(ObjAttr.TEXT) ?: ""
        if (text.isEmpty()) errors.putError(ObjAttr.TEXT, ValidatorMsg.REQUIRED)
        val introductionIndication = form.string(ObjAttr.INTRODUCTION_INDICATION) ?: ""
        if (introductionIndication.isEmpty()) errors.putError(ObjAttr.INTRODUCTION_INDICATION, ValidatorMsg.RANGE_LENGTH, 1, 128)
        val entityIdList = form.listLong(ObjAttr.ENTITY_ID_LIST)
        if (entityIdList.isEmpty()) errors.putError(ObjAttr.ENTITY_ID_LIST, ValidatorMsg.REQUIRED)
        val notificationIdList = form.listLong(ObjAttr.NOTIFICATION_ID_LIST)
        if (notificationIdList.isEmpty()) errors.putError(ObjAttr.NOTIFICATION_ID_LIST, ValidatorMsg.REQUIRED)

        if (response.isValid) baseService.exec {
            val notification = form.long(ObjAttr.ID)?.let { technologicalEntityNotificationService.read(it) } ?: TechnologicalEntityNotification()
            notification.apply {
                this.docNumber = docNumber
                this.releaseOn = form.date(ObjAttr.RELEASE_ON)
                this.termChangeOn = form.date(ObjAttr.TERM_CHANGE_ON)
                this.reason = form.long(ObjAttr.REASON)?.let { ReasonType.getById(it) }
                this.text = form.string(ObjAttr.TEXT) ?: ""
                this.reserveIndication = form.bool(ObjAttr.RESERVE_INDICATION) ?: false
                this.introductionIndication = form.string(ObjAttr.INTRODUCTION_INDICATION) ?: ""
                this.technologicalEntity = technologicalEntityService.read(form.listLong(ObjAttr.ENTITY_ID_LIST).first())
                this.techUser = form.long(ObjAttr.TECH_USER_ID)?.let { userService.read(it) }
                this.cdNotificationList = constructorDocumentNotificationService.getAllById(form.listLong(ObjAttr.NOTIFICATION_ID_LIST))
            }
            technologicalEntityNotificationService.save(notification)
        }
        return response
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = technologicalEntityNotificationService.deleteById(id)
}