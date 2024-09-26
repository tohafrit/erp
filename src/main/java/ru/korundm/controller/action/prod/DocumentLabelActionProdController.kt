package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BaseService
import ru.korundm.dao.DocumentLabelService
import ru.korundm.entity.DocumentLabel
import ru.korundm.entity.ServiceType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import ru.korundm.form.DocumentLabelListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.DOCUMENT_LABEL])
class DocumentLabelActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val documentLabelService: DocumentLabelService
) {

    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val label: String?, // метка
            val employeePosition: String?, // должность сотрудника в документе (паспорте изделия)
            val fullName: String?, // фио
            val comment: String? // комментарий
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, documentLabelService.findTableData(input, form)) {
            Item(
                it.id,
                it.label,
                it.employeePosition,
                it.user?.userOfficialName,
                it.comment
            )
        }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(session: HttpSession, form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var documentLabel: DocumentLabel
            baseService.exec {
                documentLabel = formId?.let { documentLabelService.read(it) ?: throw AlertUIException("Метка шаблона не найдена") } ?: DocumentLabel()
                documentLabel.apply {
                    label = form.label
                    employeePosition = form.employeePosition
                    user = form.employee
                    comment = form.comment.nullIfBlank()
                    documentLabelService.save(this)
                }
            }
            if (formId == null) response.putAttribute(ServiceType::id.name, documentLabel.id)
        }
        return response
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        documentLabelService.read(id)?.apply { documentLabelService.delete(this) }
    }
}