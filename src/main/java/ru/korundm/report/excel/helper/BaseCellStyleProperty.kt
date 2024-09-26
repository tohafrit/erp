package ru.korundm.report.excel.helper

import org.apache.poi.ss.usermodel.*
import ru.korundm.report.excel.enumeration.CellStylePropertyEntry
import ru.korundm.report.excel.enumeration.CellStylePropertyEntry.*

class BaseCellStyleProperty : CellStyleProperty {

    companion object {
        private val defaultPropertyMap = mapOf(
            HORIZONTAL_ALIGNMENT to HorizontalAlignment.GENERAL,
            VERTICAL_ALIGNMENT to VerticalAlignment.BOTTOM,
            BORDER_TOP to BorderStyle.NONE,
            BORDER_RIGHT to BorderStyle.NONE,
            BORDER_BOTTOM to BorderStyle.NONE,
            BORDER_LEFT to BorderStyle.NONE,
            TOP_BORDER_COLOR to IndexedColors.BLACK.getIndex(),
            RIGHT_BORDER_COLOR to IndexedColors.BLACK.getIndex(),
            BOTTOM_BORDER_COLOR to IndexedColors.BLACK.getIndex(),
            LEFT_BORDER_COLOR to IndexedColors.BLACK.getIndex(),
            DATA_FORMAT to 0,
            FILL_PATTERN to FillPatternType.NO_FILL,
            FILL_FOREGROUND_COLOR to IndexedColors.AUTOMATIC.getIndex(),
            FILL_BACKGROUND_COLOR to IndexedColors.WHITE.getIndex(),
            FONT_INDEX to 0,
            HIDDEN to false,
            INDENTION to 0,
            LOCKED to false,
            ROTATION to 0,
            WRAP_TEXT to false,
            SHRINK_TO_FIT to false
        )
    }

    private val propertyMap = mutableMapOf<CellStylePropertyEntry, Any>()

    fun setHorizontalAlignment(horizontalAlignment: HorizontalAlignment) {
        this.propertyMap[HORIZONTAL_ALIGNMENT] = horizontalAlignment
    }

    fun setVerticalAlignment(verticalAlignment: VerticalAlignment) {
        this.propertyMap[VERTICAL_ALIGNMENT] = verticalAlignment
    }

    fun setBorderTop(borderTop: BorderStyle) {
        this.propertyMap[BORDER_TOP] = borderTop
    }

    fun setBorderRight(borderRight: BorderStyle) {
        this.propertyMap[BORDER_RIGHT] = borderRight
    }

    fun setBorderBottom(borderBottom: BorderStyle) {
        this.propertyMap[BORDER_BOTTOM] = borderBottom
    }

    fun setBorderLeft(borderLeft: BorderStyle) {
        this.propertyMap[BORDER_LEFT] = borderLeft
    }

    fun setTopBorderColor(topBorderColor: Short) {
        this.propertyMap[TOP_BORDER_COLOR] = topBorderColor
    }

    fun setRightBorderColor(rightBorderColor: Short) {
        this.propertyMap[RIGHT_BORDER_COLOR] = rightBorderColor
    }

    fun setBottomBorderColor(bottomBorderColor: Short) {
        this.propertyMap[BOTTOM_BORDER_COLOR] = bottomBorderColor
    }

    fun setLeftBorderColor(leftBorderColor: Short) {
        this.propertyMap[LEFT_BORDER_COLOR] = leftBorderColor
    }

    fun setDataFormat(dataFormat: Short) {
        this.propertyMap[DATA_FORMAT] = dataFormat
    }

    fun setFillPattern(fillPattern: FillPatternType) {
        this.propertyMap[FILL_PATTERN] = fillPattern
    }

    fun setFillForegroundColor(fillForegroundColor: Short) {
        this.propertyMap[FILL_FOREGROUND_COLOR] = fillForegroundColor
    }

    fun setFillBackgroundColor(fillBackgroundColor: Short) {
        this.propertyMap[FILL_BACKGROUND_COLOR] = fillBackgroundColor
    }

    fun setFontIndex(fontIndex: Int) {
        this.propertyMap[FONT_INDEX] = fontIndex
    }

    fun setHidden(hidden: Boolean) {
        this.propertyMap[HIDDEN] = hidden
    }

    fun setIndention(indention: Short) {
        this.propertyMap[INDENTION] = indention
    }

    fun setLocked(locked: Boolean) {
        this.propertyMap[LOCKED] = locked
    }

    fun setRotation(rotation: Short) {
        this.propertyMap[ROTATION] = rotation
    }

    fun setWrapText(wrapText: Boolean) {
        this.propertyMap[WRAP_TEXT] = wrapText
    }

    fun setShrinkToFit(shrinkToFit: Boolean) {
        this.propertyMap[SHRINK_TO_FIT] = shrinkToFit
    }

    private fun replenish(sourceMap: Map<CellStylePropertyEntry, Any>) {
        for ((key, value) in sourceMap) {
            if (!propertyMap.containsKey(key)) propertyMap[key] = value
        }
        // Фикс для цвета фона ячейки при ее редактировании.
        // Если для стиля ячейки выставлены какие либо параметры кроме стандартных,
        // а Background цвет при равен IndexedColors.AUTOMATIC.getIndex(), то на выходе будет всегда черная ячейка
        propertyMap[FILL_BACKGROUND_COLOR]?.let {
            if (it as Short == IndexedColors.AUTOMATIC.getIndex()) propertyMap[FILL_BACKGROUND_COLOR] = IndexedColors.WHITE.getIndex()
        }
    }

