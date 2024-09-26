package ru.korundm.controller.view.prod

import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.RequestPath
import ru.korundm.dao.*
import ru.korundm.dto.DropdownOption
import ru.korundm.enumeration.CompanyTypeEnum.CONTRACTORS
import ru.korundm.enumeration.CompanyTypeEnum.CUSTOMERS
import ru.korundm.exception.AlertUIException
import ru.korundm.util.KtCommonUtil.contractFullNumber
import ru.korundm.util.KtCommonUtil.currencyFormat
import ru.korundm.util.KtCommonUtil.nullIfBlank
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate

@ViewController([RequestPath.View.Prod.SHIPMENT_WAYBILL])
class ShipmentWaybillViewProdController(
    private val shipmentWaybillService: ShipmentWaybillService,
    private val companyService: CompanyService,
    private val userService: UserService,
    private val accountService: AccountService,
    private val allotmentService: AllotmentService,
    private val valueAddedTaxService: ValueAddedTaxService,
    private val matValueService: MatValueService
) {

    @GetMapping("/list")
    fun list() = "prod/include/shipment-waybill/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap): String {
        model.addAttribute("payerList", companyService.getAllByType(CUSTOMERS).map { DropdownOption(it.id, it.name) })
        model.addAttribute("consigneeList", companyService.getAllByType(CUSTOMERS, CONTRACTORS).map { DropdownOption(it.id, it.name) })
        return "prod/include/shipment-waybill/list/filter"
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val waybill = id?.let { shipmentWaybillService.read(it) ?: throw AlertUIException("Накладная не найдена") }
        model.addAttribute("id", waybill?.id)
        model.addAttribute("version", waybill?.ver)
        model.addAttribute("number", waybill?.number)
        model.addAttribute("createDate", waybill?.createDate?.format(DATE_FORMATTER))
        model.addAttribute("shipmentDate", waybill?.shipmentDate?.format(DATE_FORMATTER))
        model.addAttribute("sectionName", waybill?.contractSection?.let {
            "${if (it.externalNumber.isNullOrBlank()) "" else "${it.externalNumber}/"}${contractFullNumber(it.contract?.number, it.contract?.performer?.id, it.contract?.type?.id, it.year, it.number)}"
        })
        model.addAttribute("accountId", waybill?.account?.id)
        model.addAttribute("payer", waybill?.contractSection?.contract?.customer?.name)
        model.addAttribute("consigneeId", waybill?.consignee?.id)

        val allotList = allotmentService.getAllByShipmentWaybillId(id)
        val oneHundred = BigDecimal.valueOf(100)
        model.addAttribute("vat", valueAddedTaxService.getAllByShipmentWaybillId(id).joinToString(", ") { it.name }.nullIfBlank())
        model.addAttribute("totalWoVat", allotList.sumOf { (it.amount.toBigDecimal() * (it.lot?.neededPrice ?: BigDecimal.ZERO)).toDouble() }.currencyFormat())
        model.addAttribute("totalVat", allotList.sumOf {
            (it.amount.toBigDecimal() * (it.lot?.neededPrice ?: BigDecimal.ZERO) * (BigDecimal.ONE + (it.lot?.vat?.value?.toBigDecimal()?.setScale(2) ?: BigDecimal.ZERO) / oneHundred)).setScale(2, HALF_UP).toDouble()
        }.currencyFormat())
        model.addAttribute("giveUser", waybill?.giveUser?.userOfficialName)
        model.addAttribute("permitUser", waybill?.permitUser?.userOfficialName)
        model.addAttribute("accountantUser", waybill?.accountantUser?.userOfficialName)
        model.addAttribute("transmittalLetter", waybill?.transmittalLetter)
        model.addAttribute("receiveUser", waybill?.receiver)
        model.addAttribute("letterOfAttorney", waybill?.letterOfAttorney)
        model.addAttribute("comment", waybill?.comment)

        val accountList = accountService.getAllByCompanyId(waybill?.contractSection?.contract?.customer?.id).map { DropdownOption(it.id, it.account) }.toMutableList()
        waybill?.contractSection?.separateAccount?.let { accountList += DropdownOption(it.id, it.account) }
        model.addAttribute("accountList", accountList)
        model.addAttribute("consigneeList", companyService.getAllByType(CUSTOMERS, CONTRACTORS).map { DropdownOption(it.id, it.name) })
        return "prod/include/shipment-waybill/list/edit"
    }

    @GetMapping("/list/check-shipment")
    fun listCheckShipment(model: ModelMap, id: Long): String {
        val waybill = shipmentWaybillService.read(id) ?: throw AlertUIException("Накладная не найдена")
        model.addAttribute("id", id)
        model.addAttribute("number", "№${waybill.number} от ${waybill.createDate.format(DATE_FORMATTER)}")
        return "prod/include/shipment-waybill/list/checkShipment"
    }

    // Окно выбора договора для накладной
    @GetMapping("/list/edit/contract")
    fun listEditContract() = "prod/include/shipment-waybill/list/edit/contract"

    @GetMapping("/list/edit/contract/filter")
    fun listEditContractFilter(model: ModelMap): String {
        model.addAttribute("companyList", companyService.getAllByType(CUSTOMERS).map { DropdownOption(it.id, it.name) })
        return "prod/include/shipment-waybill/list/edit/contract/filter"
    }

    // Отгрузка накладной
    @GetMapping("/list/shipment")
    fun listShipment(model: ModelMap, id: Long): String {
        val waybill = shipmentWaybillService.read(id) ?: throw AlertUIException("Накладная не найдена")
        if (waybill.shipmentDate != null) throw AlertUIException("Изделия в накладной были отгружены")
        if (matValueService.existsByShipmentWaybillId(id).not()) throw AlertUIException("Накладная не содержит ни одного изделия. Отгрузка изделий невозможна")
        model.addAttribute("id", id)
        model.addAttribute("number", waybill.number)
        model.addAttribute("createDate", waybill.createDate.format(DATE_FORMATTER))
        model.addAttribute("shipmentDate", LocalDate.now().format(DATE_FORMATTER))
        model.addAttribute("giveUserId", userService.findByUserName("zinkovskaya_ms")?.id)
        model.addAttribute("permitUserId", userService.findByUserName("repetey_aa")?.id)
        model.addAttribute("accountantUserId", userService.findByUserName("zotova_ee")?.id)
        model.addAttribute("userList", userService.all.map { DropdownOption(it.id, it.userOfficialName) })
        return "prod/include/shipment-waybill/list/shipment"
    }

    // Изделия в накладной
    @GetMapping("/list/mat-value")
    fun listMatValue(model: ModelMap, id: Long): String {
        val waybill = shipmentWaybillService.read(id) ?: throw AlertUIException("Накладная не найдена")
        model.addAttribute("waybillId", id)
        model.addAttribute("number", "Накладная №${waybill.number} от ${waybill.createDate.format(DATE_FORMATTER)}")
        return "prod/include/shipment-waybill/list/matValue"
    }

    // Добавление изделия в накладную
    @GetMapping("/list/mat-value/add")
    fun listMatValueAdd(model: ModelMap, id: Long): String {
        val waybill = shipmentWaybillService.read(id) ?: throw AlertUIException("Накладная не найдена")
        if (waybill.shipmentDate != null) throw AlertUIException("Изделия по накладной были отгружены")
        model.addAttribute("waybillId", id)
        model.addAttribute("number", "№${waybill.number} от ${waybill.createDate.format(DATE_FORMATTER)}")
        return "prod/include/shipment-waybill/list/mat-value/add"
    }

    // Фильтр окна добавления изделия в накладную
    @GetMapping("/list/mat-value/add/filter")
    fun listMatValueAddFilter() = "prod/include/shipment-waybill/list/mat-value/add/filter"
}