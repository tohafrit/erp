package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.BaseService
import ru.korundm.dao.ProductDeciphermentPeriodService
import ru.korundm.dao.ProductPricePeriodService
import ru.korundm.entity.ProductPricePeriod
import ru.korundm.exception.AlertUIException
import ru.korundm.helper.RowCountable
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.helper.ValidatorResponse
import ru.korundm.util.KtCommonUtil.nullIfBlank
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import ru.korundm.form.ProductPricePeriodListEditForm as ListEditForm

@ActionController([RequestPath.Action.Prod.PRODUCT_PRICE_PERIOD])
class ProductPricePeriodActionProdController(
    private val jsonMapper: ObjectMapper,
    private val baseService: BaseService,
    private val productPricePeriodService: ProductPricePeriodService,
    private val productDeciphermentPeriodService: ProductDeciphermentPeriodService
) {

    @GetMapping("/list/load")
    fun listLoad(request: HttpServletRequest, filterData: String): TabrOut<*> {
        data class Item(
            val id: Long?,
            val name: String, // наименование
            val startDate: LocalDate?, // дата начала
            val comment: String?, // комментарий
        )
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, productPricePeriodService.findTableData(input, form)) { p -> Item(
            p.id,
            p.name,
            p.startDate,
            p.comment,
        ) }
    }

    @PostMapping("/list/edit/save")
    fun listEditSave(form: ListEditForm): ValidatorResponse {
        val response = ValidatorResponse(form)
        if (response.isValid) {
            val formId = form.id
            lateinit var period: ProductPricePeriod
            baseService.exec {
                period = formId?.let { productPricePeriodService.read(it) ?: throw AlertUIException("Период не найден") } ?: ProductPricePeriod()
                period.apply {
                    version = form.version
                    if (!productDeciphermentPeriodService.existsByPricePeriod(period.id)) startDate = form.startDate
                    name = form.name
                    comment = form.comment.nullIfBlank()
                    productPricePeriodService.save(this)
                }
            }
            if (formId == null) response.putAttribute(ProductPricePeriod::id.name, period.id)
        }
        return response
    }

    @DeleteMapping("/list/delete/{id}")
    fun listDelete(@PathVariable id: Long) = baseService.exec {
        if (productDeciphermentPeriodService.existsByPricePeriod(id)) throw AlertUIException("Невозможно удалить запись, т.к. период используется в расчете цен")
        productPricePeriodService.deleteById(id)
    }

    @GetMapping("/list/product/load")
    fun listProductLoad(request: HttpServletRequest, filterData: String, pricePeriodId: Long): TabrOut<*> {
        data class Item(
            var id: Long? = null,
            var periodId: Long? = null,
            var name: String = "", // изделие
            var period: String = "", // период
            var priceWoPack: Double? = null, // цена без упаковки
            var pricePack: Double? = null, // цена с упаковкой
            var pricePackResearch: Double? = null, // цена с упаковкой и СИ
            //
            var startDate: LocalDate = LocalDate.MIN, // дата начала периода
            var endDate: LocalDate? = null, // дата окончания периода
            var rowCount: Long = 0
        ) : RowCountable { override fun rowCount() = rowCount }
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        return TabrOut.instance(input, productPricePeriodService.findPeriodProductTableData(input, form, pricePeriodId, Item::class)) {
            it.period = "${it.startDate.format(DATE_FORMATTER)} - ${it.endDate?.format(DATE_FORMATTER) ?: "н.в"}"
            it
        }
    }
}