package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.TableWidthType;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import ru.korundm.report.word.helper.BorderType;
import ru.korundm.report.word.helper.CTBorderProperties;

import java.math.BigInteger;

/**
 * Утилити класс для работы с таблицами в документе в word-отчетах
 * <br>
 * В xml отображении - pkg:part pkg:name="/word/document.xml"
 * <br>
 * Имеет тег - <b>w:tbl</b>
 * @author mazur_ea
 * Date:   21.01.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TableUtil {

    // Методы узлов

    /**
     * Получение узла свойств
     * @param table таблица {@link XWPFTable}
     * @return узел свойств {@link CTTblPr}
     */
    public static CTTblPr propertyNode(@NonNull XWPFTable table) {
        CTTbl ctTbl = table.getCTTbl();
        return ctTbl.getTblPr() == null ? ctTbl.addNewTblPr() : ctTbl.getTblPr();
    }

    /**
     * Получение узла границ
     * @param table таблица {@link XWPFTable}
     * @return узел границ {@link CTTblBorders}
     */
    public static CTTblBorders bordersNode(@NonNull XWPFTable table) {
        CTTblPr ctTblPr = propertyNode(table);
        return ctTblPr.isSetTblBorders() ? ctTblPr.getTblBorders() : ctTblPr.getTblBorders();
    }

    // Методы настроек

    /**
     * Убрать отступы ячеек по умолчанию
     * @param table таблица {@link XWPFTable}
     */
    public static void unsetMargin(@NonNull XWPFTable table) {
        CTTblPr ctTblPr = propertyNode(table);
        if (ctTblPr.isSetTblCellMar()) {
            ctTblPr.unsetTblCellMar();
        }
    }

    /**
     * Убрать границы ячеек по умолчанию
     * @param table таблица {@link XWPFTable}
     */
    public static void unsetBorders(@NonNull XWPFTable table) {
        CTTblPr ctTblPr = propertyNode(table);
        if (ctTblPr.isSetTblBorders()) {
            ctTblPr.unsetTblBorders();
        }
    }

    /**
     * Ширина
     * <br>
     * Свойства таблицы -> Таблица -> Размер
     * @param table таблица {@link XWPFTable}
     * @param measureType единица измерения {@link TableWidthType}
     * @param width ширина в dxa
     */
    public static void width(@NonNull XWPFTable table, @NonNull TableWidthType measureType, Long width) {
        CTTblPr ctTblPr = propertyNode(table);
        defaultSetCTTblWidth(ctTblPr.isSetTblW() ? ctTblPr.getTblW() : ctTblPr.addNewTblW(), measureType.getStWidthType(), width);
    }

    /**
     * Отступ слева
     * <br>
     * Свойства таблицы -> Таблица -> отступ слева
     * @param table таблица {@link XWPFTable}
     * @param indent значение отступа в dxa
     */
    public static void leftIndent(@NonNull XWPFTable table, Long indent) {
        CTTblPr ctTblPr = propertyNode(table);
        defaultSetCTTblWidth(ctTblPr.isSetTblInd() ? ctTblPr.getTblInd() : ctTblPr.addNewTblInd(), STTblWidth.DXA, indent);
    }

    /**
     * Автоподбор размеров ячеек по содержимому
     * <br>
     * Свойства таблицы -> Таблица -> Параметры -> автоподбор размеров по содержимому
     * @param table таблица {@link XWPFTable}
     * @param turn true - включено, false - выключено
     */
    public static void cellAutoFit(@NonNull XWPFTable table, Boolean turn) {
        CTTblPr ctTblPr = propertyNode(table);
        defaultSetCTTblLayoutType(ctTblPr.isSetTblLayout() ? ctTblPr.getTblLayout() : ctTblPr.addNewTblLayout(), turn);
    }

    /**
     * Отступы полей ячеек по умолчанию
     * <br>
     * Свойства таблицы -> Таблица -> Параметры -> поля ячеек по умолчанию
     * @param table таблица {@link XWPFTable}
     * @param top отступ сверху в dxa
     * @param right отступ справа в dxa
     * @param bottom отступ снизу в dxa
     * @param left отступ слева в dxa
     */
    public static void cellMargin(@NonNull XWPFTable table, Long top, Long right, Long bottom, Long left) {
        CTTblPr ctTblPr = propertyNode(table);
        defaultSetCTTblCellMar(ctTblPr.isSetTblCellMar() ? ctTblPr.getTblCellMar() : ctTblPr.addNewTblCellMar(), top, right, bottom, left);
    }

    /**
     * Привязка стиля
     * @param table таблица {@link XWPFTable}
     * @param styleId идентификатор стиля
     */
    public static void style(@NonNull XWPFTable table, String styleId) {
        CTTblPr ctTblPr = propertyNode(table);
        WordUtil.defaultSetCTString(ctTblPr.isSetTblStyle() ? ctTblPr.getTblStyle() : ctTblPr.addNewTblStyle(), styleId);
    }

    /**
     * Верхняя граница по умолчанию
     * @param table таблица {@link XWPFTable}
     */
    public static void borderTop(@NonNull XWPFTable table) {
        borderTop(table, CTBorderProperties.instance());
    }

    /**
     * Верхняя граница
     * @param table таблица {@link XWPFTable}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void borderTop(@NonNull XWPFTable table, CTBorderProperties properties) {
        defaultSetCTTblBorders(bordersNode(table), BorderType.TOP, properties);
    }

    /**
     * Правая граница по умолчанию
     * @param table таблица {@link XWPFTable}
     */
    public static void borderRight(@NonNull XWPFTable table) {
        borderRight(table, CTBorderProperties.instance());
    }

    /**
     * Правая граница
     * @param table таблица {@link XWPFTable}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void borderRight(@NonNull XWPFTable table, CTBorderProperties properties) {
        defaultSetCTTblBorders(bordersNode(table), BorderType.RIGHT, properties);
    }

    /**
     * Нижняя граница по умолчанию
     * @param table таблица {@link XWPFTable}
     */
    public static void borderBottom(@NonNull XWPFTable table) {
        borderBottom(table, CTBorderProperties.instance());
    }

    /**
     * Нижняя граница
     * @param table таблица {@link XWPFTable}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void borderBottom(@NonNull XWPFTable table, CTBorderProperties properties) {
        defaultSetCTTblBorders(bordersNode(table), BorderType.BOTTOM, properties);
    }

    /**
     * Левая граница по умолчанию
     * @param table таблица {@link XWPFTable}
     */
    public static void borderLeft(@NonNull XWPFTable table) {
        borderLeft(table, CTBorderProperties.instance());
    }

    /**
     * Левая граница
     * @param table таблица {@link XWPFTable}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void borderLeft(@NonNull XWPFTable table, CTBorderProperties properties) {
        defaultSetCTTblBorders(bordersNode(table), BorderType.LEFT, properties);
    }

    // Переиспользуемые методы

    /**
     * Стандартная установка свойства сетки столбцов
     * @param ctTblGridCol свойства сетки столбцов {@link CTTblGridCol}
     * @param value значение
     */
    public static void defaultSetCTTblGridCol(@NonNull CTTblGridCol ctTblGridCol, Long value) {
        if (value == null) {
            ctTblGridCol.setNil();
        } else {
            ctTblGridCol.setW(BigInteger.valueOf(value));
        }
    }

    /**
     * Стандартная установка свойства-ширины таблицы
     * @param ctTblWidth свойство-ширина таблицы {@link CTTblWidth}
     * @param measureType единица измерения {@link STTblWidth.Enum}
     * @param value значение
     */
    public static void defaultSetCTTblWidth(@NonNull CTTblWidth ctTblWidth, @NonNull STTblWidth.Enum measureType, Long value) {
        ctTblWidth.setType(measureType);
        if (STTblWidth.AUTO.equals(measureType)) {
            ctTblWidth.setW(BigInteger.ZERO);
        } else if (value == null || STTblWidth.NIL.equals(measureType)) {
            ctTblWidth.setNil();
        } else if (STTblWidth.DXA.equals(measureType) || STTblWidth.PCT.equals(measureType)) {
            ctTblWidth.setW(BigInteger.valueOf(value));
        }
    }

    /**
     * Стандартная установка свойств отступов таблицы
     * @param ctTblWidthTop свойство отступа сверху {@link CTTblWidth}
     * @param top значение отступа сверху в dxa
     * @param ctTblWidthRight свойство отступа справа {@link CTTblWidth}
     * @param right значение отступа справа в dxa
     * @param ctTblWidthBottom свойство отступа снизу {@link CTTblWidth}
     * @param bottom значение отступа снизу в dxa
     * @param ctTblWidthLeft свойство отступа слева {@link CTTblWidth}
     * @param left значение отступа слева в dxa
     */
    public static void defaultSetCTTblWidthMargin(
        @NonNull CTTblWidth ctTblWidthTop,
        Long top,
        @NonNull CTTblWidth ctTblWidthRight,
        Long right,
        @NonNull CTTblWidth ctTblWidthBottom,
        Long bottom,
        @NonNull CTTblWidth ctTblWidthLeft,
        Long left
    ) {
        defaultSetCTTblWidth(ctTblWidthTop, STTblWidth.DXA, top);
        defaultSetCTTblWidth(ctTblWidthRight, STTblWidth.DXA, right);
        defaultSetCTTblWidth(ctTblWidthBottom, STTblWidth.DXA, bottom);
        defaultSetCTTblWidth(ctTblWidthLeft, STTblWidth.DXA, left);
    }

    /**
     * Стандартная установка свойства отступов таблицы
     * @param ctTblCellMar свойство отступов таблицы {@link CTTblCellMar}
     * @param top отступ сверху в dxa
     * @param right отступ справа в dxa
     * @param bottom отступ снизу в dxa
     * @param left отступ слева в dxa
     */
    public static void defaultSetCTTblCellMar(@NonNull CTTblCellMar ctTblCellMar, Long top, Long right, Long bottom, Long left) {
        defaultSetCTTblWidthMargin(
            ctTblCellMar.isSetTop() ? ctTblCellMar.getTop() : ctTblCellMar.addNewTop(), top,
            ctTblCellMar.isSetRight() ? ctTblCellMar.getRight() : ctTblCellMar.addNewRight(), right,
            ctTblCellMar.isSetBottom() ? ctTblCellMar.getBottom() : ctTblCellMar.addNewBottom(), bottom,
            ctTblCellMar.isSetLeft() ? ctTblCellMar.getLeft() : ctTblCellMar.addNewLeft(), left
        );
        if (
            !ctTblCellMar.isSetTop() &&
            !ctTblCellMar.isSetRight() &&
            !ctTblCellMar.isSetBottom() &&
            !ctTblCellMar.isSetLeft()
        ) {
            ctTblCellMar.setNil();
        }
    }

    /**
     * Стандартная установка свойства авто подгонки размеров по содержимому для таблицы
     * @param ctTblLayoutType свойство подгонки размеров по содержимому для таблицы {@link CTTblLayoutType}
     * @param turn true - авто, false - фиксированный
     */
    public static void defaultSetCTTblLayoutType(@NonNull CTTblLayoutType ctTblLayoutType, Boolean turn) {
        if (turn == null) {
            ctTblLayoutType.setNil();
        } else {
            ctTblLayoutType.setType(turn ? STTblLayoutType.AUTOFIT : STTblLayoutType.FIXED);
        }
    }

    /**
     * Стандартная установка свойства границ
     * @param ctTblBorders свойство границ {@link CTTblBorders}
     * @param borderType тип ориентации границы {@link BorderType}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void defaultSetCTTblBorders(@NonNull CTTblBorders ctTblBorders, @NonNull BorderType borderType, CTBorderProperties properties) {
        CTBorder ctBorder = null;
        if (BorderType.TOP.equals(borderType)) {
            ctBorder = ctTblBorders.isSetTop() ? ctTblBorders.getTop() : ctTblBorders.addNewTop();
        } else if (BorderType.RIGHT.equals(borderType)) {
            ctBorder = ctTblBorders.isSetRight() ? ctTblBorders.getRight() : ctTblBorders.addNewRight();
        } else if (BorderType.BOTTOM.equals(borderType)) {
            ctBorder = ctTblBorders.isSetBottom() ? ctTblBorders.getBottom() : ctTblBorders.addNewBottom();
        } else if (BorderType.LEFT.equals(borderType)) {
            ctBorder = ctTblBorders.isSetLeft() ? ctTblBorders.getLeft() : ctTblBorders.addNewLeft();
        } else if (BorderType.INSIDE_H.equals(borderType)) {
            ctBorder = ctTblBorders.isSetInsideH() ? ctTblBorders.getInsideH() : ctTblBorders.addNewInsideH();
        } else if (BorderType.INSIDE_V.equals(borderType)) {
            ctBorder = ctTblBorders.isSetInsideV() ? ctTblBorders.getInsideV() : ctTblBorders.addNewInsideV();
        }
        if (ctBorder != null) {
            WordUtil.defaultSetCTBorder(ctBorder, properties);
        }
        if (
            !ctTblBorders.isSetTop() &&
            !ctTblBorders.isSetRight() &&
            !ctTblBorders.isSetBottom() &&
            !ctTblBorders.isSetLeft() &&
            !ctTblBorders.isSetInsideH() &&
            !ctTblBorders.isSetInsideV()
        ) {
            ctTblBorders.setNil();
        }
    }
}