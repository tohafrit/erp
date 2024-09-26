package ru.korundm.report.excel.document.decipherment.report

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import ru.korundm.dao.*
import ru.korundm.entity.BasicEconomicIndicator
import ru.korundm.entity.ProductDecipherment
import ru.korundm.enumeration.ProductDeciphermentAttr.*
import ru.korundm.enumeration.ProductDeciphermentTypeEnum
import ru.korundm.enumeration.ProductDeciphermentTypeEnum.FORM_2
import ru.korundm.enumeration.ProductDeciphermentTypeEnum.FORM_3
import ru.korundm.helper.AutowireHelper.autowire
import ru.korundm.helper.manager.decipherment.DeciphermentManager
import ru.korundm.report.excel.document.decipherment.DeciphermentExcel
import ru.korundm.util.KtCommonUtil.sourceFile
import java.io.File
import java.math.BigDecimal

class Form2or3Excel : DeciphermentExcel {

    @Autowired
    private lateinit var deciphermentService: ProductDeciphermentService

    @Autowired
    private lateinit var productDeciphermentAttrValService: ProductDeciphermentAttrValService

    @Autowired
    private lateinit var baseService: BaseService

    @Autowired
    private lateinit var basicEconomicIndicatorService: BasicEconomicIndicatorService

    @Autowired
    private lateinit var okpdCodeService: OkpdCodeService

    @Autowired
    private lateinit var util: DeciphermentManager

