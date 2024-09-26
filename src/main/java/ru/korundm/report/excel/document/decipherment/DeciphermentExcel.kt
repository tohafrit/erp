package ru.korundm.report.excel.document.decipherment

import org.apache.poi.ss.usermodel.Workbook
import java.io.IOException

/**
 * Интерфейс для формирования excel-файлов расшифровок
 * @author mazur_ea
 * Date:   26.09.2019
 */
interface DeciphermentExcel {
    @Throws(IOException::class)
    fun generate(deciphermentId: Long): Workbook
}