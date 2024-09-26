package ru.korundm.controller.view.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.FileStorageService
import ru.korundm.dao.ProductDeciphermentAttrValService
import ru.korundm.dao.ProductSpecResearchJustificationService
import ru.korundm.dao.ProductSpecResearchPriceService
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.FileStorageType.ProductSpecResearchJustificationFile
import ru.korundm.util.KtCommonUtil.currencyFormat
import java.time.LocalDate
import ru.korundm.form.ProductSpecResearchJustificationListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.PRODUCT_SPEC_RESEARCH_JUSTIFICATION])
class ProductSpecResearchJustificationViewProdController(
    private val jsonMapper: ObjectMapper,
    private val productSpecResearchJustificationService: ProductSpecResearchJustificationService,
    private val productSpecResearchPriceService: ProductSpecResearchPriceService,
    private val fileStorageService: FileStorageService,
    private val productDeciphermentAttrValService: ProductDeciphermentAttrValService
) {

    @GetMapping("/list")
    fun list() = "prod/include/product-spec-research-justification/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/product-spec-research-justification/list/filter"

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val justification = id?.let { productSpecResearchJustificationService.read(it) ?: throw AlertUIException("Обоснование не найдено") }
        model.addAttribute("isDisableEdit", productDeciphermentAttrValService.existsByProductSpecResearchJustificationId(justification?.id))
        model.addAttribute("form", ListEditForm(id).apply {
            version = justification?.version ?: 0L
            name = justification?.name ?: ""
            groupPrice = id?.let { jsonMapper.writeValueAsString(productSpecResearchPriceService.getAllByJustificationId(it)
                .map { i -> ListEditForm.GroupPrice(i.group?.id, i.group?.number ?: 0 , i.group?.characteristic ?: "", i.price) }.toList()) } ?: "[]"
            approvalDate = justification?.approvalDate ?: LocalDate.now()
            createDate = justification?.createDate?.format(DATE_FORMATTER)
            comment = justification?.comment ?: ""
            fileStorage = justification?.let { fileStorageService.readOneSingular(it, ProductSpecResearchJustificationFile) }
        })
        return "prod/include/product-spec-research-justification/list/edit"
    }

    // Окно добавления групп
    @GetMapping("/list/edit/add-group")
    fun listEditAddGroup(model: ModelMap, @RequestParam(value = "classGroupIdList") classGroupIdList: List<Long>): String {
        model.addAttribute("classGroupData", jsonMapper.writeValueAsString(classGroupIdList))
        return "prod/include/product-spec-research-justification/list/edit/addGroup"
    }

    // Окно списка цен классификационных групп
    @GetMapping("/list/group-price")
    fun listGroupPrice(model: ModelMap, id: Long): String {
        data class Item(
            var id: Long? = null,
            var number: Int? = null,
            var name: String? = null,
            var price: String = ""
        )
        model.addAttribute("groupPriceList", jsonMapper.writeValueAsString(productSpecResearchPriceService.getAllByJustificationId(id)
            .map { Item(it.id, it.group?.number, it.group?.characteristic, it.price.currencyFormat()) }.toList()))
        return "prod/include/product-spec-research-justification/list/groupPrice"
    }
}