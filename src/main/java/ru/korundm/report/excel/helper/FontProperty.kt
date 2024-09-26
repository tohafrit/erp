package ru.korundm.report.excel.helper

import org.apache.poi.ss.usermodel.Font

interface FontProperty {

    fun match(font: Font): Boolean
    fun fill(font: Font): Font
}