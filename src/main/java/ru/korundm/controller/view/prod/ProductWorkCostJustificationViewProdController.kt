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
import ru.korundm.dao.ProductWorkCostJustificationService
import ru.korundm.dao.ProductWorkCostService
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.FileStorageType
import ru.korundm.util.KtCommonUtil.currencyFormat
import java.time.LocalDate
import ru.korundm.form.ProductWorkCostJustificationListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.PRODUCT_WORK_COST_JUSTIFICATION])
class ProductWorkCostJustificationViewProdController(
    private val jsonMapper: ObjectMapper,
    private val productWorkCostJustificationService: ProductWorkCostJustificationService,
    private val productWorkCostService: ProductWorkCostService,
    private val fileStorageService: FileStorageService,
    private val productDeciphermentAttrValService: ProductDeciphermentAttrValService
) {

    @GetMapping("/list")
    fun list() = "prod/include/product-work-cost-justification/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/product-work-cost-justification/list/filter"

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val justification = id?.let { productWorkCostJustificationService.read(it) ?: throw AlertUIException("Обоснование не найдено") }
        model.addAttribute("isDisableEdit", productDeciphermentAttrValService.existsByProductWorkCostJustificationId(justification?.id))
        model.addAttribute("form", ListEditForm(id).apply {
            val nowDate = LocalDate.now()
            version = justification?.version ?: 0L
            name = justification?.name ?: "Тарифы н/час от ${nowDate.format(DATE_FORMATTER)}"
            workCost = id?.let { jsonMapper.writeValueAsString(productWorkCostService.getAllByJustificationId(it).map { i -> ListEditForm.WorkCost(i.workType?.id, i.workType?.name ?: "", i.cost) }.toList()) } ?: "[]"
            approvalDate = justification?.approvalDate ?: nowDate
            createDate = justification?.createDate?.format(DATE_FORMATTER)
            comment = justification?.comment ?: ""
            fileStorage = justification?.let { fileStorageService.readOneSingular(it, FileStorageType.ProductWorkCostJustificationFile) }
        })
        return "prod/include/product-work-cost-justification/list/edit"
    }

    // Окно добавления работ
    @GetMapping("/list/edit/add-work")
    fun listEditAddWork(model: ModelMap, @RequestParam(value = "workTypeIdList") workTypeIdList: List<Long>): String {
        model.addAttribute("workTypeData", jsonMapper.writeValueAsString(workTypeIdList))
        return "prod/include/product-work-cost-justification/list/edit/addWork"
    }

    // Окно списка работ
    @GetMapping("/list/work-cost")
    fun listWorkCost(model: ModelMap, id: Long): String {
        data class Item(
            var id: Long? = null,
            var name: String? = null,
            var cost: String = ""
        )
        model.addAttribute("workCostList", jsonMapper.writeValueAsString(productWorkCostService.getAllByJustificationId(id).map { Item(it.id, it.workType?.name, it.cost.currencyFormat()) }))
        return "prod/include/product-work-cost-justification/list/workCost"
    }
}