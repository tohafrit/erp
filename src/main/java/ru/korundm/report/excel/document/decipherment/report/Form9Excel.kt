package ru.korundm.report.excel.document.decipherment.report

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.usermodel.PrintSetup.A4_PAPERSIZE
import org.apache.poi.ss.usermodel.Sheet.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType.PAGE_BREAK_PREVIEW
import org.springframework.beans.factory.annotation.Autowired
import ru.korundm.dao.*
import ru.korundm.entity.Product
import ru.korundm.entity.ProductDecipherment
import ru.korundm.entity.WorkType
import ru.korundm.enumeration.ProductDeciphermentAttr.*
import ru.korundm.enumeration.ProductDeciphermentTypeEnum.FORM_9
import ru.korundm.helper.AutowireHelper.autowire
import ru.korundm.report.excel.document.decipherment.DeciphermentExcel
import ru.korundm.report.excel.enumeration.FontTypeOffset
import ru.korundm.report.excel.helper.BaseCellStyleProperty
import ru.korundm.report.excel.helper.BaseFontProperty
import ru.korundm.report.excel.util.ExcelUtil.defineFont
import ru.korundm.report.excel.util.ExcelUtil.defineFontIndex
import ru.korundm.report.excel.util.ExcelUtil.mergeApply
import ru.korundm.util.CommonUtil.cmToInch

class Form9Excel : DeciphermentExcel {

    companion object {
        private const val SHEET_CELL_WIDTH = 11
    }

    private class WorkData(
        var name: String = "",
        var labIntensityFact: Double = .0,
        var labIntensityPlan: Double = .0,
        var priceFact: Double = .0,
        var pricePlan: Double = .0
    )

    @Autowired
    private lateinit var deciphermentService: ProductDeciphermentService
    @Autowired
    private lateinit var labourIntensityOperationService: ProductLabourIntensityOperationService
    @Autowired
    private lateinit var productWorkCostService: ProductWorkCostService
    @Autowired
    private lateinit var productDeciphermentAttrValService: ProductDeciphermentAttrValService
    @Autowired
    private lateinit var baseService: BaseService

    private val workbook: Workbook = XSSFWorkbook()
    private lateinit var decipherment: ProductDecipherment
    private lateinit var product: Product

    private var totalRowNum = 0
    private val formatDouble = workbook.createDataFormat().getFormat("#,##0.00")
    private lateinit var planPeriodYear: String
    private lateinit var reportPeriodYear: String
    private lateinit var headEco: String
    private lateinit var chiefTech: String

