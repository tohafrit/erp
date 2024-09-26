package ru.korundm.helper.manager.decipherment

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.stereotype.Component
import ru.korundm.dao.*
import ru.korundm.entity.BasicEconomicIndicator
import ru.korundm.entity.ProductDecipherment
import ru.korundm.entity.WorkType
import ru.korundm.enumeration.ProductDeciphermentAttr.*
import ru.korundm.enumeration.ProductDeciphermentTypeEnum
import ru.korundm.enumeration.ProductDeciphermentTypeEnum.FORM_9
import java.math.BigDecimal

@Component
class DeciphermentManager(
    private val manager: CompositionManager,
    private val deciphermentService: ProductDeciphermentService,
    private val labourIntensityOperationService: ProductLabourIntensityOperationService,
    private val productWorkCostService: ProductWorkCostService,
    private val productDeciphermentAttrValService: ProductDeciphermentAttrValService,
    private val productSpecReviewPriceService: ProductSpecReviewPriceService,
    private val productSpecResearchPriceService: ProductSpecResearchPriceService,
    private val basicEconomicIndicatorService: BasicEconomicIndicatorService
) {

    private val oneHundred = BigDecimal.valueOf(100)

    /**
     * Метод для получения итого по 4 и 6 форме
     */
    fun total6Form(decipherment: ProductDecipherment): BigDecimal {
        val product = manager.createRootDataProduct(decipherment)
        val componentInvoiceList = manager.getComponentInvoiceList(decipherment)
        var sum = BigDecimal.ZERO

        for (item in product.componentList) {
            val invoiceData = componentInvoiceList?.firstOrNull { it?.componentId == item?.componentId }
            if (invoiceData != null) sum += item.quantity.toBigDecimal() * invoiceData.price.toBigDecimal()
        }
        for (subItem in product.subProductList) {
            for (subItemComponent in subItem.componentList) {
                val invoiceData = componentInvoiceList?.firstOrNull { it?.componentId == subItemComponent?.componentId }
                if (invoiceData != null) sum += subItemComponent.quantity.toBigDecimal() * invoiceData.price.toBigDecimal()
            }
        }
        return sum
    }

    /**
     * Метод для получения итого по 9 форме
     */
    fun total9Form(decipherment: ProductDecipherment): Pair <BigDecimal, BigDecimal> {
        val product =  decipherment.period?.product
        val reportDecipherment = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.prevPeriod?.id, FORM_9)

        // Данные текущей расшифровки
        val justificationId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_WORK_COST_JUSTIFICATION)?.productWorkCostJustification?.id
        val labourIntensityId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_LABOUR_INTENSITY)?.productLabourIntensity?.id
        val reportLabourIntensityId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(reportDecipherment, PRODUCT_LABOUR_INTENSITY)?.productLabourIntensity?.id

        val workCostList = productWorkCostService.getAllByJustificationId(justificationId)
        val labourIntensityList = labourIntensityOperationService.getAllByLabourIntensityIdAndProductId(labourIntensityId, product?.id)
        val reportLabourIntensityList = labourIntensityOperationService.getAllByLabourIntensityIdAndProductId(reportLabourIntensityId, product?.id)

        // Собираем список работ
        val workTypeList = mutableListOf<WorkType>()
        workTypeList.addAll(labourIntensityList.filterNot { workTypeList.contains(it.operation) }.mapNotNull { it.operation })
        workTypeList.addAll(reportLabourIntensityList.filterNot { workTypeList.contains(it.operation) }.mapNotNull { it.operation })

        var sum = BigDecimal.ZERO
        var labourSum = BigDecimal.ZERO
        workTypeList.forEach { workType ->
            val workCost = workCostList.find { workType.id == it.workType?.id }
            val labourIntensity = labourIntensityList.find { workType.id == it.operation?.id }
            workCost?.cost?.times(labourIntensity?.value ?: .0)?.let { sum += it.toBigDecimal() }
            labourIntensity?.value?.let { labourSum += it.toBigDecimal() }
        }
        return Pair(sum, labourSum)
    }

    /**
     * Метод для получения итого по 18 форме
     */
    fun total18Form(decipherment: ProductDecipherment): Pair <BigDecimal, BigDecimal> {
        val product =  decipherment.period?.product
        var sum = BigDecimal.ZERO
        var researchSum = BigDecimal.ZERO
        val groupId = product?.classificationGroup?.id
        val reviewJustificationId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_SPEC_REVIEW_JUSTIFICATION)?.productSpecReviewJustification?.id
        val researchJustificationId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_SPEC_RESEARCH_JUSTIFICATION)?.productSpecResearchJustification?.id
        productSpecReviewPriceService.getByJustificationIdAndGroupId(reviewJustificationId, groupId)?.let { sum = it.price.toBigDecimal() }
        productSpecResearchPriceService.getByJustificationIdAndGroupId(researchJustificationId, groupId)?.let { researchSum = it.price.toBigDecimal() }
        return Pair(sum, researchSum)
    }

    /**
     * Метод для получения итого по 20 форме
     */
    fun total20Form(decipherment: ProductDecipherment): BigDecimal {
        val firstProfitability = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROFITABILITY_FIRST)?.decimalVal ?: BigDecimal.ZERO
        val secondProfitability = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROFITABILITY_SECOND)?.decimalVal ?: BigDecimal.ZERO
        val form2 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_2)
        val basePlanEcoIndicator: BasicEconomicIndicator? = basicEconomicIndicatorService.read(productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(form2, BASIC_PLAN_ECO_INDICATOR)?.longVal ?: 0)
        val form6and1 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_6_1)
        val purchaseComponent = if (form6and1 != null) total6Form(form6and1) else BigDecimal.ZERO

        val form6and2 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_6_2)
        val containerPacking = if (form6and2 != null) total6Form(form6and2) else BigDecimal.ZERO

        val form9 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, FORM_9)
        val sumForm9 = if (form9 != null) total9Form(form9).first else BigDecimal.ZERO
        val laborCosts = sumForm9 + sumForm9 * (basePlanEcoIndicator?.additionalSalary?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        val insurancePremium = sumForm9 + sumForm9 * (basePlanEcoIndicator?.additionalSalary?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred * (basePlanEcoIndicator?.socialInsurance?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        val generalProductionCosts = sumForm9 * (basePlanEcoIndicator?.productionCosts?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        val generalOperatingCosts = sumForm9 * (basePlanEcoIndicator?.householdExpenses?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred

        val form18 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_18)
        val otherDirestCosts = if (form18 != null) total18Form(form18).first else BigDecimal.ZERO
        val otherDirestCostsResearch = if (form18 != null) total18Form(form18).second + otherDirestCosts else BigDecimal.ZERO

        val ownCost = (sumForm9 + laborCosts + insurancePremium + generalProductionCosts + generalOperatingCosts) * firstProfitability / oneHundred
        val addedCost = (purchaseComponent + containerPacking + otherDirestCosts + otherDirestCostsResearch) * secondProfitability / oneHundred

        return ownCost + addedCost
    }

    /**
     * Метод для очищения файла от меток
     */
    fun clearTag(document: Workbook): Workbook {
        for (sheet in document) {
            for (i in 0 until sheet.lastRowNum) {
                val row = sheet.getRow(i)
                if (row != null) {
                    for (j in 0 until row.lastCellNum) {
                        val cell = row.getCell(j)
                        if (cell != null && cell.cellType == CellType.STRING) {
                            val text = cell.stringCellValue
                            if (text.contains(Regex("""&[0-9A-Z_]+&"""))) {
                                text.replace(Regex("""&[0-9A-Z_]+&"""), "")
                                cell.setCellValue("")
                            }
                        }
                    }
                }
            }
        }
        return document
    }
}