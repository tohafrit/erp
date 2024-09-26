package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BaseService
import ru.korundm.dao.LotGroupService
import ru.korundm.dao.ServiceTypeService
import ru.korundm.entity.ServiceType
import ru.korundm.exception.AlertUIException
import ru.korundm.form.ServiceTypeListEditForm
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@ActionController([RequestPath.Action.Prod.SERVICE_TYPE])
class ServiceTypeActionProdController(
    private val jsonMapper: ObjectMapper,
    private val serviceTypeService: ServiceTypeService,
    private val baseService: BaseService,
    private val lotGroupService: LotGroupService
) {

    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val name: String, // наименование
            val prefix: String?, // префикс
            val comment: String? // комментарий
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, serviceTypeService.findTableData(input, form)) { type ->
            type.name?.let {
                Item(
                    type.id,
                    it,
                    type.prefix,
                    type.comment
                )
            }
        }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(session: HttpSession, form: ServiceTypeListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var type: ServiceType
            baseService.exec {
                type = formId?.let { serviceTypeService.read(it) ?: throw AlertUIException("Тип услуги не найден") } ?: ServiceType()
                if (lotGroupService.existsByServiceType(type)) throw AlertUIException("Тип услуги связан с другими объектами системы. Редактирование невозможно")
                type.apply {
                    name = form.name
                    prefix = form.prefix
                    comment = form.comment.nullIfBlank()
                    serviceTypeService.save(this)
                }
            }
            if (formId == null) response.putAttribute(ServiceType::id.name, type.id)
        }
        return response
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        serviceTypeService.read(id)?.apply {
            if (lotGroupService.existsByServiceType(this)) throw AlertUIException("Тип услуги связан с другими объектами системы. Удаление невозможно")
            serviceTypeService.delete(this)
        }
    }
}