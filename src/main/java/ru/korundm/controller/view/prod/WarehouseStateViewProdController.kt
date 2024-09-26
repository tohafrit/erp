package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.BaseConstant.MONTH_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.dto.DropdownOption
import ru.korundm.enumeration.CompanyTypeEnum.CUSTOMERS
import ru.korundm.exception.AlertUIException
import java.time.LocalDate
import java.time.Month

@ViewController([RequestPath.View.Prod.WAREHOUSE_STATE])
class WarehouseStateViewProdController(
    private val companyService: CompanyService,
    private val storagePlaceService: StoragePlaceService,
    private val productService: ProductService,
    private val productTypeService: ProductTypeService,
    private val userService: UserService
) {

    @GetMapping("/list")
    fun list() = "prod/include/warehouse-state/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        model.addAttribute("companyList", companyService.getAllByType(CUSTOMERS).map { DropdownOption(it.id, it.name) })
        model.addAttribute("placeList", storagePlaceService.all.map { DropdownOption(it.id, it.name) })
        return "prod/include/warehouse-state/list/filter"
    }

    // Экземпляры изделий
    @GetMapping("/list/mat-value")
    fun listMatValue(model: ModelMap, id: Long): String {
        val product = productService.read(id) ?: throw AlertUIException("Изделие не найдено")
        model.addAttribute("id", id)
        model.addAttribute("productName", product.conditionalName)
        return "prod/include/warehouse-state/list/matValue"
    }

    // История движения экземляра изделия
    @GetMapping("/list/mat-value/history")
    fun listMatValueHistory(model: ModelMap, id: Long): String {
        model.addAttribute("id", id)
        return "prod/include/warehouse-state/list/mat-value/history"
    }

    // Отчет о Поступление и отгрузка изделия за период
    @GetMapping("/list/report-first")
    fun listReportFirst(model: ModelMap): String {
        model.addAttribute("periodDateFrom", LocalDate.of(2006, Month.JANUARY, 1).format(DATE_FORMATTER))
        model.addAttribute("periodDateTo", LocalDate.now().format(DATE_FORMATTER))
        return "prod/include/warehouse-state/list/reportFirst"
    }

    // Список изделий для отчета
    @GetMapping("/list/report-first/product")
    fun listReportFirstProduct() = "prod/include/warehouse-state/list/report-first/product"

    // Фильтр списка изделий для отчета
    @GetMapping("/list/report-first/product/filter")
    fun listReportFirstProductFilter(model: ModelMap): String {
        model.addAttribute("typeList", productTypeService.all.map { DropdownOption(it.id, it.name) })
        return "prod/include/warehouse-state/list/report-first/product/filter"
    }

    // Отчет о Поступление и отгрузка изделия за период
    @GetMapping("/list/report-second")
    fun listReportSecond(model: ModelMap): String {
        model.addAttribute("date", LocalDate.now().format(MONTH_FORMATTER))
        model.addAttribute("customer", "Линину С.А")
        model.addAttribute("chiefId", userService.findByUserName("zinkovskaya_ms")?.id)
        model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
        return "prod/include/warehouse-state/list/reportSecond"
    }
}