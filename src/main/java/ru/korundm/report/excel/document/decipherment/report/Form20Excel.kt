package ru.korundm.report.excel.document.decipherment.report

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import ru.korundm.dao.BaseService
import ru.korundm.dao.BasicEconomicIndicatorService
import ru.korundm.dao.ProductDeciphermentAttrValService
import ru.korundm.dao.ProductDeciphermentService
import ru.korundm.entity.BasicEconomicIndicator
import ru.korundm.entity.Product
import ru.korundm.entity.ProductDecipherment
import ru.korundm.enumeration.ProductDeciphermentAttr.*
import ru.korundm.enumeration.ProductDeciphermentTypeEnum
import ru.korundm.helper.AutowireHelper.autowire
import ru.korundm.helper.manager.decipherment.DeciphermentManager
import ru.korundm.report.excel.document.decipherment.DeciphermentExcel
import ru.korundm.util.KtCommonUtil
import java.io.File
import java.math.BigDecimal

class Form20Excel : DeciphermentExcel {

    @Autowired
    private lateinit var deciphermentService: ProductDeciphermentService

    @Autowired
    private lateinit var productDeciphermentAttrValService: ProductDeciphermentAttrValService

    @Autowired
    private lateinit var basicEconomicIndicatorService: BasicEconomicIndicatorService

    @Autowired
    private lateinit var baseService: BaseService

    @Autowired
    private lateinit var util: DeciphermentManager

    private lateinit var workbook: Workbook
    private lateinit var decipherment: ProductDecipherment
    private lateinit var product: Product

    private lateinit var planPeriodYear: String
    private lateinit var headEco: String
    private var firstProfitability = BigDecimal.ZERO
    private var secondProfitability = BigDecimal.ZERO