    override fun generate(deciphermentId: Long): Workbook {
        autowire(this)
        lateinit var deciphermentReport: ProductDecipherment
        lateinit var workbook: Workbook
        var basePlanEcoIndicator: BasicEconomicIndicator? = null
        var planPeriodYear = ""
        var reportPeriodYear = ""
        var headEco = ""
        var directorEco = ""
        var accountant = ""
        val decipherment = deciphermentService.read(deciphermentId) ?: return XSSFWorkbook()
        val product = decipherment.period?.product ?: return XSSFWorkbook()

        val enumType = decipherment.type?.enum
        when (enumType) {
            FORM_2 -> {
                workbook = XSSFWorkbook(sourceFile("blank" + File.separator + "excel", "form2.xlsx"))
                // Плановый года
                planPeriodYear = decipherment.period?.pricePeriod?.startDate?.year?.toString() ?: "    "
                // Подписанты
                headEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user?.userShortName ?: ""
                basePlanEcoIndicator = basicEconomicIndicatorService.read(productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, BASIC_PLAN_ECO_INDICATOR)?.longVal ?: 0)
            }
            FORM_3 -> {
                workbook = XSSFWorkbook(sourceFile("blank" + File.separator + "excel", "form3.xlsx"))
                deciphermentReport = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.prevPeriod?.id, FORM_2) ?: return util.clearTag(workbook)
                baseService.exec {
                    reportPeriodYear = decipherment.period?.prevPeriod?.pricePeriod?.startDate?.year?.toString() ?: "    "
                    // Подписанты
                    basePlanEcoIndicator = basicEconomicIndicatorService.read(productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(deciphermentReport, BASIC_PLAN_ECO_INDICATOR)?.longVal ?: 0)
                    directorEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO)?.user?.userShortName ?: ""
                    accountant = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT)?.user?.userShortName ?: ""
                }
            }
            else -> throw IllegalStateException("unknown decipherment type")
        }

        val map = mutableMapOf<String, String>()
        map["&YEAR&"] = if (enumType == FORM_2) planPeriodYear else reportPeriodYear
        map["&OKPD&"] = okpdCodeService.getFirstByProductTypeId(product.type?.id)?.code ?: ""
        map["&TECH_DOC&"] = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, TECH_DOC)?.stringVal ?: ""
        map["&PRODUCT&"] = product.techSpecName ?: ""
        map["&BUCH&"] = if (enumType == FORM_3) accountant else ""
        map["&DIR_ECO&"] = if (enumType == FORM_3) directorEco else ""
        map["&PEO&"] = if (enumType == FORM_2) headEco else ""

        val mapDecimal: MutableMap<String, BigDecimal> = HashMap()
        val currentDecipherment = if (decipherment.type?.enum == FORM_2) decipherment else deciphermentReport

        val form4 = deciphermentService.getFirstByPeriodIdAndType(currentDecipherment.period?.id, ProductDeciphermentTypeEnum.FORM_4)
        val purchaseRawMaterials = if (form4 != null) util.total6Form(form4) else BigDecimal.ZERO
        mapDecimal["&0101&"] = purchaseRawMaterials

        val form6and1 = deciphermentService.getFirstByPeriodIdAndType(currentDecipherment.period?.id, ProductDeciphermentTypeEnum.FORM_6_1)
        val purchaseComponent = if (form6and1 != null) util.total6Form(form6and1) else BigDecimal.ZERO
        mapDecimal["&0104&"] = purchaseComponent

        val form6and2 = deciphermentService.getFirstByPeriodIdAndType(currentDecipherment.period?.id, ProductDeciphermentTypeEnum.FORM_6_2)
        val containerPacking = if (form6and2 != null) util.total6Form(form6and2) else BigDecimal.ZERO
        mapDecimal["&0109&"] = containerPacking
        val form6and3 = deciphermentService.getFirstByPeriodIdAndType(currentDecipherment.period?.id, ProductDeciphermentTypeEnum.FORM_6_3)
        val costsOfOwnProduct = if (form6and3 != null) util.total6Form(form6and3) else BigDecimal.ZERO
        mapDecimal["&0110&"] = costsOfOwnProduct

        val materialCosts = purchaseRawMaterials + purchaseComponent + containerPacking + costsOfOwnProduct
        mapDecimal["&0100&"] = materialCosts
        mapDecimal["&01001&"] = materialCosts - containerPacking

        val oneHundred = BigDecimal.valueOf(100)
        val form9 = deciphermentService.getFirstByPeriodIdAndType(currentDecipherment.period?.id, ProductDeciphermentTypeEnum.FORM_9)
        val sumForm9 = if (form9 != null) util.total9Form(form9).first else BigDecimal.ZERO

        mapDecimal["&2222&"] = if (form9 != null) util.total9Form(form9).second else BigDecimal.ZERO
        mapDecimal["&0202&"] = sumForm9 * (basePlanEcoIndicator?.additionalSalary?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        mapDecimal["&0201&"] = sumForm9
        val laborCosts = sumForm9 + sumForm9 * (basePlanEcoIndicator?.additionalSalary?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        mapDecimal["&0200&"] = laborCosts

        val insurancePremium = sumForm9 + sumForm9 * (basePlanEcoIndicator?.additionalSalary?.toBigDecimal() ?: BigDecimal.ZERO) * (basePlanEcoIndicator?.socialInsurance?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        mapDecimal["&0300&"] = insurancePremium

        val generalProductionCosts = sumForm9 * (basePlanEcoIndicator?.productionCosts?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        mapDecimal["&0800&"] = generalProductionCosts
        val generalOperatingCosts = sumForm9 * (basePlanEcoIndicator?.householdExpenses?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred
        mapDecimal["&0900&"] = generalOperatingCosts

        val form18 = deciphermentService.getFirstByPeriodIdAndType(currentDecipherment.period?.id, ProductDeciphermentTypeEnum.FORM_18)
        val otherDirestCosts = if (form18 != null) util.total18Form(form18).first else BigDecimal.ZERO

        val otherDirestCostsResearch = if (form18 != null) util.total18Form(form18).second else BigDecimal.ZERO
        mapDecimal["&1100&"] = otherDirestCosts
        mapDecimal["&1111&"] = otherDirestCosts + otherDirestCostsResearch

        mapDecimal["&1300&"] = materialCosts + laborCosts + insurancePremium + generalProductionCosts + generalOperatingCosts + otherDirestCosts
        mapDecimal["&1301&"] = materialCosts + laborCosts + insurancePremium + generalProductionCosts + generalOperatingCosts + otherDirestCosts - containerPacking
        mapDecimal["&1302&"] = materialCosts + laborCosts + insurancePremium + generalProductionCosts + generalOperatingCosts + otherDirestCosts + otherDirestCostsResearch

        val productionCost = materialCosts + laborCosts + insurancePremium + generalProductionCosts + generalOperatingCosts + otherDirestCosts
        val form20 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, ProductDeciphermentTypeEnum.FORM_20)
        val profit = if (form20 != null) util.total20Form(form20) else BigDecimal.ZERO

        mapDecimal["&1800&"] = profit
        mapDecimal["&1700&"] = productionCost
        mapDecimal["&1701&"] = productionCost - containerPacking
        mapDecimal["&1702&"] = productionCost + otherDirestCostsResearch
        mapDecimal["&1900&"] = productionCost + profit
        mapDecimal["&1901&"] = productionCost + profit - containerPacking
        mapDecimal["&1902&"] = productionCost + profit - otherDirestCostsResearch

        workbook.forEach {
            replaceTags(map, it)
            replaceTagsForDecimal(mapDecimal, it)
        }
        return workbook
    }

    private fun replaceTags(map: Map<String, String>, sheet: Sheet): Sheet {
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

    private fun replaceTagsForDecimal(map: Map<String, BigDecimal>, sheet: Sheet): Sheet {
        for (i in 0 until sheet.lastRowNum) {
            val row = sheet.getRow(i)
            if (row != null) {
                for (j in 0 until row.lastCellNum) {
                    val cell = row.getCell(j)
                    if (cell != null && cell.cellType == CellType.STRING) {
                        val text = cell.stringCellValue
                        for (key in map.keys) {
                            if (text.contains(key)) cell.setCellValue(map[key]?.toDouble() ?: 0.0)
                        }
                    }
                }
            }
        }
        return sheet
    }
}