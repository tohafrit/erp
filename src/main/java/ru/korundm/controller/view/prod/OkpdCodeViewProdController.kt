package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.ComponentGroupService
import ru.korundm.dao.OkpdCodeService
import ru.korundm.dao.ProductTypeService
import ru.korundm.dto.DropdownOption
import ru.korundm.entity.OkpdCode
import ru.korundm.entity.OkpdCode.Type.COMPONENT
import ru.korundm.entity.OkpdCode.Type.PRODUCT
import ru.korundm.exception.AlertUIException
import ru.korundm.form.OkpdCodeListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.OKPD_CODE])
class OkpdCodeViewProdController(
    private val okpdCodeService: OkpdCodeService,
    private val componentGroupService: ComponentGroupService,
    private val productTypeService: ProductTypeService
) {

    @GetMapping("/list")
    fun list() = "prod/include/okpd-code/list"

    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?, typeParam: OkpdCode.Type): String {
        val okpdCode = id?.let { okpdCodeService.read(it) ?: throw AlertUIException("Код не найден") }
        model.addAttribute("form", ListEditForm(id).apply {
            version = okpdCode?.version ?: 0
            type = typeParam
            typeId = when (typeParam) {
                PRODUCT -> okpdCode?.productType?.id
                COMPONENT -> okpdCode?.componentGroup?.id
            }
            code = okpdCode?.code ?: ""
            //
            val typeList = when (typeParam) {
                PRODUCT -> productTypeService.all.map { DropdownOption(it.id, it.name) }
                COMPONENT -> componentGroupService.all.map { DropdownOption(it.id, it.name) }
            }
            val existsTypeIdList = okpdCodeService.findAllExistsTypeIdList(typeParam).filterNot { it == typeId }
            model.addAttribute("typeList", typeList.filterNot { existsTypeIdList.contains(it.id) })
        })
        return "prod/include/okpd-code/list/edit"
    }
}