package ru.korundm.controller.action.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ActionController
import ru.korundm.constant.RequestPath
import ru.korundm.dao.MatValueService
import ru.korundm.dao.ProductService
import ru.korundm.dao.WaybillHistoryService
import ru.korundm.helper.TabrIn
import ru.korundm.helper.TabrOut
import ru.korundm.util.KtCommonUtil.readDynamic
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest

@ActionController([RequestPath.Action.Prod.WAREHOUSE_STATE])
class WarehouseStateActionProdController(
    private val jsonMapper: ObjectMapper,
    private val matValueService: MatValueService,
    private val waybillHistoryService: WaybillHistoryService,
    private val productService: ProductService
) {

    // Загрузка списка изделий
    @GetMapping("/list/load")
    fun listLoad(filterData: String) = matValueService.findWarehouseStateTableData(jsonMapper.readDynamic(filterData))

    // Загрузка списка экземпляров изделий
    @GetMapping("/list/mat-value/load")
    fun listMatValueLoad(
        request: HttpServletRequest,
        filterData: String,
        productId: Long
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val result = matValueService.findWarehouseStateMatValueTableData(input, form, productId)
        return TabrOut.instance(input, result)
    }

    // Загрузка списка изделий
    @GetMapping("/list/mat-value/history/load")
    fun listMatValueHistoryLoad(mvId: Long): List<*> {
        data class Item(
            var type: Int? = null, // тип документа
            var number: String? = null, // номер
            var signDate: LocalDate? = null // дата подписания
        )
        return waybillHistoryService.getAllByMatValueId(mvId).map { wh ->
            val item = Item()
            when {
                wh.internal != null -> {
                    item.type = 1
                    item.number = wh.internal?.let { "${it.number.toString().padStart(3, '0')}/АО" }
                    item.signDate = wh.internal?.acceptDate
                }
                wh.shipment != null -> {
                    item.type = 2
                    item.number = wh.shipment?.number?.toString()
                    item.signDate = wh.shipment?.shipmentDate
                }
            }
            item
        }
    }

    // Загрузка списка экземпляров изделий
    @GetMapping("/list/report-first/product/load")
    fun listReportFirstProductLoad(
        request: HttpServletRequest,
        filterData: String
    ): TabrOut<*> {
        val input = TabrIn(request)
        val form = jsonMapper.readDynamic(filterData)
        val result = productService.findWarehouseStateFirstReportTableData(input, form)
        return TabrOut.instance(input, result)
    }
}