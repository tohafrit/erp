package ru.korundm.report.excel.util

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.*
import ru.korundm.report.excel.helper.CellStyleProperty
import ru.korundm.report.excel.helper.FontProperty


/**
 * Утилити класс для работы с excel-файлами
 *
 * Для excel-книги существует лимит в 32767 уникальных шрифтов [Font] (для HSSF реализации)
 *
 * Для excel-книги существует лимит в 64000 стилей ячеек [CellStyle]
 *
 * https://support.office.com/en-us/article/excel-specifications-and-limits-1672b34d-7043-467e-8e27-269d656771c3
 * https://poi.apache.org/components/spreadsheet/quick-guide.html
 * @author mazur_ea
 * Date:   10.08.2021
 */
object ExcelUtil {

    /**
     * Метод применения свойств стиля ячейки к объекту ячейки, учитывая (выполняется слияние) оригинальный стиль ячейки
     * @param property свойства стиля ячейки [CellStyleProperty]
     */
    fun Cell.mergeApply(property: CellStyleProperty) {
        property.merge(this.cellStyle)
        this.apply(property)
    }

    /**
     * Метод применения свойств стиля ячейки к объекту ячейки, игнорируя оригинальный стиль ячейки
     * @param property свойства стиля ячейки [CellStyleProperty]
     */
    fun Cell.apply(property: CellStyleProperty) {
        val workbook: Workbook = this.sheet.workbook
        this.cellStyle = workbook.findCellStyle(property) ?: property.fill(workbook, workbook.createCellStyle())
    }

    /**
     * Поиск стиля ячейки в excel-книге
     * @param property свойства стиля ячейки [CellStyleProperty]
     * @return стиль ячейки или null, если стиль не найден
     */
    fun Workbook.findCellStyle(property: CellStyleProperty): CellStyle? {
        for (i in 0 until this.numCellStyles) {
            val style: CellStyle = this.getCellStyleAt(i)
            if (property.match(style)) return style
        }
        return null
    }

    /**
     * Метод выполняет определение индекса шрифта в рабочей excel-книге, и если находит, то возвращает его,
     * иначе создает новый шрифт и возвращает его индекс
     * @param property свойства шрифта [FontProperty]
     * @return индекс шрифта
     */
    fun Workbook.defineFontIndex(property: FontProperty) = this.defineFont(property).indexAsInt

    /**
     * Метод выполняет определение шрифта в рабочей excel-книге, и если находит, то возвращает его, иначе создает новый
     * @param property свойства шрифта [FontProperty]
     * @return шрифт [Font]
     */
    fun Workbook.defineFont(property: FontProperty) = this.findFont(property) ?: property.fill(this.createFont())

    /**
     * Поиск шрифта в excel-книге
     * @param property свойства шрифта [FontProperty]
     * @return шрифт [Font] или null, если шрифт не найден
     */
    fun Workbook.findFont(property: FontProperty): Font? {
        for (i in 0 until this.numberOfFontsAsInt) {
            val font: Font = this.getFontAt(i)
            if (property.match(font)) return font
        }
        return null
    }

    fun mergeExcelFiles(book: Workbook, wbList: List<XSSFWorkbook>): Workbook {
        for (b in wbList) {
            for (i in 0 until b.numberOfSheets) {
                val srcSheet = b.getSheetAt(i)
                val destSheet = book.createSheet(srcSheet.sheetName) as XSSFSheet
                destSheet.setZoom(srcSheet.ctWorksheet.sheetViews.getSheetViewArray(0).zoomScale.toInt())
                copySheets(destSheet, srcSheet)
            }
        }
        return book
    }

    private fun copySheets(newSheet: XSSFSheet, sheet: XSSFSheet) {
        var maxColumnNum = 0
        val styleMap = mutableMapOf<Int, XSSFCellStyle>()
        for (i in sheet.firstRowNum..sheet.lastRowNum) {
            val srcRow = sheet.getRow(i)
            val destRow = newSheet.createRow(i)
            if (srcRow != null) {
                copyRow(sheet, newSheet, srcRow, destRow, styleMap)
                if (srcRow.lastCellNum > maxColumnNum) maxColumnNum = srcRow.lastCellNum.toInt()
            }
        }
        for (i in 0..maxColumnNum) newSheet.setColumnWidth(i, sheet.getColumnWidth(i))
    }

    private fun copyRow(
        srcSheet: XSSFSheet,
        destSheet: XSSFSheet,
        srcRow: XSSFRow,
        destRow: XSSFRow,
        styleMap: MutableMap<Int, XSSFCellStyle>
    ) {
        destRow.height = srcRow.height
        if (srcRow.firstCellNum.toInt() == -1) return
        for (j in srcRow.firstCellNum..srcRow.lastCellNum) {
            val oldCell = srcRow.getCell(j)
            var newCell = destRow.getCell(j)
            if (oldCell != null) {
                if (newCell == null) newCell = destRow.createCell(j)
                copyCell(oldCell, newCell, styleMap)
                val range = getMergedCellRangeAddress(srcSheet, srcRow.rowNum, oldCell.columnIndex)
                if (range != null) {
                    val newMergedCellRangeAddress = CellRangeAddress(range.firstRow, range.lastRow, range.firstColumn, range.lastColumn)
                    if (!destSheet.mergedRegions.contains(newMergedCellRangeAddress)) {
                        destSheet.addMergedRegion(newMergedCellRangeAddress)
                    }
                }
            }
        }
    }

    fun copyCell(oldCell: XSSFCell, newCell: XSSFCell, styleMap: MutableMap<Int, XSSFCellStyle>) {
        if (oldCell.sheet.workbook === newCell.sheet.workbook) {
            newCell.cellStyle = oldCell.cellStyle
        } else {
            val stHashCode = oldCell.cellStyle.hashCode()
            var newCellStyle = styleMap[stHashCode]
            if (newCellStyle == null) {
                newCellStyle = newCell.sheet.workbook.createCellStyle()
                newCellStyle.cloneStyleFrom(oldCell.cellStyle)
                styleMap[stHashCode] = newCellStyle
            }
            newCell.cellStyle = newCellStyle
        }
        when (oldCell.cellType) {
            CellType.STRING -> newCell.setCellValue(oldCell.stringCellValue)
            CellType.NUMERIC -> newCell.setCellValue(oldCell.numericCellValue)
            CellType.BLANK -> newCell.cellType = CellType.BLANK
            CellType.BOOLEAN -> newCell.setCellValue(oldCell.booleanCellValue)
            CellType.ERROR -> newCell.setCellErrorValue(oldCell.errorCellValue)
            CellType.FORMULA -> newCell.cellFormula = oldCell.cellFormula
            else -> {}
        }
    }

    fun getMergedCellRangeAddress(sheet: XSSFSheet, rowNum: Int, cellNum: Int): CellRangeAddress? {
        for (i in 0 until sheet.mergedRegions.size) {
            val merged = sheet.getMergedRegion(i)
            if (merged.containsColumn(cellNum) && merged.containsRow(rowNum)) {
                return merged
            }
        }
        return null
    }
}