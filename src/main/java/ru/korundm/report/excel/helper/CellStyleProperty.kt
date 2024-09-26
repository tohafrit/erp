package ru.korundm.report.excel.helper

import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Workbook

interface CellStyleProperty {
    fun merge(style: CellStyle)
    fun fill(workbook: Workbook, style: CellStyle): CellStyle
    fun match(style: CellStyle): Boolean
}