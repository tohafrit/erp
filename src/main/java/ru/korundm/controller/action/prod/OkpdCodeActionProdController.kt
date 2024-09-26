package ru.korundm.controller.action.prod

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.ObjAttr
import ru.korundm.constant.RequestPath
import ru.korundm.constant.ValidatorMsg
import ru.korundm.dao.BaseService
import ru.korundm.dao.OkpdCodeService
import ru.korundm.entity.ComponentGroup
import ru.korundm.entity.OkpdCode
import ru.korundm.entity.ProductType
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.ValidatorResponse
import ru.korundm.form.OkpdCodeListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.OKPD_CODE])
class OkpdCodeActionProdController(
    private val baseService: BaseService,
    private val okpdCodeService: OkpdCodeService
) {

    // Загрузка кодов
    @GetMapping("/list/load")
    fun listLoad(type: OkpdCode.Type): List<*> {
        data class Item(
            val id: Long?,
            val name: String?, // наименование
            val code: String = "" // код
        )
        return okpdCodeService.getAllByType(type).map { Item(
            it.id,
            if (type == OkpdCode.Type.PRODUCT) it.productType?.name else it.componentGroup?.name,
            it.code
        ) }
    }

    // Сохранение кода
    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        val formId = form.id
        val type = form.type
        val typeId = form.typeId
        baseService.exec {
            typeId?.let { if (okpdCodeService.existsByType(type, it, formId)) response.putError(ListEditForm::typeId.name, ValidatorMsg.UNIQUE) }
            if (response.isValid) {
                val okpdCode = formId?.let { okpdCodeService.read(it) ?: throw AlertUIException("Код не найден") } ?: OkpdCode()
                okpdCode.apply {
                    version = form.version
                    code = form.code
                    when (type) {
                        OkpdCode.Type.PRODUCT -> productType = ProductType(typeId)
                        OkpdCode.Type.COMPONENT -> componentGroup = ComponentGroup(typeId)
                    }
                    okpdCodeService.save(this)
                }
                if (formId == null) response.putAttribute(ObjAttr.ID, okpdCode.id)
            }
        }
        return response
    }

    // Удаление кода
    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = okpdCodeService.deleteById(id)
}