package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

/**
 * Утилити класс для работы со строками таблиц документа в word-отчетах
 * <br>
 * В xml отображении - pkg:part pkg:name="/word/document.xml"
 * <br>
 * Имеет тег - <b>w:tr</b>
 * @author mazur_ea
 * Date:   21.01.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RowUtil {

    // Методы узлов

    /**
     * Получение узла свойств
     * @param row строка {@link XWPFTableRow}
     * @return узел свойств {@link CTTrPr}
     */
    public static CTTrPr propertyNode(@NonNull XWPFTableRow row) {
        CTRow ctRow = row.getCtRow();
        return ctRow.isSetTrPr() ? ctRow.getTrPr() : ctRow.addNewTrPr();
    }

    // Методы настроек

    /**
     * Столбцы сетки после последней ячейки
     * <br>
     * Определяет количество столбцов сетки в сетке таблицы, которое должно быть оставлено после последней ячейки в строке таблицы
     * @param row строка {@link XWPFTableRow}
     * @param value значение
     */
    public static void gridAfter(@NonNull XWPFTableRow row, Long value) {
        CTTrPr ctTrPr = propertyNode(row);
        WordUtil.defaultSetCTDecimalNumber(ctTrPr.sizeOfGridAfterArray() == 0 ? ctTrPr.addNewGridAfter() : ctTrPr.getGridAfterList().get(0), value);
    }

    /**
     * Столбцы сетки перед первой ячейкой
     * <br>
     * Определяет количество столбцов сетки в сетке таблицы, которые должны быть пропущены до добавления содержимого этой строки таблицы
     * @param row строка {@link XWPFTableRow}
     * @param value значение
     */
    public static void gridBefore(@NonNull XWPFTableRow row, Long value) {
        CTTrPr ctTrPr = propertyNode(row);
        WordUtil.defaultSetCTDecimalNumber(ctTrPr.sizeOfGridBeforeArray() == 0 ? ctTrPr.addNewGridBefore() : ctTrPr.getGridBeforeList().get(0), value);
    }

    /**
     * Предпочтительная ширина после строки таблицы
     * <br>
     * Предпочтительная ширина для общего числа столбцов сетки после этой строки
     * @param row строка {@link XWPFTableRow}
     * @param measureType единица измерения {@link TableWidthType}
     * @param value значение
     */
    public static void wAfter(@NonNull XWPFTableRow row, @NonNull TableWidthType measureType, Long value) {
        CTTrPr ctTrPr = propertyNode(row);
        TableUtil.defaultSetCTTblWidth(ctTrPr.sizeOfWAfterArray() == 0 ? ctTrPr.addNewWAfter() : ctTrPr.getWAfterList().get(0), measureType.getStWidthType(), value);
    }

    /**
     * Предпочтительная ширина перед строкой таблицы
     * <br>
     * Предпочтительная ширину для общего числа столбцов сетки перед этой строкой
     * @param row строка {@link XWPFTableRow}
     * @param measureType единица измерения {@link TableWidthType}
     * @param value значение
     */
    public static void wBefore(@NonNull XWPFTableRow row, @NonNull TableWidthType measureType, Long value) {
        CTTrPr ctTrPr = propertyNode(row);
        TableUtil.defaultSetCTTblWidth(ctTrPr.sizeOfWBeforeArray() == 0 ? ctTrPr.addNewWBefore() : ctTrPr.getWBeforeList().get(0), measureType.getStWidthType(), value);
    }

    /**
     * Размер строки
     * <br>
     * Свойства таблицы -> Строка -> Размер
     * @param row узел свойств {@link XWPFTableRow}
     * @param rule режим {@link STHeightRule.Enum}
     */
    public static void height(@NonNull XWPFTableRow row, @NonNull STHeightRule.Enum rule, Long value) {
        CTTrPr ctTrPr = propertyNode(row);
        WordUtil.defaultSetCTHeight(ctTrPr.sizeOfTrHeightArray() == 0 ? ctTrPr.addNewTrHeight() : ctTrPr.getTrHeightArray(0), rule, value);
    }

    /**
     * Выравнивание внутри строки по горизонтали
     * @param row узел свойств {@link XWPFTableRow}
     * @param value режим {@link STJc.Enum}
     */
    public static void hAlign(@NonNull XWPFTableRow row, @NonNull STJc.Enum value) {
        CTTrPr ctTrPr = propertyNode(row);
        WordUtil.defaultSetCTJc(ctTrPr.sizeOfJcArray() == 0 ? ctTrPr.addNewJc() : ctTrPr.getJcList().get(0), value);
    }

    // Переиспользуемые методы
}