    override fun generate(deciphermentId: Long): Workbook {
        autowire(this)
        decipherment = deciphermentService.read(deciphermentId) ?: return workbook
        product = decipherment.period?.product ?: return workbook

        val workDataList = mutableListOf<WorkData>()
        val workDataWoPackList = mutableListOf<WorkData>()
        baseService.exec {
            // Плановый и отчетные года
            planPeriodYear = decipherment.period?.pricePeriod?.startDate?.year?.toString() ?: "    "
            reportPeriodYear = decipherment.period?.prevPeriod?.pricePeriod?.startDate?.year?.toString() ?: "    "
            // Подписанты
            headEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user?.userShortName ?: ""
            chiefTech = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CHIEF_TECHNOLOGIST)?.user?.userShortName ?: ""
            // Данные текущей расшифровки
            val justificationId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_WORK_COST_JUSTIFICATION)?.productWorkCostJustification?.id
            val labourIntensityId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_LABOUR_INTENSITY)?.productLabourIntensity?.id
            val workCostList = productWorkCostService.getAllByJustificationId(justificationId)
            val labourIntensityList = labourIntensityOperationService.getAllByLabourIntensityIdAndProductId(labourIntensityId, product.id)
            // Данные предыдущей расшифровки для расчета
            val reportDecipherment = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.prevPeriod?.id, FORM_9)
            val reportJustificationId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(reportDecipherment, PRODUCT_WORK_COST_JUSTIFICATION)?.productWorkCostJustification?.id
            val reportLabourIntensityId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(reportDecipherment, PRODUCT_LABOUR_INTENSITY)?.productLabourIntensity?.id
            val reportWorkCostList = productWorkCostService.getAllByJustificationId(reportJustificationId)
            val reportLabourIntensityList = labourIntensityOperationService.getAllByLabourIntensityIdAndProductId(reportLabourIntensityId, product.id)

            // Собираем список работ
            val workTypeList = mutableListOf<WorkType>()
            workTypeList.addAll(labourIntensityList.filterNot { workTypeList.contains(it.operation) }.mapNotNull { it.operation })
            workTypeList.addAll(reportLabourIntensityList.filterNot { workTypeList.contains(it.operation) }.mapNotNull { it.operation })

            workTypeList.forEach { workType: WorkType ->
                val workCost = workCostList.find { workType.id == it.workType?.id }
                val reportWorkCost = reportWorkCostList.find { workType.id == it.workType?.id }
                val labourIntensity = labourIntensityList.find { workType.id == it.operation?.id }
                val reportLabourIntensity = reportLabourIntensityList.find { workType.id == it.operation?.id }
                val workData = WorkData(workType.name)
                workData.labIntensityFact = reportLabourIntensity?.value ?: .0
                workData.labIntensityPlan = labourIntensity?.value ?: .0
                workData.priceFact = reportWorkCost?.cost ?: .0
                workData.pricePlan = workCost?.cost ?: .0
                workDataList.add(workData)
                if (!workType.separateDelivery) workDataWoPackList.add(workData)
            }
        }

        fillSheet(workbook.createSheet("Ф9 ЗП"), workDataWoPackList, false)
        fillSheet(workbook.createSheet("Ф9 ЗП У"), workDataList, true)

        return workbook
    }

    private fun fillSheet(sheet: Sheet, workDataList: List<WorkData>, isPack: Boolean) {
        run {
            sheet.setZoom(80) // масштабирование

            // Настройки печати
            val printSetup: PrintSetup = sheet.printSetup
            // Разметка страницы -> Настраиваемые поля (параметры страницы)
            // Страница
            printSetup.scale = 71
            printSetup.landscape = true // альбомная ориентация
            printSetup.paperSize = A4_PAPERSIZE // А4
            // Поля
            sheet.setMargin(TopMargin, cmToInch(0.5))
            sheet.setMargin(BottomMargin, cmToInch(0.5))
            sheet.setMargin(LeftMargin, cmToInch(0.5))
            sheet.setMargin(RightMargin, cmToInch(0.3))
            sheet.setMargin(HeaderMargin, cmToInch(.0))
            sheet.setMargin(FooterMargin, cmToInch(.0))
            sheet.horizontallyCenter = true // выравнивание по горизонтали
            // Колонтитулы
            val sheetHeader: Header = sheet.header
            if (sheetHeader is XSSFHeaderFooter) sheetHeader.headerFooter.alignWithMargins = false // Опция выравнивания относительно полей страницы
            // Режим просмотра книг - страничный режим
            if (sheet is XSSFSheet) sheet.ctWorksheet.sheetViews.getSheetViewArray(0).view = PAGE_BREAK_PREVIEW
            sheet.autobreaks = true // автоматический разрыв по страницей при печати

            // Ширина столбцов
            sheet.setColumnWidth(0, 2000)
            sheet.setColumnWidth(1, 8000)
            sheet.setColumnWidth(2, 2500)
            sheet.setColumnWidth(3, 2500)
            sheet.setColumnWidth(4, 3300)
            sheet.setColumnWidth(5, 2500)
            sheet.setColumnWidth(6, 2500)
            sheet.setColumnWidth(7, 3350)
            sheet.setColumnWidth(8, 3300)
            sheet.setColumnWidth(9, 2500)
            sheet.setColumnWidth(10, 2500)
            sheet.setColumnWidth(SHEET_CELL_WIDTH, 3300)

            // Название формы
            run {
                val cell: Cell = sheet.createRow(0).createCell(SHEET_CELL_WIDTH)
                cell.setCellValue("Форма № 9")
                val fontProperty = BaseFontProperty()
                fontProperty.setFontHeightInPoints(9)
                val styleProperty = BaseCellStyleProperty()
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                cell.mergeApply(styleProperty)
            }

            run {
                val fontProperty = BaseFontProperty()
                fontProperty.setFontHeightInPoints(12)
                fontProperty.bold = true

                val styleProperty = BaseCellStyleProperty()
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)

                for (rowNum in 2..4) {
                    val row: Row = sheet.createRow(rowNum)
                    val cell: Cell = row.createCell(0)
                    sheet.addMergedRegion(CellRangeAddress(row.rowNum, row.rowNum, cell.columnIndex, SHEET_CELL_WIDTH))
                    cell.setCellValue(when (rowNum) {
                        2 -> "РАСШИФРОВКА"
                        3 -> "затрат на основную заработную плату"
                        4 -> "на изготовление и поставку изделия ${product.techSpecName} ${product.decimalNumber} ${if (isPack) "с упаковкой" else "без упаковки"}"
                        else -> ""
                    })
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                for (rowNum in 6..10) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(10)
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    styleProperty.setBorderTop(BorderStyle.THIN)
                    styleProperty.setBorderRight(BorderStyle.THIN)
                    styleProperty.setBorderBottom(BorderStyle.THIN)
                    styleProperty.setBorderLeft(BorderStyle.THIN)
                    styleProperty.setWrapText(true)

                    val row: Row = sheet.createRow(rowNum)
                    if (rowNum == 8) row.height = 1300
                    for (cellNum in 0..SHEET_CELL_WIDTH) {
                        if (rowNum == 8) {
                            if (cellNum in 2..6 || cellNum in 2..SHEET_CELL_WIDTH) styleProperty.setBorderBottom(BorderStyle.NONE)
                        } else if (rowNum == 9) {
                            if (cellNum in 2..6 || cellNum in 2..SHEET_CELL_WIDTH) styleProperty.setBorderTop(BorderStyle.NONE)
                        } else {
                            styleProperty.setBorderTop(BorderStyle.THIN)
                            styleProperty.setBorderBottom(BorderStyle.THIN)
                        }
                        row.createCell(cellNum).mergeApply(styleProperty)
                    }
                }

                // Слияние ячеек
                for (cellNum in 0..1) sheet.addMergedRegion(CellRangeAddress(6, 10, cellNum, cellNum))
                sheet.addMergedRegion(CellRangeAddress(6, 6, 2, SHEET_CELL_WIDTH))
                sheet.addMergedRegion(CellRangeAddress(7, 7, 2, 4))
                sheet.addMergedRegion(CellRangeAddress(7, 7, 5, 8))
                sheet.addMergedRegion(CellRangeAddress(7, 7, 9, SHEET_CELL_WIDTH))
                for (rowNum in 8..9) {
                    sheet.addMergedRegion(CellRangeAddress(rowNum, rowNum, 2, 3))
                    sheet.addMergedRegion(CellRangeAddress(rowNum, rowNum, 5, 6))
                    sheet.addMergedRegion(CellRangeAddress(rowNum, rowNum, 9, 10))
                }
                sheet.addMergedRegion(CellRangeAddress(8, 10, 7, 7))

                val headerValues = arrayOf(
                    arrayOf<Any>(6, 0, "№ п/п"),
                    arrayOf<Any>(6, 1, "Вид работ"),
                    arrayOf<Any>(6, 2, "Трудоемкость и заработная плата, учтенные при расчете цены"),
                    arrayOf<Any>(7, 2, "трудоемкость (н/час, чел/час)"),
                    arrayOf<Any>(7, 5, "стоимость н/час, чел/час, руб. коп."),
                    arrayOf<Any>(7, 9, "основная заработная плата (руб.)"),
                    arrayOf<Any>(8, 2, "Отчетный период/период, предшествующий планируемому"),
                    arrayOf<Any>(8, 4, "Планируемый период"),
                    arrayOf<Any>(8, 5, "Отчетный период/период, предшествующий планируемому"),
                    arrayOf<Any>(8, 7, "Применяемый индекс цен"),
                    arrayOf<Any>(8, 8, "Планируемый период"),
                    arrayOf<Any>(8, 9, "Отчетный период/период, предшествующий планируемому"),
                    arrayOf<Any>(8, SHEET_CELL_WIDTH, "Планируемый период"),
                    arrayOf<Any>(9, 2, "(год $reportPeriodYear)"),
                    arrayOf<Any>(9, 4, "(год $planPeriodYear)"),
                    arrayOf<Any>(9, 5, "(год $reportPeriodYear)"),
                    arrayOf<Any>(9, 8, "(год $planPeriodYear)"),
                    arrayOf<Any>(9, 9, "(год $reportPeriodYear)"),
                    arrayOf<Any>(9, SHEET_CELL_WIDTH, "(год $planPeriodYear)"),
                    arrayOf<Any>(10, 2, "план"),
                    arrayOf<Any>(10, 3, "факт"),
                    arrayOf<Any>(10, 4, "план"),
                    arrayOf<Any>(10, 5, "план"),
                    arrayOf<Any>(10, 6, "факт"),
                    arrayOf<Any>(10, 8, "план"),
                    arrayOf<Any>(10, 9, "план"),
                    arrayOf<Any>(10, 10, "факт"),
                    arrayOf<Any>(10, SHEET_CELL_WIDTH, "план")
                )

                for (headerValue in headerValues) {
                    sheet.getRow(headerValue[0] as Int).getCell(headerValue[1] as Int).setCellValue(headerValue[2] as String)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(10)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                styleProperty.setBorderTop(BorderStyle.THIN)
                styleProperty.setBorderRight(BorderStyle.THIN)
                styleProperty.setBorderBottom(BorderStyle.THIN)
                styleProperty.setBorderLeft(BorderStyle.THIN)

                val row: Row = sheet.createRow(11)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    cell.setCellValue((cellNum + 1).toDouble())
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(10)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                styleProperty.setBorderTop(BorderStyle.THIN)
                styleProperty.setBorderRight(BorderStyle.THIN)
                styleProperty.setBorderBottom(BorderStyle.THIN)
                styleProperty.setBorderLeft(BorderStyle.THIN)

                val row: Row = sheet.createRow(12)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 0) cell.setCellValue("1.")
                    else if (cellNum >= 2) {
                        fontProperty.setFontHeightInPoints(9)
                        styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                        cell.setCellValue("Х")
                    }
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(10)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                styleProperty.setBorderTop(BorderStyle.THIN)
                styleProperty.setBorderRight(BorderStyle.THIN)
                styleProperty.setBorderBottom(BorderStyle.THIN)
                styleProperty.setBorderLeft(BorderStyle.THIN)
                styleProperty.setIndention(1)

                val row: Row = sheet.createRow(13)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 1) cell.setCellValue("Итого сдельно")
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(10)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                styleProperty.setBorderTop(BorderStyle.THIN)
                styleProperty.setBorderRight(BorderStyle.THIN)
                styleProperty.setBorderBottom(BorderStyle.THIN)
                styleProperty.setBorderLeft(BorderStyle.THIN)

                val row: Row = sheet.createRow(14)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 0) cell.setCellValue("2.")
                    cell.mergeApply(styleProperty)
                }
                sheet.setAutoFilter(CellRangeAddress(14, 14, 0, SHEET_CELL_WIDTH))
            }

            var rowCount = 15
            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                workDataList.forEach { workData ->
                    val row: Row = sheet.createRow(rowCount)
                    // Для фильтра
                    /*if (row is XSSFRow && workData.salaryPlan == .0) {
                        row.ctRow.hidden = true
                    }*/
                    for (cellNum in 0..SHEET_CELL_WIDTH) {
                        fontProperty.reset()
                        styleProperty.reset()
                        fontProperty.setFontHeightInPoints(8)
                        styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                        styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                        styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                        styleProperty.setBorderTop(BorderStyle.DOTTED)
                        styleProperty.setBorderRight(BorderStyle.THIN)
                        styleProperty.setBorderBottom(BorderStyle.DOTTED)
                        styleProperty.setBorderLeft(BorderStyle.THIN)

                        val cell: Cell = row.createCell(cellNum)
                        if (cellNum == 1) {
                            styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                            styleProperty.setIndention(1)
                            styleProperty.setWrapText(true)
                            cell.setCellValue(workData.name)
                        } else if (cellNum == 2 || cellNum == 5 || cellNum == 7 || cellNum == 9) {
                            cell.setCellValue("Х")
                        } else if (cellNum == 3 || cellNum == 4 || cellNum == 6 || cellNum == 8 || cellNum == 10 || cellNum == 11) {
                            when (cellNum) {
                                3 -> cell.setCellValue(workData.labIntensityFact)
                                4 -> cell.setCellValue(workData.labIntensityPlan)
                                6 -> cell.setCellValue(workData.priceFact)
                                8 -> cell.setCellValue(workData.pricePlan)
                                10 -> cell.cellFormula = "ROUND(D${rowCount + 1}*G${rowCount + 1},2)"
                                11 -> cell.cellFormula = "ROUND(E${rowCount + 1}*I${rowCount + 1},2)"
                            }
                            styleProperty.setDataFormat(formatDouble)
                        }
                        cell.mergeApply(styleProperty)
                    }
                    rowCount++
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(rowCount++)
                totalRowNum = rowCount
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(8)
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    styleProperty.setBorderTop(BorderStyle.THIN)
                    styleProperty.setBorderRight(BorderStyle.THIN)
                    styleProperty.setBorderBottom(BorderStyle.THIN)
                    styleProperty.setBorderLeft(BorderStyle.THIN)

                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 1) {
                        fontProperty.setFontHeightInPoints(10)
                        styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                        styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                        styleProperty.setIndention(1)
                        cell.setCellValue("Итого повременно")
                    } else if (cellNum == 3 || cellNum == 4 || cellNum == 6 || cellNum == 8 || cellNum == 10 || cellNum == 11) {
                         val rowNum = rowCount - 1
                         when (cellNum) {
                            3 -> cell.cellFormula = if (rowNum == 15) "0" else "SUM(D16:D$rowNum)"
                            4 -> cell.cellFormula = if (rowNum == 15) "0" else "SUM(E16:E$rowNum)"
                            6 -> cell.cellFormula = "IFERROR(ROUND(K$rowCount/D$rowCount,2),0)"
                            8 -> cell.cellFormula = "IFERROR(ROUND(L$rowCount/E$rowCount,2),0)"
                            10 -> cell.cellFormula = if (rowNum == 15) "0" else "SUM(K16:K$rowNum)"
                            11 -> cell.cellFormula = if (rowNum == 15) "0" else "SUM(L16:L$rowNum)"
                            else -> Unit
                        }
                        styleProperty.setDataFormat(formatDouble)
                    }
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                for (num in 3..6) {
                    val row: Row = sheet.createRow(rowCount++)
                    for (cellNum in 0..SHEET_CELL_WIDTH) {
                        fontProperty.reset()
                        styleProperty.reset()
                        fontProperty.setFontHeightInPoints(10)
                        styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                        styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                        styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                        styleProperty.setBorderTop(BorderStyle.THIN)
                        styleProperty.setBorderRight(BorderStyle.THIN)
                        styleProperty.setBorderBottom(BorderStyle.THIN)
                        styleProperty.setBorderLeft(BorderStyle.THIN)

                        val cell: Cell = row.createCell(cellNum)
                        if (cellNum == 0) {
                            styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                            styleProperty.setIndention(1)
                            cell.setCellValue("$num.")
                        } else if (cellNum == 1) {
                            fontProperty.setFontHeightInPoints(9)
                            styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                            styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                            styleProperty.setIndention(1)
                            styleProperty.setWrapText(true)
                            cell.setCellValue(when (num) {
                                3 -> "Начисления стимулирующего характера"
                                4 -> "Начисления, обусловленные районным регулированием"
                                5 -> "Начисления за работу в условиях, отклоняющихся от нормальных"
                                6 -> "Специальные надбавки при выполнении работ, оказании услуг в командировках на территорию иностранных государств, необходимость которых определена требованиями государственного заказчика (заказчика)"
                                else -> ""
                            })
                        } else cell.setCellValue("Х")
                        cell.mergeApply(styleProperty)
                    }
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(rowCount++)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(8)
                    fontProperty.bold = true
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    styleProperty.setBorderTop(BorderStyle.THIN)
                    styleProperty.setBorderRight(BorderStyle.THIN)
                    styleProperty.setBorderBottom(BorderStyle.THIN)
                    styleProperty.setBorderLeft(BorderStyle.THIN)

                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 1) {
                        fontProperty.setFontHeightInPoints(10)
                        styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                        styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                        styleProperty.setIndention(1)
                        cell.setCellValue("Итого")
                    } else if (cellNum == 3 || cellNum == 4 || cellNum == 6 || cellNum == 8 || cellNum == 10 || cellNum == 11) {
                        val col = when (cellNum) {
                            3 -> "D"
                            4 -> "E"
                            6 -> "G"
                            8 -> "I"
                            10 -> "K"
                            11 -> "L"
                            else -> ""
                        }
                        styleProperty.setDataFormat(formatDouble)
                        cell.cellFormula = "$col$totalRowNum"
                    }
                    cell.mergeApply(styleProperty)
                }
            }

            sheet.createRow(rowCount++).height = 1
            sheet.createRow(rowCount++)

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(9)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                styleProperty.setWrapText(true)

                val textFontProperty = BaseFontProperty()
                textFontProperty.setFontHeightInPoints(10)
                textFontProperty.setTypeOffset(FontTypeOffset.SUPERSCRIPT)
                for (num in 1..7) {
                    sheet.addMergedRegion(CellRangeAddress(rowCount, rowCount, 0, SHEET_CELL_WIDTH))
                    val row: Row = sheet.createRow(rowCount++)
                    row.height = -1
                    for (cellNum in 0..SHEET_CELL_WIDTH) {
                        val text = XSSFRichTextString(when (num) {
                            1 -> {
                                row.height = 400
                                "$num Указывается используемая единица измерения трудоемкости: нормо-час / человеко-час / человеко-день / человеко-месяц."
                            }
                            2 -> {
                                row.height = 1000
                                "$num В случае если продукция ранее не поставлялась, данные за отчетный период / период, предшествующий планируемому, представляются по имеющимся сведениям о затратах в отчетном периоде / периоде, предшествующем планируемому. В пояснительной записке, прилагаемой к соответствующему предложению о цене, организацией указывается, что продукция ранее не поставлялась."
                            }
                            3 -> {
                                row.height = 3100
                                "$num При проведении процедуры перевода в фиксированную цену других видов цен на продукцию организацией с учетом установленных в государственном контракте (контракте) условий уточнения и порядка перевода соответствующего вида цены на продукцию в фиксированную цену в графах 3-4, 6-7, 10-11 представляются затраты по состоянию на конкретную дату, планируемые затраты до окончания поставки продукции в рамках выполнения государственного контракта (контракта) (или завершения отдельных этапов поставки продукции, если государственным контрактом (контрактом) предусмотрены такие этапы) представляются в графах 5, 9, 12." +
                                        " В случае если технологический цикл производства продукции составляет более 1 года либо год начала производства продукции не соответствует году окончания ее производства и поставки (или завершения отдельных этапов поставки продукции, если государственным контрактом (контрактом) предусмотрены такие этапы), то организация представляет сведения о затратах по состоянию на конкретную дату в соответствии с графами 3-4, 6-7, 10-11 по годам, дополнив форму после граф 4, 7, 11 графами, аналогичными по содержанию графам 3-4, 6-7, 10-11 соответственно (с последующей корректировкой нумерации граф), на каждый год производства продукции для указания информации."
                            }
                            4 -> {
                                row.height = 1300
                                "$num При формировании нормо-часа / человеко-часа / человеко-дня / человеко-месяца по цехам дополнительно справочно представляются сведения по цехам по настоящей форме или обоснование средней стоимости нормо-часа / человеко-часа / человеко-дня / человеко-месяца за отчетный период и на плановый период (по годам производства) с учетом распределения трудоемкости по цехам в пояснительной записке, прилагаемой к соответствующему предложению о цене."
                            }
                            5 -> {
                                row.height = 700
                                "$num В пояснительной записке, прилагаемой к соответствующему предложению о цене, организацией представляется обоснование применяемого индекса цен (в случае использования)."
                            }
                            6 -> {
                                row.height = 400
                                "$num Показатели по строкам 1, 2 в графах 3-12 приводятся без учета начислений, надбавок, указанных в строках 3-6."
                            }
                            7 -> {
                                row.height = 700
                                "$num Указываются виды начислений стимулирующего характера, порядок и размеры которых определяются коллективным договором или локальным нормативным актом. Организацией представляется соответствующая выписка из документа, в котором определены указанные начисления."
                            }
                            else -> ""
                        })
                        text.applyFont(0, 1, workbook.defineFont(textFontProperty))
                        val cell: Cell = row.createCell(cellNum)
                        if (cellNum == 0) cell.setCellValue(text)
                        cell.mergeApply(styleProperty)
                    }
                }
                sheet.groupRow(rowCount - 9, rowCount - 1)
                sheet.setRowGroupCollapsed(rowCount - 9, true)
            }

            sheet.createRow(rowCount++)

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(9)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                styleProperty.setVerticalAlignment(VerticalAlignment.TOP)
                styleProperty.setIndention(1)
                styleProperty.setWrapText(true)

                val row: Row = sheet.createRow(rowCount++)
                row.height = 400
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 1) cell.setCellValue("Начальник")
                    else if (cellNum == 7) cell.setCellValue("Главный технолог")
                    cell.mergeApply(styleProperty)
                }
                sheet.addMergedRegion(CellRangeAddress(rowCount - 1, rowCount - 1, 1, 2))
                sheet.addMergedRegion(CellRangeAddress(rowCount - 1, rowCount - 1, 7, SHEET_CELL_WIDTH))
            }

            sheet.createRow(rowCount++)

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(rowCount++)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(9)
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                    styleProperty.setVerticalAlignment(VerticalAlignment.BOTTOM)
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 1 || cellNum == 3 || cellNum in 7..10) {
                        styleProperty.setBorderBottom(BorderStyle.THIN)
                        if (cellNum == 9) cell.setCellValue(chiefTech)
                    } else if (cellNum == 2) {
                        styleProperty.setBorderBottom(BorderStyle.THIN)
                        cell.setCellValue(headEco)
                    }
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(rowCount++)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(9)
                    fontProperty.italic = true
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 1 || cellNum == 7) {
                        styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                        cell.setCellValue("(подпись)")
                    } else if (cellNum == 2 || cellNum == 9) {
                        styleProperty.setHorizontalAlignment(HorizontalAlignment.RIGHT)
                        cell.setCellValue("(Ф.И.О.)")
                    }
                    cell.mergeApply(styleProperty)
                }
                sheet.addMergedRegion(CellRangeAddress(rowCount - 1, rowCount - 1, 7, 8))
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(9)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                styleProperty.setVerticalAlignment(VerticalAlignment.BOTTOM)

                val row: Row = sheet.createRow(rowCount++)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 1 || cellNum == 7) cell.setCellValue("\"___\"________________ $planPeriodYear г.")
                    cell.mergeApply(styleProperty)
                }
                sheet.addMergedRegion(CellRangeAddress(rowCount - 1, rowCount - 1, 7, 9))
            }

            // Область печати
            workbook.setPrintArea(workbook.getSheetIndex(sheet.sheetName), 0, SHEET_CELL_WIDTH, 0, rowCount - 1)
            // Активация фильтра пустых строк
            /*if (sheet is XSSFSheet) {
                val column = sheet.ctWorksheet.autoFilter.addNewFilterColumn()
                column.colId = SHEET_CELL_WIDTH.toLong()
                val filter = column.addNewCustomFilters().addNewCustomFilter()
                filter.operator = STFilterOperator.Enum.forInt(4)
                filter.`val` = " "
            }*/
        }
    }
}