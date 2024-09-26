package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.dto.DropdownOption
import ru.korundm.enumeration.CompanyTypeEnum.CUSTOMERS
import ru.korundm.exception.AlertUIException

@ViewController([RequestPath.View.Prod.INTERNAL_WAYBILL])
class InternalWaybillViewProdController(
    private val internalWaybillService: InternalWaybillService,
    private val companyService: CompanyService,
    private val userService: UserService,
    private val matValueService: MatValueService,
    private val storagePlaceService: StoragePlaceService
) {

    @GetMapping("/list")
    fun list() = "prod/include/internal-waybill/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        model.addAttribute("companyList", companyService.getAllByType(CUSTOMERS).map { DropdownOption(it.id, it.name) })
        return "prod/include/internal-waybill/list/filter"
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val matValueList = matValueService.getAllByInternalWaybillId(id)
        if (matValueList.any { it.shipmentWaybill != null }) throw AlertUIException("МСН содержит изделия, добавленные в накладные на отгрузку. Редактирование невозможно")
        val waybill = id?.let { internalWaybillService.read(it) ?: throw AlertUIException("Накладная не найдена") }
        model.addAttribute("id", waybill?.id)
        model.addAttribute("version", waybill?.ver)
        model.addAttribute("number", waybill?.number?.let { "${it.toString().padStart(3, '0')}/АО" })
        model.addAttribute("createDate", waybill?.createDate?.format(DATE_FORMATTER))
        model.addAttribute("acceptDate", waybill?.acceptDate?.format(DATE_FORMATTER))
        model.addAttribute("giveUser", waybill?.giveUser?.userOfficialName)
        model.addAttribute("acceptUser", waybill?.acceptUser?.userOfficialName)
        model.addAttribute("storagePlace", waybill?.storagePlace?.name)
        model.addAttribute("comment", waybill?.comment)
        model.addAttribute("storagePlaceList", storagePlaceService.all.map { DropdownOption(it.id, it.name) })
        return "prod/include/internal-waybill/list/edit"
    }

    // Принятие накладной
    @GetMapping("/list/accept")
    fun listAccept(model: ModelMap, id: Long): String {
        val waybill = internalWaybillService.read(id) ?: throw AlertUIException("Накладная не найдена")
        if (waybill.acceptDate != null) throw AlertUIException("Накладная была принята")
        if (matValueService.existsByInternalWaybillId(id).not()) throw AlertUIException("МСН не содержит ни одного изделия. Принятие изделий невозможно")
        model.addAttribute("id", id)
        model.addAttribute("number", "${waybill.number.toString().padStart(3, '0')}/АО")
        model.addAttribute("createDate", waybill.createDate.format(DATE_FORMATTER))
        model.addAttribute("giveUserId", userService.findByUserName("shpilman_vm")?.id)
        model.addAttribute("acceptUserId", userService.findByUserName("zinkovskaya_ms")?.id)
        model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
        return "prod/include/internal-waybill/list/accept"
    }

    // Изделия в накладной
    @GetMapping("/list/mat-value")
    fun listMatValue(model: ModelMap, id: Long): String {
        val waybill = internalWaybillService.read(id) ?: throw AlertUIException("Накладная не найдена")
        model.addAttribute("waybillId", id)
        model.addAttribute("number", "Накладная ${waybill.number.toString().padStart(3, '0')}/АО")
        return "prod/include/internal-waybill/list/matValue"
    }

    // Добавление изделия в накладную
    @GetMapping("/list/mat-value/add")
    fun listMatValueAdd(model: ModelMap, id: Long): String {
        val waybill = internalWaybillService.read(id) ?: throw AlertUIException("Накладная не найдена")
        if (waybill.acceptDate != null) throw AlertUIException("Накладная была принята")
        model.addAttribute("waybillId", id)
        model.addAttribute("number", "${waybill.number.toString().padStart(3, '0')}/АО")
        return "prod/include/internal-waybill/list/mat-value/add"
    }

    // Фильтр окна добавления изделия в накладную
    @GetMapping("/list/mat-value/add/filter")
    fun listMatValueAddFilter() = "prod/include/internal-waybill/list/mat-value/add/filter"
}