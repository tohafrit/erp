package ru.korundm.controller.view.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.ProductLabourIntensityEntryService
import ru.korundm.dao.ProductLabourIntensityOperationService
import ru.korundm.dao.ProductLabourIntensityService
import ru.korundm.exception.AlertUIException
import ru.korundm.util.KtCommonUtil.currencyFormat
import ru.korundm.util.KtCommonUtil.userFullName
import java.time.LocalDate
import ru.korundm.form.ProductLabourIntensityDetailEditForm as DetailEditForm
import ru.korundm.form.ProductLabourIntensityListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.PRODUCT_LABOUR_INTENSITY])
class ProductLabourIntensityViewProdController(
    private val jsonMapper: ObjectMapper,
    private val productLabourIntensityService: ProductLabourIntensityService,
    private val productLabourIntensityEntryService: ProductLabourIntensityEntryService,
    private val productLabourIntensityOperationService: ProductLabourIntensityOperationService
) {

    @GetMapping("/list")
    fun list() = "prod/include/product-labour-intensity/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/product-labour-intensity/list/filter"

    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        data class Item(
            var id: Long? = null,
            var version: Long? = null,
            var name: String = "",
            var comment: String? = null,
            var createDate: LocalDate = LocalDate.MIN,
            var lastName: String? = null,
            var firstName: String? = null,
            var middleName: String? = null,
            var approvedCount: Int = 0,
            var totalCount: Int = 0
        )
        val labour = id?.let { productLabourIntensityService.findDataById(it, Item::class) ?: throw AlertUIException("Расчет турдоемкости не найден") }
        model.addAttribute("form", ListEditForm(id).apply {
            val nowDate = LocalDate.now()
            version = labour?.version ?: 0L
            name = if (id == null) "Трудоемкость изготовления и проверки изделий от ${nowDate.format(DATE_FORMATTER)}" else labour?.name ?: ""
            approved = labour?.let { "${it.approvedCount}/${it.totalCount}" } ?: ""
            createDate = if (id == null) nowDate else labour?.createDate
            createdBy = userFullName(labour?.lastName, labour?.firstName, labour?.middleName)
            comment = labour?.comment ?: ""
        })
        return "prod/include/product-labour-intensity/list/edit"
    }

    @GetMapping("/detail")
    fun detail(model: ModelMap, id: Long): String {
        val labour = id.let { productLabourIntensityService.read(it) ?: throw AlertUIException("Расчет турдоемкости не найден") }
        model.addAttribute("title", labour.name)
        model.addAttribute("id", labour.id)
        return "prod/include/product-labour-intensity/detail"
    }

    @GetMapping("/detail/filter")
    fun detailFilter() = "prod/include/product-labour-intensity/detail/filter"

    @GetMapping("/detail/work-type")
    fun detailWorkType(model: ModelMap, id: Long): String {
        data class Item(
            var id: Long?,
            var name: String?,
            var value: String
        )
        val entry = productLabourIntensityEntryService.read(id) ?: throw AlertUIException("Трудоемкость не найдена")
        model.addAttribute("title", entry.product?.conditionalName ?: "")
        model.addAttribute("data", jsonMapper.writeValueAsString(productLabourIntensityOperationService.getAllByEntryId(id)
            .map { Item(it.id, it.operation?.name, it.value.currencyFormat()) }.toList()))
        return "prod/include/product-labour-intensity/detail/workType"
    }

    @GetMapping("/detail/edit")
    fun detailEdit(model: ModelMap, id: Long): String {
        val labour = productLabourIntensityEntryService.read(id) ?: throw AlertUIException("Трудоемкость не найдена")
        model.addAttribute("form", DetailEditForm(id).apply {
            version = labour.version
            comment = labour.comment ?: ""
        })
        return "prod/include/product-labour-intensity/detail/edit"
    }

    @GetMapping("/detail/add")
    fun detailAdd(model: ModelMap, id: Long): String {
        model.addAttribute("id", id)
        return "prod/include/product-labour-intensity/detail/add"
    }
}