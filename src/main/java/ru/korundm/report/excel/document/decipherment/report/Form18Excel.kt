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
import ru.korundm.enumeration.ProductDeciphermentAttr.*
import ru.korundm.enumeration.ProductDeciphermentTypeEnum.FORM_18
import ru.korundm.helper.AutowireHelper.autowire
import ru.korundm.report.excel.document.decipherment.DeciphermentExcel
import ru.korundm.report.excel.enumeration.FontTypeOffset
import ru.korundm.report.excel.helper.BaseCellStyleProperty
import ru.korundm.report.excel.helper.BaseFontProperty
import ru.korundm.report.excel.util.ExcelUtil.defineFont
import ru.korundm.report.excel.util.ExcelUtil.defineFontIndex
import ru.korundm.report.excel.util.ExcelUtil.mergeApply
import ru.korundm.util.CommonUtil.cmToInch

class Form18Excel : DeciphermentExcel {

    companion object {
        private const val SHEET_CELL_WIDTH = 6
    }

    @Autowired
    private lateinit var deciphermentService: ProductDeciphermentService
    @Autowired
    private lateinit var productSpecReviewPriceService: ProductSpecReviewPriceService
    @Autowired
    private lateinit var productSpecResearchPriceService: ProductSpecResearchPriceService
    @Autowired
    private lateinit var productDeciphermentAttrValService: ProductDeciphermentAttrValService
    @Autowired
    private lateinit var baseService: BaseService

    private val workbook: Workbook = XSSFWorkbook()
    private lateinit var decipherment: ProductDecipherment
    private lateinit var product: Product

    private lateinit var planPeriodYear: String
    private lateinit var reportPeriodYear: String
    private var reportReviewPrice = .0
    private var reportResearchPrice = .0
    private var planReviewName = ""
    private var planReviewPrice = .0
    private var planResearchName = ""
    private var planResearchPrice = .0
    private lateinit var headEco: String
    private lateinit var headProd: String

