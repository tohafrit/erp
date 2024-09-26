package ru.korundm.report.excel.document.military.report

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType
import ru.korundm.report.excel.helper.BaseCellStyleProperty
import ru.korundm.report.excel.helper.BaseFontProperty
import ru.korundm.report.excel.util.ExcelUtil.mergeApply
import ru.korundm.util.CommonUtil

class StateDefenceOrderErrorExcel {

    private val workbook: Workbook = XSSFWorkbook()
    private var errorList = mutableListOf<Pair<String, String>>()

    companion object {
        fun create(): StateDefenceOrderErrorExcel = StateDefenceOrderErrorExcel()
    }

    fun add(error: Pair<String, String>) = errorList.add(error)

    fun generate(): Workbook {
        val sheet: Sheet = workbook.createSheet("Список ошибок")

        run {
            sheet.setZoom(100) // масштабирование

            // Настройки печати
            val printSetup: PrintSetup = sheet.printSetup
            // Разметка страницы -> Настраиваемые поля (параметры страницы)
            // Страница
            sheet.fitToPage = true // Разместить не более чем на
            printSetup.fitWidth = 1
            printSetup.fitHeight = 0
            printSetup.landscape = true // альбомная ориентация
            printSetup.paperSize = PrintSetup.A4_PAPERSIZE // А4
            // Поля
            sheet.setMargin(Sheet.TopMargin, CommonUtil.cmToInch(1.9))
            sheet.setMargin(Sheet.BottomMargin, CommonUtil.cmToInch(1.9))
            sheet.setMargin(Sheet.LeftMargin, CommonUtil.cmToInch(0.6))
            sheet.setMargin(Sheet.RightMargin, CommonUtil.cmToInch(0.6))
            sheet.setMargin(Sheet.HeaderMargin, CommonUtil.cmToInch(0.8))
            sheet.setMargin(Sheet.FooterMargin, CommonUtil.cmToInch(0.8))
            sheet.horizontallyCenter = true // выравнивание по горизонтали
            // Колонтитулы
            val sheetHeader: Header = sheet.header
            if (sheetHeader is XSSFHeaderFooter) sheetHeader.headerFooter.alignWithMargins = false // Опция выравнивания относительно полей страницы
            // Режим просмотра книг - страничный режим
            if (sheet is XSSFSheet) sheet.ctWorksheet.sheetViews.getSheetViewArray(0).view =
                STSheetViewType.PAGE_BREAK_PREVIEW
            sheet.autobreaks = true // автоматический разрыв по страницей при печати

            // Ширина столбцов
            sheet.setColumnWidth(0, 1800)
            sheet.setColumnWidth(1, 10000)
            sheet.setColumnWidth(2, 10000)

            run {
                val fontProperty = BaseFontProperty()
                val styleProperty = BaseCellStyleProperty()
                val row: Row = sheet.createRow(0)
                fontProperty.setFontHeightInPoints(12)
                fontProperty.bold = true
                styleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER)
                styleProperty.setVerticalAlignment(VerticalAlignment.CENTER)
                val cell: Cell = row.createCell(0)
                sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 2))
                cell.setCellValue("Список ошибок при формировании отчета")
                cell.mergeApply(styleProperty)
            }

            run {
                val headerValues = arrayOf(
                    arrayOf<Any>(0, "№"),
                    arrayOf<Any>(1, "Название"),
                    arrayOf<Any>(2, "Описание"),
                )

                val row = sheet.createRow(1)
                for (headerValue in headerValues) {
                    val cell: Cell = row.createCell(headerValue[0] as Int)
                    cell.setCellValue(headerValue[1] as String)
                }
            }

            run {
                for (rowNum in 2..(errorList.size + 1)) {
                    val row = sheet.createRow(rowNum)
                    var cell: Cell = row.createCell(0)
                    cell.setCellValue((rowNum - 1).toString())
                    cell = row.createCell(1)
                    cell.setCellValue(errorList[rowNum - 2].first)
                    cell = row.createCell(2)
                    cell.setCellValue(errorList[rowNum - 2].second)
                }
            }
        }

        return workbook
    }
}