    override fun merge(style: CellStyle) = replenish(mapOf(
        HORIZONTAL_ALIGNMENT to style.alignment,
        VERTICAL_ALIGNMENT to style.verticalAlignment,
        BORDER_TOP to style.borderTop,
        BORDER_RIGHT to style.borderRight,
        BORDER_BOTTOM to style.borderBottom,
        BORDER_LEFT to style.borderLeft,
        TOP_BORDER_COLOR to style.topBorderColor,
        RIGHT_BORDER_COLOR to style.rightBorderColor,
        BOTTOM_BORDER_COLOR to style.bottomBorderColor,
        LEFT_BORDER_COLOR to style.leftBorderColor,
        DATA_FORMAT to style.dataFormat,
        FILL_PATTERN to style.fillPattern,
        FILL_FOREGROUND_COLOR to style.fillForegroundColor,
        FILL_BACKGROUND_COLOR to style.fillBackgroundColor,
        FONT_INDEX to style.fontIndexAsInt,
        HIDDEN to style.hidden,
        INDENTION to style.indention,
        LOCKED to style.locked,
        ROTATION to style.rotation,
        WRAP_TEXT to style.wrapText,
        SHRINK_TO_FIT to style.shrinkToFit
    ))

    override fun fill(workbook: Workbook, style: CellStyle): CellStyle {
        replenish(defaultPropertyMap)
        style.alignment = propertyMap[HORIZONTAL_ALIGNMENT] as HorizontalAlignment
        style.verticalAlignment = propertyMap[VERTICAL_ALIGNMENT] as VerticalAlignment
        style.borderTop = propertyMap[BORDER_TOP] as BorderStyle
        style.borderRight = propertyMap[BORDER_RIGHT] as BorderStyle
        style.borderBottom = propertyMap[BORDER_BOTTOM] as BorderStyle
        style.borderLeft = propertyMap[BORDER_LEFT] as BorderStyle
        style.topBorderColor = propertyMap[TOP_BORDER_COLOR] as Short
        style.rightBorderColor = propertyMap[RIGHT_BORDER_COLOR] as Short
        style.bottomBorderColor = propertyMap[BOTTOM_BORDER_COLOR] as Short
        style.leftBorderColor = propertyMap[LEFT_BORDER_COLOR] as Short
        style.dataFormat = propertyMap[DATA_FORMAT] as Short
        style.fillPattern = propertyMap[FILL_PATTERN] as FillPatternType
        style.fillForegroundColor = propertyMap[FILL_FOREGROUND_COLOR] as Short
        style.fillBackgroundColor = propertyMap[FILL_BACKGROUND_COLOR] as Short
        style.setFont(workbook.getFontAt(propertyMap[FONT_INDEX] as Int))
        style.hidden = propertyMap[HIDDEN] as Boolean
        style.indention = propertyMap[INDENTION] as Short
        style.locked = propertyMap[LOCKED] as Boolean
        style.rotation = propertyMap[ROTATION] as Short
        style.wrapText = propertyMap[WRAP_TEXT] as Boolean
        style.shrinkToFit = propertyMap[SHRINK_TO_FIT] as Boolean
        return style
    }

    override fun match(style: CellStyle): Boolean {
        replenish(defaultPropertyMap)
        return style.alignment == propertyMap[HORIZONTAL_ALIGNMENT] as HorizontalAlignment
            && style.verticalAlignment == propertyMap[VERTICAL_ALIGNMENT] as VerticalAlignment
            && style.borderTop == propertyMap[BORDER_TOP] as BorderStyle
            && style.borderRight == propertyMap[BORDER_RIGHT] as BorderStyle
            && style.borderBottom == propertyMap[BORDER_BOTTOM] as BorderStyle
            && style.borderLeft == propertyMap[BORDER_LEFT] as BorderStyle
            && style.topBorderColor == propertyMap[TOP_BORDER_COLOR] as Short
            && style.rightBorderColor == propertyMap[RIGHT_BORDER_COLOR] as Short
            && style.bottomBorderColor == propertyMap[BOTTOM_BORDER_COLOR] as Short
            && style.leftBorderColor == propertyMap[LEFT_BORDER_COLOR] as Short
            && style.dataFormat == propertyMap[DATA_FORMAT] as Short
            && style.fillPattern == propertyMap[FILL_PATTERN] as FillPatternType
            && style.fillForegroundColor == propertyMap[FILL_FOREGROUND_COLOR] as Short
            && style.fillBackgroundColor == propertyMap[FILL_BACKGROUND_COLOR] as Short
            && style.fontIndexAsInt == propertyMap[FONT_INDEX] as Int
            && style.hidden == propertyMap[HIDDEN] as Boolean
            && style.indention == propertyMap[INDENTION] as Short
            && style.locked == propertyMap[LOCKED] as Boolean
            && style.rotation == propertyMap[ROTATION] as Short
            && style.wrapText == propertyMap[WRAP_TEXT] as Boolean
            && style.shrinkToFit == propertyMap[SHRINK_TO_FIT] as Boolean
    }

    fun reset() = propertyMap.clear()
}