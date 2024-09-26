package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.ProductDeciphermentPeriodService
import ru.korundm.dao.ProductPricePeriodService
import ru.korundm.exception.AlertUIException
import ru.korundm.form.ProductPricePeriodListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.PRODUCT_PRICE_PERIOD])
class ProductPricePeriodViewProdController(
    private val productPricePeriodService: ProductPricePeriodService,
    private val productDeciphermentPeriodService: ProductDeciphermentPeriodService
) {

    @GetMapping("/list")
    fun list() = "prod/include/product-price-period/list"

    @GetMapping("/list/filter")
    fun listFilter() = "prod/include/product-price-period/list/filter"

    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val period = id?.let { productPricePeriodService.read(it) ?: throw AlertUIException("Период не найден") }
        model.addAttribute("isDisableEdit", productDeciphermentPeriodService.existsByPricePeriod(period?.id))
        model.addAttribute("form", ListEditForm(id).apply { period?.let {
            version = it.version
            name = it.name
            startDate = it.startDate
            comment = it.comment ?: ""
        } })
        return "prod/include/product-price-period/list/edit"
    }

    @GetMapping("/list/product")
    fun listProduct(model: ModelMap, id: Long): String {
        model.addAttribute("pricePeriodId", id)
        return "prod/include/product-price-period/list/product"
    }
}