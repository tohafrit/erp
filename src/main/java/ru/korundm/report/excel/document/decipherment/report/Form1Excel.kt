package ru.korundm.report.excel.document.decipherment.report

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import ru.korundm.dao.*
import ru.korundm.entity.BasicEconomicIndicator
import ru.korundm.enumeration.ProductDeciphermentAttr.*
import ru.korundm.enumeration.ProductDeciphermentTypeEnum.*
import ru.korundm.helper.AutowireHelper.autowire
import ru.korundm.helper.manager.decipherment.DeciphermentManager
import ru.korundm.report.excel.document.decipherment.DeciphermentExcel
import ru.korundm.util.KtCommonUtil.sourceFile
import java.io.File
import java.math.BigDecimal

class Form1Excel : DeciphermentExcel {

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

    @Autowired
    private lateinit var okpdCodeService: OkpdCodeService

    override fun generate(deciphermentId: Long): Workbook {
        autowire(this)
        val workbook = XSSFWorkbook(sourceFile("blank" + File.separator + "excel", "form1.xlsx"))

        val decipherment = deciphermentService.read(deciphermentId) ?: return workbook
        val product = decipherment.period?.product

        var headEco = ""
        var directorEco = ""
        var planPeriodYear = "    "
        baseService.exec {
            headEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user?.userShortName ?: ""
            directorEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO)?.user?.userShortName ?: ""
            planPeriodYear = decipherment.period?.pricePeriod?.startDate?.year?.toString() ?: "    "
        }

        val oneHundred = BigDecimal.valueOf(100)
        val map = mutableMapOf<String, String>()
        map["&YEAR&"] = planPeriodYear
        map["&CUSTOMER&"] = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CUSTOMER)?.company?.name ?: ""
        map["&PROCEDURE&"] = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PROCEDURE)?.stringVal ?: ""
        map["&PRICE_DETERMINATION&"] = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRICE_DETERMINATION)?.stringVal ?: ""
        map["&NOTE&"] = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, NOTE)?.stringVal ?: ""
        map["&OKPD&"] = okpdCodeService.getFirstByProductTypeId(product?.type?.id)?.code ?: ""
        map["&TECH_DOC&"] = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, TECH_DOC)?.stringVal ?: ""
        map["&START&"] = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, START_DATE)?.stringVal ?: ""
        map["&END&"] = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, END_DATE)?.stringVal ?: ""
        map["&PRICE_TYPE&"] = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRICE_TYPE)?.stringVal ?: ""
        map["&PRODUCT&"] = product?.techSpecName ?: ""
        map["&PEO&"] = headEco
        map["&ECO_DIR&"] = directorEco

        val mapDecimal = mutableMapOf<String, BigDecimal>()

        val form2 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, FORM_2)
        val bei: BasicEconomicIndicator? = basicEconomicIndicatorService.read(productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(form2, BASIC_PLAN_ECO_INDICATOR)?.longVal ?: 0)

        val form4 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, FORM_4)
        val purchaseRawMaterials = if (form4 != null) util.total6Form(form4) else BigDecimal.ZERO

        val form6and1 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, FORM_6_1)
        val purchaseComponent = if (form6and1 != null) util.total6Form(form6and1) else BigDecimal.ZERO

        val form6and2 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, FORM_6_2)
        val containerPacking = if (form6and2 != null) util.total6Form(form6and2) else BigDecimal.ZERO

        val form6and3 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, FORM_6_3)
        val costsOfOwnProduct = if (form6and3 != null) util.total6Form(form6and3) else BigDecimal.ZERO

        val materialCosts = purchaseRawMaterials + purchaseComponent + containerPacking + costsOfOwnProduct //0100

        val form9 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, FORM_9)
        val sumForm9 = if (form9 != null) util.total9Form(form9).first else BigDecimal.ZERO
        val laborCosts = sumForm9 + sumForm9 * (bei?.additionalSalary?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred // 0200
        val insurancePremium = sumForm9 + sumForm9 * (bei?.additionalSalary?.toBigDecimal() ?: BigDecimal.ZERO) * (bei?.socialInsurance?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred // 0300
        val generalProductionCosts = sumForm9 * (bei?.productionCosts?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred // 0800
        val generalOperatingCosts = sumForm9 * (bei?.householdExpenses?.toBigDecimal() ?: BigDecimal.ZERO) / oneHundred // 0900

        val form18 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, FORM_18)
        val otherDirestCosts = if (form18 != null) util.total18Form(form18).first else BigDecimal.ZERO // 1100
        val otherDirestCostsResearch = if (form18 != null) util.total18Form(form18).second + otherDirestCosts else BigDecimal.ZERO // 1111

        val form20 = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.id, FORM_20)
        val profit = if (form20 != null) util.total20Form(form20) else BigDecimal.ZERO // 1800

        mapDecimal["&PRICE&"] = materialCosts + laborCosts + insurancePremium + generalProductionCosts + generalOperatingCosts + otherDirestCosts + profit
        mapDecimal["&PRICEWOPACK&"] = materialCosts + laborCosts + insurancePremium + generalProductionCosts + generalOperatingCosts + otherDirestCosts + profit - containerPacking
        mapDecimal["&PRICERES&"] = materialCosts + laborCosts + insurancePremium + generalProductionCosts + generalOperatingCosts + otherDirestCosts + otherDirestCostsResearch + profit

        workbook.forEach { replaceTags(map, it) }
        workbook.forEach { replaceTagsForDecimal(mapDecimal, it) }
        return workbook
    }

    private fun replaceTags(map: Map<String, String>, sheet: Sheet) {
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
    }

    private fun replaceTagsForDecimal(map: Map<String, BigDecimal>, sheet: Sheet) {
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
    }
}