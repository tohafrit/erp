package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import ru.korundm.report.word.helper.BorderType;
import ru.korundm.report.word.helper.CTBorderProperties;

/**
 * Утилити класс для работы с ячейками таблиц документа в word-отчетах
 * <br>
 * В xml отображении - part pkg:name="/word/document.xml"
 * <br>
 * Имеет тег - <b>w:tc</b>
 * @author mazur_ea
 * Date:   21.01.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CellUtil {

    // Методы узлов

    /**
     * Получение узла свойств
     * @param cell ячейка {@link XWPFTableCell}
     * @return узел свойств {@link CTTcPr}
     */
    public static CTTcPr propertyNode(@NonNull XWPFTableCell cell) {
        CTTc ctTc = cell.getCTTc();
        return ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
    }

    /**
     * Получение узла границ
     * @param cell ячейка {@link XWPFTableCell}
     * @return узел границ {@link CTTcBorders}
     */
    public static CTTcBorders bordersNode(@NonNull XWPFTableCell cell) {
        CTTcPr ctTcPr = propertyNode(cell);
        return ctTcPr.isSetTcBorders() ? ctTcPr.getTcBorders() : ctTcPr.addNewTcBorders();
    }

    // Методы настроек

    /**
     * Убрать границы ячеек
     * @param cell ячейка {@link XWPFTable}
     */
    public static void unsetBorders(@NonNull XWPFTableCell cell) {
        CTTcPr ctTcPr = propertyNode(cell);
        if (ctTcPr.isSetTcBorders()) {
            ctTcPr.unsetTcBorders();
        }
    }

    /**
     * Ширина
     * <br>
     * Свойства таблицы -> Ячейка -> Размер
     * @param cell ячейка {@link XWPFTableCell}
     * @param measureType единица измерения {@link TableWidthType}
     * @param width ширина в dxa
     */
    public static void width(@NonNull XWPFTableCell cell, @NonNull TableWidthType measureType, Long width) {
        CTTcPr ctTcPr = propertyNode(cell);
        TableUtil.defaultSetCTTblWidth(ctTcPr.isSetTcW() ? ctTcPr.getTcW() : ctTcPr.addNewTcW(), measureType.getStWidthType(), width);
    }

    /**
     * Тип слияния по вертикали
     * @param cell ячейка {@link XWPFTableCell}
     * @param mergeValue тип слияния {@link STMerge.Enum}
     */
    public static void verticalMerge(@NonNull XWPFTableCell cell, STMerge.Enum mergeValue) {
        CTTcPr ctTcPr = propertyNode(cell);
        WordUtil.defaultSetCTVMerge(ctTcPr.isSetVMerge() ? ctTcPr.getVMerge() : ctTcPr.addNewVMerge(), mergeValue);
    }

    /**
     * Слияние столбцов
     * @param cell ячейка {@link XWPFTableCell}
     * @param mergeValue значение слияния
     */
    public static void gridSpan(@NonNull XWPFTableCell cell, Long mergeValue) {
        CTTcPr ctTcPr = propertyNode(cell);
        WordUtil.defaultSetCTDecimalNumber(ctTcPr.isSetGridSpan() ? ctTcPr.getGridSpan() : ctTcPr.addNewGridSpan(), mergeValue);
    }

    /**
     * Верхняя граница по умолчанию
     * @param cell ячейка {@link XWPFTableCell}
     */
    public static void borderTop(@NonNull XWPFTableCell cell) {
        borderTop(cell, CTBorderProperties.instance());
    }

    /**
     * Верхняя граница
     * @param cell ячейка {@link XWPFTableCell}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void borderTop(@NonNull XWPFTableCell cell, CTBorderProperties properties) {
        defaultSetCTTcBorders(bordersNode(cell), BorderType.TOP, properties);
    }

    /**
     * Правая граница по умолчанию
     * @param cell ячейка {@link XWPFTableCell}
     */
    public static void borderRight(@NonNull XWPFTableCell cell) {
        borderRight(cell, CTBorderProperties.instance());
    }

    /**
     * Правая граница
     * @param cell ячейка {@link XWPFTableCell}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void borderRight(@NonNull XWPFTableCell cell, CTBorderProperties properties) {
        defaultSetCTTcBorders(bordersNode(cell), BorderType.RIGHT, properties);
    }

    /**
     * Нижняя граница по умолчанию
     * @param cell ячейка {@link XWPFTableCell}
     */
    public static void borderBottom(@NonNull XWPFTableCell cell) {
        borderBottom(cell, CTBorderProperties.instance());
    }

    /**
     * Нижняя граница
     * @param cell ячейка {@link XWPFTableCell}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void borderBottom(@NonNull XWPFTableCell cell, CTBorderProperties properties) {
        defaultSetCTTcBorders(bordersNode(cell), BorderType.BOTTOM, properties);
    }

    /**
     * Левая граница по умолчанию
     * @param cell ячейка {@link XWPFTableCell}
     */
    public static void borderLeft(@NonNull XWPFTableCell cell) {
        borderLeft(cell, CTBorderProperties.instance());
    }

    /**
     * Левая граница
     * @param cell ячейка {@link XWPFTableCell}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void borderLeft(@NonNull XWPFTableCell cell, CTBorderProperties properties) {
        defaultSetCTTcBorders(bordersNode(cell), BorderType.LEFT, properties);
    }

    /**
     * Отступы полей ячеек
     * <br>
     * Свойства таблицы -> Ячейка -> Параметры -> поля ячеек по умолчанию
     * @param cell ячейка {@link XWPFTableCell}
     * @param top отступ сверху в dxa
     * @param right отступ справа в dxa
     * @param bottom отступ снизу в dxa
     * @param left отступ слева в dxa
     */
    public static void margin(@NonNull XWPFTableCell cell, Long top, Long right, Long bottom, Long left) {
        CTTcPr ctTcPr = propertyNode(cell);
        defaultSetCTTcMar(ctTcPr.isSetTcMar() ? ctTcPr.getTcMar() : ctTcPr.addNewTcMar(), top, right, bottom, left);
    }

    // Переиспользуемые методы

    /**
     * Стандартная установка свойства границ
     * @param ctTcBorders свойство границ {@link CTTcBorders}
     * @param borderType тип ориентации границы {@link BorderType}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void defaultSetCTTcBorders(@NonNull CTTcBorders ctTcBorders, @NonNull BorderType borderType, CTBorderProperties properties) {
        CTBorder ctBorder = null;
        if (BorderType.TOP.equals(borderType)) {
            ctBorder = ctTcBorders.isSetTop() ? ctTcBorders.getTop() : ctTcBorders.addNewTop();
        } else if (BorderType.RIGHT.equals(borderType)) {
            ctBorder = ctTcBorders.isSetRight() ? ctTcBorders.getRight() : ctTcBorders.addNewRight();
        } else if (BorderType.BOTTOM.equals(borderType)) {
            ctBorder = ctTcBorders.isSetBottom() ? ctTcBorders.getBottom() : ctTcBorders.addNewBottom();
        } else if (BorderType.LEFT.equals(borderType)) {
            ctBorder = ctTcBorders.isSetLeft() ? ctTcBorders.getLeft() : ctTcBorders.addNewLeft();
        } else if (BorderType.INSIDE_H.equals(borderType)) {
            ctBorder = ctTcBorders.isSetInsideH() ? ctTcBorders.getInsideH() : ctTcBorders.addNewInsideH();
        } else if (BorderType.INSIDE_V.equals(borderType)) {
            ctBorder = ctTcBorders.isSetInsideV() ? ctTcBorders.getInsideV() : ctTcBorders.addNewInsideV();
        } else if (BorderType.TL2BR.equals(borderType)) {
            ctBorder = ctTcBorders.isSetTl2Br() ? ctTcBorders.getTl2Br() : ctTcBorders.addNewTl2Br();
        } else if (BorderType.TR2BL.equals(borderType)) {
            ctBorder = ctTcBorders.isSetTr2Bl() ? ctTcBorders.getTr2Bl() : ctTcBorders.addNewTr2Bl();
        }
        if (ctBorder != null) {
            WordUtil.defaultSetCTBorder(ctBorder, properties);
        }
        if (
            !ctTcBorders.isSetTop() &&
            !ctTcBorders.isSetRight() &&
            !ctTcBorders.isSetBottom() &&
            !ctTcBorders.isSetLeft() &&
            !ctTcBorders.isSetInsideH() &&
            !ctTcBorders.isSetInsideV() &&
            !ctTcBorders.isSetTl2Br() &&
            !ctTcBorders.isSetTr2Bl()
        ) {
            ctTcBorders.setNil();
        }
    }

    /**
     * Стандартная установка свойства отступов ячейки
     * @param ctTcMar свойство отступов ячейки {@link CTTcMar}
     * @param top отступ сверху в dxa
     * @param right отступ справа в dxa
     * @param bottom отступ снизу в dxa
     * @param left отступ слева в dxa
     */
    public static void defaultSetCTTcMar(@NonNull CTTcMar ctTcMar, Long top, Long right, Long bottom, Long left) {
        TableUtil.defaultSetCTTblWidthMargin(
            ctTcMar.isSetTop() ? ctTcMar.getTop() : ctTcMar.addNewTop(), top,
            ctTcMar.isSetRight() ? ctTcMar.getRight() : ctTcMar.addNewRight(), right,
            ctTcMar.isSetBottom() ? ctTcMar.getBottom() : ctTcMar.addNewBottom(), bottom,
            ctTcMar.isSetLeft() ? ctTcMar.getLeft() : ctTcMar.addNewLeft(), left
        );
        if (
            !ctTcMar.isSetTop() &&
            !ctTcMar.isSetRight() &&
            !ctTcMar.isSetBottom() &&
            !ctTcMar.isSetLeft()
        ) {
            ctTcMar.setNil();
        }
    }
}