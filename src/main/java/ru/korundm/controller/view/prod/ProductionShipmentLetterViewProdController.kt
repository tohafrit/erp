package ru.korundm.controller.view.prod

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import ru.korundm.annotation.ViewController
import ru.korundm.constant.BaseConstant.DATE_FORMATTER
import ru.korundm.constant.BaseConstant.ONE_INT
import ru.korundm.constant.BaseConstant.SCALE
import ru.korundm.constant.BaseConstant.ZERO_LONG
import ru.korundm.constant.RequestPath
import ru.korundm.dao.AllotmentService
import ru.korundm.dao.ContractSectionService
import ru.korundm.dao.ProductionShipmentLetterService
import ru.korundm.dto.DropdownOption
import ru.korundm.enumeration.ContractType
import ru.korundm.exception.AlertUIException
import java.math.BigDecimal
import java.math.RoundingMode.HALF_DOWN
import java.math.RoundingMode.HALF_UP
import java.time.LocalDate
import ru.korundm.form.ProductionShipmentLetterListEditForm as ListEditForm

@ViewController([RequestPath.View.Prod.PRODUCTION_SHIPMENT_LETTER])
class ProductionShipmentLetterViewProdController(
    private val jsonMapper: ObjectMapper,
    private val productionShipmentLetterService: ProductionShipmentLetterService,
    private val contractSectionService: ContractSectionService,
    private val allotmentService: AllotmentService
) {

    @GetMapping("/list")
    fun list(model: ModelMap) = "prod/include/production-shipment-letter/list"

    @GetMapping("/list/filter")
    fun listFilter(model: ModelMap) = "prod/include/production-shipment-letter/list/filter"

    // Редактирование/добавление письма
    @GetMapping("/list/edit")
    fun listEdit(model: ModelMap, id: Long?): String {
        val letter = id?.let { productionShipmentLetterService.read(it) ?: throw AlertUIException("Письмо на производство не найдено") }
        // Находим последнее письмо
        val lastLetter = productionShipmentLetterService.findLastLetter()
        var letterNumber = lastLetter?.number ?: ONE_INT
        //
        val hundred = BigDecimal.valueOf(100)
        model.addAttribute("form", ListEditForm(id).apply {
            number = letter?.number ?: lastLetter?.let { ++letterNumber } ?: letterNumber
            createDate = letter?.createDate ?: LocalDate.now()
            sendToWarehouseDate = letter?.sendToWarehouseDate
            sendToProductionDate = letter?.sendToProductionDate
            allotmentIdList = id?.let { jsonMapper.writeValueAsString(allotmentService.getAllByMatValueListLetterId(it).map { allotment ->
                val lot = allotment.lot
                val lotGroup = lot?.lotGroup
                val contractNumber = lotGroup?.contractSection?.fullNumber
                val deliveryDate = lot?.deliveryDate
                val formattedDate = deliveryDate?.format(DATE_FORMATTER)
                val valueAddedTax = lot?.vat
                // Величина процентной ставки НДС
                val vat = valueAddedTax?.value?.toBigDecimal() ?: BigDecimal.ZERO

                // Коэффициент НДС
                val vatRatio = vat / hundred + BigDecimal.ONE
                var costWithVAT = vatRatio * allotment.amount.toBigDecimal() * allotment.neededPrice
                costWithVAT = costWithVAT.setScale(SCALE, HALF_DOWN)
                val paid = allotment.paid.setScale(SCALE, HALF_DOWN)

                // Получение процентного соотношения оплаченной суммы к стоимости allotment-a
                val percentPaid = if (costWithVAT.compareTo(BigDecimal.ZERO) != 0) {
                    paid * hundred / costWithVAT.setScale(SCALE, HALF_UP)
                } else {
                    BigDecimal.ZERO
                }

                val launchProduct = allotment.launchProduct
                val launchAmount = if (launchProduct != null) allotment.amount else ZERO_LONG
                val launchNumber = launchProduct?.launch?.numberInYear ?: ""
                //
                ListEditForm.AllotmentList(
                    allotment.id,
                    contractNumber,
                    lotGroup?.product?.conditionalName,
                    "Договор № $contractNumber",
                    deliveryDate,
                    "Дата поставки: $formattedDate",
                    allotment.amount,
                    costWithVAT,
                    paid,
                    percentPaid,
                    allotment.finalPrice,
                    launchNumber,
                    launchAmount
                )
            }.toList()) } ?: "[]"
            comment = letter?.comment ?: ""
        })
        return "prod/include/production-shipment-letter/list/edit"
    }

    // Распределение изделий по договорам для письма
    @GetMapping("/list/letter-info")
    fun listLogRecordInfo(
        model: ModelMap,
        id: Long
    ): String {
        val letter = productionShipmentLetterService.read(id)
        val fullNumber = letter.fullNumber
        val createDate = letter.createDate?.format(DATE_FORMATTER)
        model.addAttribute("letterId", letter.id)
        model.addAttribute("fullNumber", fullNumber)
        model.addAttribute("createDate", createDate)
        return "prod/include/production-shipment-letter/list/distribution"
    }

    @GetMapping("/list/edit/contract")
    fun listEditContract() = "prod/include/production-shipment-letter/list/edit/contract"

    @GetMapping("/list/edit/contract/filter")
    fun listEditContractFilter(model: ModelMap): String {
        val contractTypeSearchList = listOf(
            ContractType.PRODUCT_SUPPLY,
            ContractType.SUPPLY_OF_EXPORTED,
            ContractType.SCIENTIFIC_AND_TECHNICAL,
            ContractType.INTERNAL_APPLICATION,
            ContractType.OTHER,
            ContractType.SERVICES,
            ContractType.ORDER_WITHOUT_EXECUTION_OF_THE_CONTRACT,
            ContractType.REPAIR_OTK,
            ContractType.REPAIR_PZ
        )
        val contractTypeList = contractTypeSearchList.map { DropdownOption(it.id, it.code) }.toList()
        model.addAttribute("contractTypeList", contractTypeList)
        return "prod/include/production-shipment-letter/list/edit/contract/filter"
    }

    @GetMapping("/list/edit/contract/product")
    fun listEditContractProduct(
        model: ModelMap,
        contractSectionId: Long
    ): String {
        val section = contractSectionService.read(contractSectionId) ?: throw AlertUIException("Договор был удален")
        model.addAttribute("contractSectionId", contractSectionId)
        model.addAttribute("sectionFullNumber", section.fullNumber)
        return "prod/include/production-shipment-letter/list/edit/contract/product"
    }

    @GetMapping("/list/edit/contract/product/filter")
    fun listEditContractProductFilter() = "prod/include/production-shipment-letter/list/edit/contract/product/filter"
}