    override fun generate(deciphermentId: Long): Workbook {
        autowire(this)
        decipherment = deciphermentService.read(deciphermentId) ?: return workbook
        product = decipherment.period?.product ?: return workbook

        val formatDouble = workbook.createDataFormat().getFormat("#,##0.00")

        baseService.exec {
            val groupId = product.classificationGroup?.id
            // Плановый и отчетные года
            planPeriodYear = decipherment.period?.pricePeriod?.startDate?.year?.toString() ?: "    "
            reportPeriodYear = decipherment.period?.prevPeriod?.pricePeriod?.startDate?.year?.toString() ?: "    "
            // Подписанты
            headEco = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT)?.user?.userShortName ?: ""
            headProd = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PRODUCTION)?.user?.userShortName ?: ""
            // Данные текущей расшифровки
            val reviewJustificationId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_SPEC_REVIEW_JUSTIFICATION)?.productSpecReviewJustification?.id
            val researchJustificationId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, PRODUCT_SPEC_RESEARCH_JUSTIFICATION)?.productSpecResearchJustification?.id
            productSpecReviewPriceService.getByJustificationIdAndGroupId(reviewJustificationId, groupId)?.let {
                planReviewName = it.justification?.name ?: ""
                planReviewPrice = it.price
            }
            productSpecResearchPriceService.getByJustificationIdAndGroupId(researchJustificationId, groupId)?.let {
                planResearchName = it.justification?.name ?: ""
                planResearchPrice = it.price
            }
            // Данные предыдущей расшифровки для расчета
            val reportDecipherment = deciphermentService.getFirstByPeriodIdAndType(decipherment.period?.prevPeriod?.id, FORM_18)
            val reportReviewJustificationId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(reportDecipherment, PRODUCT_SPEC_REVIEW_JUSTIFICATION)?.productSpecReviewJustification?.id
            val reportResearchJustificationId = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(reportDecipherment, PRODUCT_SPEC_RESEARCH_JUSTIFICATION)?.productSpecResearchJustification?.id
            reportReviewPrice = productSpecReviewPriceService.getByJustificationIdAndGroupId(reportReviewJustificationId, groupId)?.price ?: .0
            reportResearchPrice = productSpecResearchPriceService.getByJustificationIdAndGroupId(reportResearchJustificationId, groupId)?.price ?: .0
        }

        val sheet: Sheet = workbook.createSheet("Ф18 (18д) прочие")

        run {
            sheet.setZoom(70) // масштабирование

            // Настройки печати
            val printSetup: PrintSetup = sheet.printSetup
            // Разметка страницы -> Настраиваемые поля (параметры страницы)
            // Страница
            sheet.fitToPage = true // Разместить не более чем на
            printSetup.fitWidth = 1
            printSetup.fitHeight = 0
            printSetup.landscape = true // альбомная ориентация
            printSetup.paperSize = A4_PAPERSIZE // А4
            // Поля
            sheet.setMargin(TopMargin, cmToInch(1.9))
            sheet.setMargin(BottomMargin, cmToInch(1.9))
            sheet.setMargin(LeftMargin, cmToInch(0.6))
            sheet.setMargin(RightMargin, cmToInch(0.6))
            sheet.setMargin(HeaderMargin, cmToInch(0.8))
            sheet.setMargin(FooterMargin, cmToInch(0.8))
            sheet.horizontallyCenter = true // выравнивание по горизонтали
            // Колонтитулы
            val sheetHeader: Header = sheet.header
            if (sheetHeader is XSSFHeaderFooter) sheetHeader.headerFooter.alignWithMargins = false // Опция выравнивания относительно полей страницы
            // Режим просмотра книг - страничный режим
            if (sheet is XSSFSheet) sheet.ctWorksheet.sheetViews.getSheetViewArray(0).view = PAGE_BREAK_PREVIEW
            sheet.autobreaks = true // автоматический разрыв по страницей при печати

            // Ширина столбцов
            sheet.setColumnWidth(0, 1800)
            sheet.setColumnWidth(1, 10000)
            sheet.setColumnWidth(2, 5800)
            sheet.setColumnWidth(3, 5800)
            sheet.setColumnWidth(4, 3500)
            sheet.setColumnWidth(5, 7500)
            sheet.setColumnWidth(SHEET_CELL_WIDTH, 8500)

            // Название формы
            run {
                val cell: Cell = sheet.createRow(0).createCell(SHEET_CELL_WIDTH)
                cell.setCellValue("Форма № 18 (18д)")
                val fontProperty = BaseFontProperty()
                fontProperty.setFontHeightInPoints(10)
                val styleProperty = BaseCellStyleProperty()
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                cell.mergeApply(styleProperty)
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                for (rowNum in 2..5) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(12)
                    fontProperty.bold = true
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    val row: Row = sheet.createRow(rowNum)
                    val cell: Cell = row.createCell(0)
                    sheet.addMergedRegion(CellRangeAddress(row.rowNum, row.rowNum, cell.columnIndex, SHEET_CELL_WIDTH))
                    cell.setCellValue(when (rowNum) {
                        2 -> "РАСШИФРОВКА"
                        3 -> "прочих прямых затрат"
                        4 -> "${product.techSpecName} ${product.decimalNumber}"
                        5 -> {
                            fontProperty.setFontHeightInPoints(10)
                            fontProperty.italic = true
                            fontProperty.bold = false
                            "(наименование, шифр товара, работы, услуги)"
                        }
                        else -> ""
                    })
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                for (rowNum in 7..8) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(11)
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    styleProperty.setBorderTop(BorderStyle.THIN)
                    styleProperty.setBorderRight(BorderStyle.THIN)
                    styleProperty.setBorderBottom(BorderStyle.THIN)
                    styleProperty.setBorderLeft(BorderStyle.THIN)
                    styleProperty.setWrapText(true)
                    val row: Row = sheet.createRow(rowNum)
                    if (rowNum == 7) row.height = 1000
                    for (cellNum in 0..SHEET_CELL_WIDTH) {
                        row.createCell(cellNum).mergeApply(styleProperty)
                    }
                }

                // Слияние ячеек
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    if (cellNum in 0..1 || cellNum in 4..SHEET_CELL_WIDTH) sheet.addMergedRegion(CellRangeAddress(7, 8, cellNum, cellNum))
                }
                sheet.addMergedRegion(CellRangeAddress(7, 7, 2, 3))

                val headerValues = arrayOf(
                    arrayOf<Any>(7, 0, "№ статей"),
                    arrayOf<Any>(7, 1, "Наименование статей затрат2"),
                    arrayOf<Any>(7, 2, "Затраты отчетного периода / периода, предшествующего планируемому (год $reportPeriodYear) (руб.)"),
                    arrayOf<Any>(7, 4, "Применяемый индекс цен"),
                    arrayOf<Any>(7, 5, "Затраты планируемого периода (год $planPeriodYear) (руб.)"),
                    arrayOf<Any>(7, SHEET_CELL_WIDTH, "Основания для включения затрат"),
                    arrayOf<Any>(8, 2, "план"),
                    arrayOf<Any>(8, 3, "факт")
                )

                for (headerValue in headerValues) {
                    val cell: Cell = sheet.getRow(headerValue[0] as Int).getCell(headerValue[1] as Int)
                    if (cell.rowIndex == 7 && cell.columnIndex == 1) {
                        val textFontProperty = BaseFontProperty()
                        textFontProperty.setFontHeightInPoints(10)
                        textFontProperty.setTypeOffset(FontTypeOffset.SUPERSCRIPT)
                        val text = XSSFRichTextString(headerValue[2] as String)
                        text.applyFont(text.length() - 1, text.length(), workbook.defineFont(textFontProperty))
                        cell.setCellValue(text)
                    } else {
                        cell.setCellValue(headerValue[2] as String)
                    }
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(11)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                styleProperty.setBorderTop(BorderStyle.THIN)
                styleProperty.setBorderRight(BorderStyle.THIN)
                styleProperty.setBorderBottom(BorderStyle.THIN)
                styleProperty.setBorderLeft(BorderStyle.THIN)

                val row: Row = sheet.createRow(9)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    when (cellNum) {
                        0 -> cell.setCellValue(1.0)
                        1 -> cell.setCellValue(2.0)
                        2 -> cell.setCellValue("3.1")
                        3 -> cell.setCellValue("3.2")
                        4 -> cell.setCellValue(4.0)
                        5 -> cell.setCellValue(5.0)
                        6 -> cell.setCellValue(6.0)
                        else -> Unit
                    }
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(10)
                row.height = 500
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(11)
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    styleProperty.setBorderTop(BorderStyle.THIN)
                    styleProperty.setBorderRight(BorderStyle.THIN)
                    styleProperty.setBorderBottom(BorderStyle.THIN)
                    styleProperty.setBorderLeft(BorderStyle.THIN)
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 0) cell.setCellValue("1.")
                    else if (cellNum == 1) {
                        styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                        cell.setCellValue("Проведение специальных проверок")
                    }
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(11)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(11)
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    styleProperty.setBorderTop(BorderStyle.THIN)
                    styleProperty.setBorderRight(BorderStyle.THIN)
                    styleProperty.setBorderBottom(BorderStyle.THIN)
                    styleProperty.setBorderLeft(BorderStyle.THIN)
                    styleProperty.setWrapText(true)
                    val cell: Cell = row.createCell(cellNum)
                    when (cellNum) {
                        0 -> cell.setCellValue("1.1")
                        1 -> {
                            styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                            cell.setCellValue("${product.techSpecName} ${product.decimalNumber}")
                        }
                        2, 4 -> cell.setCellValue("Х")
                        3 -> {
                            styleProperty.setDataFormat(formatDouble)
                            cell.setCellValue(reportReviewPrice)
                        }
                        5 -> {
                            styleProperty.setDataFormat(formatDouble)
                            cell.setCellValue(planReviewPrice)
                        }
                        6 -> cell.setCellValue(planReviewName)
                        else -> Unit
                    }
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(13)
                row.height = 500
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(11)
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    styleProperty.setBorderTop(BorderStyle.THIN)
                    styleProperty.setBorderRight(BorderStyle.THIN)
                    styleProperty.setBorderBottom(BorderStyle.THIN)
                    styleProperty.setBorderLeft(BorderStyle.THIN)
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 0) cell.setCellValue(2.0)
                    else if (cellNum == 1) {
                        styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                        cell.setCellValue("Проведение специальных исследований")
                    }
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(14)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(11)
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    styleProperty.setBorderTop(BorderStyle.THIN)
                    styleProperty.setBorderRight(BorderStyle.THIN)
                    styleProperty.setBorderBottom(BorderStyle.THIN)
                    styleProperty.setBorderLeft(BorderStyle.THIN)
                    styleProperty.setWrapText(true)
                    val cell: Cell = row.createCell(cellNum)
                    when (cellNum) {
                        0 -> cell.setCellValue("2.1")
                        1 -> {
                            styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                            cell.setCellValue("${product.techSpecName} ${product.decimalNumber}")
                        }
                        2, 4 -> cell.setCellValue("Х")
                        3 -> {
                            styleProperty.setDataFormat(formatDouble)
                            cell.setCellValue(reportResearchPrice)
                        }
                        5 -> {
                            styleProperty.setDataFormat(formatDouble)
                            cell.setCellValue(planResearchPrice)
                        }
                        6 -> cell.setCellValue(planResearchName)
                        else -> Unit
                    }
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    cell.mergeApply(styleProperty)
                }
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(15)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    fontProperty.reset()
                    styleProperty.reset()
                    fontProperty.setFontHeightInPoints(11)
                    fontProperty.bold = true
                    styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                    styleProperty.setBorderTop(BorderStyle.THIN)
                    styleProperty.setBorderRight(BorderStyle.THIN)
                    styleProperty.setBorderBottom(BorderStyle.THIN)
                    styleProperty.setBorderLeft(BorderStyle.THIN)
                    val cell: Cell = row.createCell(cellNum)
                    when (cellNum) {
                        0 -> {
                            styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                            fontProperty.bold = false
                            cell.setCellValue("ИТОГО")
                        }
                        3 -> {
                            styleProperty.setDataFormat(formatDouble)
                            cell.setCellValue(reportReviewPrice + reportResearchPrice)
                        }
                        5 -> {
                            styleProperty.setDataFormat(formatDouble)
                            cell.setCellValue(planReviewPrice + planResearchPrice)
                        }
                        else -> Unit
                    }
                    styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                    cell.mergeApply(styleProperty)
                }
                sheet.addMergedRegion(CellRangeAddress(15, 15, 0, 1))
            }

            sheet.groupRow(12, 14)
            sheet.setRowGroupCollapsed(12, true)

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(10)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                styleProperty.setWrapText(true)

                val row: Row = sheet.createRow(17)
                row.height = 700
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 0) cell.setCellValue("Начальник ПЭО")
                    else if (cellNum == 5) cell.setCellValue("Начальник производства")
                    cell.mergeApply(styleProperty)
                }
                sheet.addMergedRegion(CellRangeAddress(17, 17, 0, 1))
                sheet.addMergedRegion(CellRangeAddress(17, 17, 5, SHEET_CELL_WIDTH))
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(10)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                styleProperty.setVerticalAlignment(VerticalAlignment.BOTTOM)
                styleProperty.setWrapText(true)

                val row: Row = sheet.createRow(18)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    if (cellNum == 0) cell.setCellValue("___________________  $headEco")
                    else if (cellNum == 5) cell.setCellValue("___________________  $headProd")
                    cell.mergeApply(styleProperty)
                }
                sheet.addMergedRegion(CellRangeAddress(18, 18, 0, 1))
                sheet.addMergedRegion(CellRangeAddress(18, 18, 5, SHEET_CELL_WIDTH))
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(10)
                fontProperty.italic = true
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                styleProperty.setVerticalAlignment(VerticalAlignment.BOTTOM)
                styleProperty.setWrapText(true)

                val row: Row = sheet.createRow(19)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    val value = "             (подпись)                   (Ф.И.О.)"
                    if (cellNum == 0 || cellNum == 5) cell.setCellValue(value)
                    cell.mergeApply(styleProperty)
                }
                sheet.addMergedRegion(CellRangeAddress(19, 19, 0, 1))
                sheet.addMergedRegion(CellRangeAddress(19, 19, 5, SHEET_CELL_WIDTH))
            }

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(10)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                styleProperty.setVerticalAlignment(VerticalAlignment.BOTTOM)
                styleProperty.setWrapText(true)

                val row: Row = sheet.createRow(20)
                for (cellNum in 0..SHEET_CELL_WIDTH) {
                    val cell: Cell = row.createCell(cellNum)
                    val value = "\"____\"   ________________   2021 г"
                    if (cellNum == 0 || cellNum == 5) cell.setCellValue(value)
                    cell.mergeApply(styleProperty)
                }
                sheet.addMergedRegion(CellRangeAddress(20, 20, 0, 1))
                sheet.addMergedRegion(CellRangeAddress(20, 20, 5, SHEET_CELL_WIDTH))
            }

            sheet.createRow(21)
            workbook.setPrintArea(0, 0, SHEET_CELL_WIDTH, 0, 21)

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                fontProperty.setFontHeightInPoints(10)
                styleProperty.setFontIndex(workbook.defineFontIndex(fontProperty))
                styleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                styleProperty.setWrapText(true)

                val textFontProperty = BaseFontProperty()
                textFontProperty.setFontHeightInPoints(10)
                textFontProperty.setTypeOffset(FontTypeOffset.SUPERSCRIPT)
                var num = 1
                for (rowNum in 22..27) {
                    sheet.addMergedRegion(CellRangeAddress(rowNum, rowNum, 0, SHEET_CELL_WIDTH))
                    val row: Row = sheet.createRow(rowNum)
                    for (cellNum in 0..SHEET_CELL_WIDTH) {
                        val text = XSSFRichTextString(when (rowNum) {
                            22 -> {
                                row.height = 400
                                "$num Заполняется для НИР (ОКР) (по каждому этапу (подэтапу), в отношении которого формируется стоимостной показатель)."
                            }
                            23 -> {
                                row.height = 1000
                                "$num Представляются по статьям затрат согласно порядку определения состава затрат, включаемых в цену продукции, поставляемой в рамках государственного оборонного заказа, утвержденному в соответствии с постановлением Правительства Российской Федерации от 2 декабря 2017 года № 1465 \"О государственном регулировании цен на продукцию, поставляемую по государственному оборонному заказу, а также о внесении изменений и признании утратившими силу некоторых актов Правительства Российской Федерации."
                            }
                            24 -> {
                                row.height = 800
                                "$num В случае если технологический цикл производства продукции составляет более 1 года либо год начала производства продукции не соответствует году окончания ее производства и поставки (или завершения отдельных этапов поставки продукции, если государственным контрактом (контрактом) предусмотрены такие этапы), то организация представляет сведения по годам."
                            }
                            25 -> {
                                row.height = 1000
                                "$num В случае если продукция ранее не поставлялась, данные за отчетный период / период, предшествующий планируемому, представляются по имеющимся сведениям о затратах в отчетном периоде / периоде, предшествующем планируемому. В пояснительной записке, прилагаемой к соответствующему предложению о цене, организацией указывается, что продукция ранее не поставлялась."
                            }
                            26 -> {
                                row.height = 2500
                                "$num При проведении процедуры перевода в фиксированную цену других видов цен на продукцию организацией с учетом установленных в государственном контракте (контракте) условий уточнения и порядка перевода соответствующего вида цены на продукцию в фиксированную цену в графах 3.1, 3.2 представляются затраты по состоянию на конкретную дату, планируемые затраты до окончания поставки продукции в рамках выполнения государственного контракта (контракта) (или завершения отдельных этапов поставки продукции, если государственным контрактом (контрактом) предусмотрены такие этапы) представляются в графе 5.\n" +
                                        " В случае если технологический цикл производства продукции составляет более 1 года либо год начала производства продукции не соответствует году окончания ее производства и поставки (или завершения отдельных этапов поставки продукции, если государственным контрактом (контрактом) предусмотрены такие этапы), то организация представляет сведения о затратах по состоянию на конкретную дату в соответствии с графами 3.1, 3.2 по годам, дополнив форму после графы 3.2 графами, аналогичными по содержанию графам 3.1, 3.2 соответственно (с последующей корректировкой нумерации граф), на каждый год производства продукции для указания информации. "
                            }
                            27 -> {
                                row.height = 400
                                "$num В пояснительной записке, прилагаемой к соответствующему предложению о цене, организацией представляется обоснование применяемого индекса цен (в случае использования)."
                            }
                            else -> ""
                        })
                        text.applyFont(0, 1, workbook.defineFont(textFontProperty))
                        val cell: Cell = row.createCell(cellNum)
                        if (cellNum == 0) cell.setCellValue(text)
                        cell.mergeApply(styleProperty)
                    }
                    num++
                }
                sheet.groupRow(22, 27)
                sheet.setRowGroupCollapsed(22, true)
            }
        }

        return workbook
    }
}