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
import ru.korundm.dao.ProductSpecReviewJustificationService
import ru.korundm.dao.ProductSpecReviewPriceService
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.FileStorageType.ProductSpecReviewJustificationFile
import ru.korundm.util.KtCommonUtil.currencyFormat
import java.time.LocalDate
import ru.korundm.form.ProductSpecReviewJustificationListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.PRODUCT_SPEC_REVIEW_JUSTIFICATION])
class ProductSpecReviewJustificationViewProdController(
    private val jsonMapper: ObjectMapper,
    private val productSpecReviewJustificationService: ProductSpecReviewJustificationService,
    private val productSpecReviewPriceService: ProductSpecReviewPriceService,
    private val fileStorageService: FileStorageService,
    private val productDeciphermentAttrValService: ProductDeciphermentAttrValService
) {

    @GetMapping("/list")
    fun list() = "prod/include/product-spec-review-justification/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/product-spec-review-justification/list/filter"

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val justification = id?.let { productSpecReviewJustificationService.read(it) ?: throw AlertUIException("Обоснование не найдено") }
        model.addAttribute("isDisableEdit", productDeciphermentAttrValService.existsByProductSpecReviewJustificationId(justification?.id))
        model.addAttribute("form", ListEditForm(id).apply {
            version = justification?.version ?: 0L
            name = justification?.name ?: ""
            groupPrice = id?.let { jsonMapper.writeValueAsString(productSpecReviewPriceService.getAllByJustificationId(it)
                .map { i -> ListEditForm.GroupPrice(i.group?.id, i.group?.number ?: 0 , i.group?.characteristic ?: "", i.price) }.toList()) } ?: "[]"
            approvalDate = justification?.approvalDate ?: LocalDate.now()
            createDate = justification?.createDate?.format(DATE_FORMATTER)
            comment = justification?.comment ?: ""
            fileStorage = justification?.let { fileStorageService.readOneSingular(it, ProductSpecReviewJustificationFile) }
        })
        return "prod/include/product-spec-review-justification/list/edit"
    }

    // Окно добавления групп
    @GetMapping("/list/edit/add-group")
    fun listEditAddGroup(model: ModelMap, @RequestParam(value = "classGroupIdList") classGroupIdList: List<Long>): String {
        model.addAttribute("classGroupData", jsonMapper.writeValueAsString(classGroupIdList))
        return "prod/include/product-spec-review-justification/list/edit/addGroup"
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
        model.addAttribute("groupPriceList", jsonMapper.writeValueAsString(productSpecReviewPriceService.getAllByJustificationId(id)
            .map { Item(it.id, it.group?.number, it.group?.characteristic, it.price.currencyFormat()) }.toList()))
        return "prod/include/product-spec-review-justification/list/groupPrice"
    }
}