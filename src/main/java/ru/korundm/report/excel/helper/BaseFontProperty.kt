package ru.korundm.report.excel.helper

import org.apache.poi.common.usermodel.fonts.FontCharset
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.FontUnderline
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFFont
import ru.korundm.report.enumeration.FontName
import ru.korundm.report.excel.enumeration.FontTypeOffset

/**
 * Класс хранения свойств шрифта и работы со шрифтами [Font]
 * @author mazur_ea
 * Date:   25.10.2019
 */
class BaseFontProperty : FontProperty {

    var bold = false // свойство для выделения жирным текста шрифта
    var color: Short = 0 // цвет текста шрифта
    var fontHeight: Short = 0 // размер шрифта в юнитах (1 юнит = 1/20 поинта)
    var fontName = "" // наименование шрифта
    var italic = false // курсив
    var strikeout = false // перечеркнутый текст шрифта
    var typeOffset: Short = 0 // тип оффсета шрифта - без/под линией шрифта/над линией шрифта
    var underline: Byte = 0 // тип подчеркивания текста шрифта
    var charset = 0 // базовый набор символов, связанных со шрифтом (который он может отображать), и соответствует кодовой странице ANSI

    init { reset() }

    fun setColor(indexedColors: IndexedColors) {
        this.color = indexedColors.getIndex()
    }

    fun getFontHeightInPoints() = (this.fontHeight / 20)

    fun setFontHeightInPoints(value: Int) {
        this.fontHeight = (value * 20).toShort()
    }

    fun setFontName(fontName: FontName) {
        this.fontName = fontName.value
    }

    fun setTypeOffset(fontTypeOffset: FontTypeOffset) {
        this.typeOffset = fontTypeOffset.byteIndex.toShort()
    }

    fun setUnderline(fontUnderline: FontUnderline) {
        this.underline = fontUnderline.byteValue
    }

    fun setCharset(fontCharset: FontCharset) {
        this.charset = fontCharset.nativeId
    }

    fun from(cell: Cell) = from(cell.sheet.workbook.getFontAt(cell.cellStyle.fontIndexAsInt))

    fun from(font: Font) {
        this.bold = font.bold
        this.color = font.color
        this.fontHeight = font.fontHeight
        this.fontName = font.fontName
        this.italic = font.italic
        this.strikeout = font.strikeout
        this.typeOffset = font.typeOffset
        this.underline = font.underline
        this.charset = font.charSet
    }

    override fun match(font: Font) =
        font.bold == bold &&
        font.color == color &&
        font.fontHeight == fontHeight &&
        font.fontName == fontName &&
        font.italic == italic &&
        font.strikeout == strikeout &&
        font.typeOffset == typeOffset &&
        font.underline == underline &&
        font.charSet == charset

    override fun fill(font: Font): Font {
        font.bold = this.bold
        font.color = this.color
        font.fontHeight = this.fontHeight
        font.fontName = this.fontName
        font.italic = this.italic
        font.strikeout = this.strikeout
        font.typeOffset = this.typeOffset
        font.underline = this.underline
        font.charSet = this.charset
        return font
    }

    fun reset() {
        bold = false
        color = IndexedColors.BLACK.getIndex()
        fontHeight = (XSSFFont.DEFAULT_FONT_SIZE * 20).toShort()
        fontName = FontName.TIMES_NEW_ROMAN.value
        italic = false
        strikeout = false
        typeOffset = FontTypeOffset.NONE.byteIndex.toShort()
        underline = FontUnderline.NONE.byteValue
        charset = FontCharset.ANSI.nativeId
    }
}