    override fun generate(deciphermentId: Long): Workbook {
        autowire(this)
        workbook = XSSFWorkbook(KtCommonUtil.sourceFile("blank" + File.separator + "excel", "form20.xlsx"))
        decipherment = deciphermentService.read(deciphermentId) ?: return util.clearTag(workbook)
        product = decipherment.period?.product ?: return util.clearTag(workbook)
        baseService.exec {
            // Плановый года
            planPeriodYear = decipherment.period?.pricePeriod?.startDate?.year?.toString() ?: "    "
            // Подписанты
            headEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user?.userShortName ?: ""
            firstProfitability = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROFITABILITY_FIRST)?.decimalVal ?: BigDecimal.ZERO
            secondProfitability = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROFITABILITY_SECOND)?.decimalVal ?: BigDecimal.ZERO
        }
        val map = mutableMapOf<String, String>()
        map["&YEAR&"] = planPeriodYear
        map["&PEO&"] = headEco
        map["&FIRST&"] = "$firstProfitability%"
        map["&SECOND&"] = "$secondProfitability%"
        map["&PRODUCT&"] = product.techSpecName ?: ""
        val mapDecimal = mutableMapOf<String, BigDecimal>()
        val oneHundred = BigDecimal.valueOf(100)

        val form2 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_2)
        val bei: BasicEconomicIndicator? = basicEconomicIndicatorService.read(productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(form2, BASIC_PLAN_ECO_INDICATOR)?.longVal ?: 0)

        val form6and1 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_6_1)
        val purchaseComponent = if (form6and1 != null) util.total6Form(form6and1) else BigDecimal.ZERO

        val form6and2 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_6_2)
        val containerPacking = if (form6and2 != null) util.total6Form(form6and2) else BigDecimal.ZERO

        val form9 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_9)
        val sumForm9 = if (form9 != null) util.total9Form(form9).first else BigDecimal.ZERO
        val laborCosts = sumForm9 + sumForm9 * (bei?.additionalSalary?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        val insurancePremium = sumForm9 + sumForm9 * (bei?.additionalSalary?.toBigDecimal() ?: BigDecimal.ZERO) * (bei?.socialInsurance?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        val generalProductionCosts = sumForm9 * (bei?.productionCosts?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        val generalOperatingCosts = sumForm9 * (bei?.householdExpenses?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred

        val form18 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_18)
        val otherDirestCosts = if (form18 != null) util.total18Form(form18).first else BigDecimal.ZERO
        val otherDirestCostsResearch = if (form18 != null) util.total18Form(form18).second + otherDirestCosts else BigDecimal.ZERO

        mapDecimal["&0200&"] = sumForm9
        mapDecimal["&0300&"] = laborCosts
        mapDecimal["&0400&"] = insurancePremium
        mapDecimal["&0500&"] = generalProductionCosts
        mapDecimal["&0600&"] = generalOperatingCosts
        mapDecimal["&0800&"] = purchaseComponent
        mapDecimal["&0900&"] = containerPacking
        mapDecimal["&1000&"] = otherDirestCosts + otherDirestCostsResearch
        mapDecimal["&0100&"] = sumForm9 + laborCosts + insurancePremium + generalProductionCosts +generalOperatingCosts
        mapDecimal["&0700&"] = purchaseComponent + containerPacking + otherDirestCosts + otherDirestCostsResearch

        mapDecimal["&0200r&"] = sumForm9 * firstProfitability / oneHundred
        mapDecimal["&0300r&"] = laborCosts * firstProfitability / oneHundred
        mapDecimal["&0400r&"] = insurancePremium * firstProfitability / oneHundred
        mapDecimal["&0500r&"] = generalProductionCosts * firstProfitability / oneHundred
        mapDecimal["&0600r&"] = generalOperatingCosts * firstProfitability / oneHundred
        mapDecimal["&0800r&"] = purchaseComponent * secondProfitability / oneHundred
        mapDecimal["&0900r&"] = containerPacking * secondProfitability / oneHundred
        mapDecimal["&1000r&"] = (otherDirestCosts + otherDirestCostsResearch) * secondProfitability / oneHundred

        val ownCost = (sumForm9 + laborCosts + insurancePremium + generalProductionCosts + generalOperatingCosts) * firstProfitability / oneHundred
        val addedCost = (purchaseComponent + containerPacking + otherDirestCosts + otherDirestCostsResearch) * secondProfitability / oneHundred

        mapDecimal["&0100r&"] = ownCost
        mapDecimal["&0700r&"] = addedCost
        mapDecimal["&1111&"] = addedCost + ownCost

        workbook.forEach { replaceTags(map, it) }
        workbook.forEach { replaceTagsForDecimal(mapDecimal, it) }
        return workbook
    }

    fun replaceTags(map: Map<String, String>, sheet: Sheet): Sheet {
        for (i in 0 until sheet.lastRowNum) {
            val row = sheet.getRow(i)
            if (row != null) {
                for (j in 0 until row.lastCellNum) {
                    val cell = row.getCell(j)
                    if (cell != null && cell.cellType == CellType.STRING) {
                        var text = cell.stringCellValue
                        for (key in map.keys) {
                            if (text.contains(key)) {
                                text = text.replace(key, map[key] ?: "")
                                cell.setCellValue(text)
                            }
                        }
                    }
                }
            }
        }
        return sheet
    }

    fun replaceTagsForDecimal(map: Map<String, BigDecimal>, sheet: Sheet): Sheet {
        for (i in 0 until sheet.lastRowNum) {
            val row = sheet.getRow(i)
            if (row != null) {
                for (j in 0 until row.lastCellNum) {
                    val cell = row.getCell(j)
                    if (cell != null && cell.cellType == CellType.STRING) {
                        val text = cell.stringCellValue
                        for (key in map.keys) {
                            if (text.contains(key)) {
                                cell.setCellValue(map[key]?.toDouble() ?: 0.0)
                            }
                        }
                    }
                }
            }
        }
        return sheet
    }
}