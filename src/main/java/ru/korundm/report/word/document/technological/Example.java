package ru.korundm.report.word.document.technological;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import ru.korundm.report.enumeration.FontName;
import ru.korundm.report.word.helper.CTBorderProperties;
import ru.korundm.report.word.helper.CTFontsProperties;
import ru.korundm.report.word.helper.CTIndProperties;
import ru.korundm.report.word.helper.CTSpacingProperties;
import ru.korundm.report.word.util.*;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

public class Example {

    public static void main(String[] args) throws Exception {
        generate();
    }

    private static void generate() throws Exception {
        XWPFDocument document = new XWPFDocument();
        document.setZoomPercent(140);
        DocumentUtil.orientSize(document, STPageOrientation.LANDSCAPE, WordUtil.cmToDXA(29.7), WordUtil.cmToDXA(21));
        // Отступы документа
        long valueRL = WordUtil.cmToDXA(1.0), valueTB = WordUtil.cmToDXA(0.5), valueHF = WordUtil.cmToDXA(1.25);
        DocumentUtil.pageMargin(document, valueTB, valueRL, valueTB,  valueRL, valueHF, valueHF, null);

        // Элементы стилей
        XWPFStyles styles = document.createStyles();
        // Стандартные настройки стилей абзаца
        XWPFDefaultParagraphStyle defaultParagraphStyle = styles.getDefaultParagraphStyle();
        ParagraphDefaultsUtil.propertyNode(defaultParagraphStyle).setNil();
        // Стандартные настройки для стиля текста
        XWPFDefaultRunStyle defaultRunStyle = styles.getDefaultRunStyle();
        RunDefaultsUtil.size(defaultRunStyle, null);
        RunDefaultsUtil.sizeCs(defaultRunStyle, null);

        CTBorderProperties nilBorderProperties = CTBorderProperties.instance().val(STBorder.NIL); // Обнуление границ
        String tableContentId = "tableContent"; // id стиля для содержимого таблицы
        // Стили
        {
            // Стандартный стиль абзаца
            {
                XWPFStyle style = StyleUtil.defaultStyle(styles, STStyleType.PARAGRAPH);
                StyleUtil.styleQFormat(style, Boolean.TRUE);
                StyleUtil.paragraphWindowControl(style, Boolean.FALSE);
                StyleUtil.paragraphSuppressAutoHyphens(style, Boolean.TRUE);
                StyleUtil.runSize(style, WordUtil.ptToHalfPoints(12L));
                StyleUtil.runSizeCs(style, WordUtil.ptToHalfPoints(12L));
                StyleUtil.runFonts(style, CTFontsProperties.instance()
                    .ascii(FontName.TIMES_NEW_ROMAN.getValue())
                    .cs(FontName.TIMES_NEW_ROMAN.getValue())
                    .eastAsia(FontName.TIMES_NEW_ROMAN.getValue())
                    .hAnsi(FontName.TIMES_NEW_ROMAN.getValue())
                );
                styles.addStyle(style);
            }
            // Содержимое таблицы
            {
                XWPFStyle style = StyleUtil.createStyle(STStyleType.PARAGRAPH);
                // Настройки стиля
                style.setStyleId(tableContentId);
                StyleUtil.styleName(style, "Содержимое таблицы");
                StyleUtil.styleBasedOn(style, StyleUtil.DEFAULT_PARAGRAPH_STYLE_ID);
                StyleUtil.styleNext(style, StyleUtil.DEFAULT_PARAGRAPH_STYLE_ID);
                StyleUtil.paragraphSuppressLineNumbers(style, Boolean.TRUE);
                StyleUtil.paragraphSpacing(style, CTSpacingProperties.instance().after(120L));
                styles.addStyle(style);
            }
        }

        // Стр.1

        // Пустая стока абзаца перед таблицей
        {
            document.createParagraph();
        }

        {
            // Настройки таблицы
            XWPFTable table = document.createTable();
            TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(27.68));
            TableUtil.leftIndent(table, WordUtil.cmToDXA(0.02));
            TableUtil.cellAutoFit(table, Boolean.FALSE);
            TableUtil.unsetBorders(table);
            TableUtil.cellMargin(table, null, WordUtil.cmToDXA(0), null, WordUtil.cmToDXA(0));

            // 1
            {
                XWPFTableRow row = table.getRow(0);
                XWPFTableCell cell = row.getCell(0);
                CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(27.68));
                CellUtil.gridSpan(cell, 24L);
                // Настройка абзаца
                XWPFParagraph paragraph = cell.getParagraphs().get(0);
                paragraph.setStyle(tableContentId);
                ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                paragraph.setAlignment(ParagraphAlignment.RIGHT);
                // Текст
                XWPFRun run = paragraph.createRun();
                run.setText("ГОСТ 3.1105-2011                                              Форма 2");
                run.setFontSize(10);
            }

            // 2
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.79));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    ParagraphUtil.indentation(paragraph, CTIndProperties.instance().firstLine(131L));
                    XWPFRun run = paragraph.createRun();
                    RunUtil.position(run, WordUtil.ptToHalfPoints(11));
                    run.setText("Дубл.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.6));
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.55));
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.48));
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.03));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.13));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 3L);
                }

                { // 9
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.03));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.36));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 12
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 4L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.68));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.gridSpan(cell, 2L);
                }
            }

            // 3
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.79));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    ParagraphUtil.indentation(paragraph, CTIndProperties.instance().firstLine(131L));
                    XWPFRun run = paragraph.createRun();
                    RunUtil.position(run, WordUtil.ptToHalfPoints(11));
                    run.setText("Взам.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.6));
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.55));
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.48));
                    CellUtil.borderRight(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.03));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.0));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.13));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 3L);
                }

                { // 9
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.03));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.36));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 12
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 4L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.68));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.gridSpan(cell, 2L);
                }
            }

            // 4
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.79));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    ParagraphUtil.indentation(paragraph, CTIndProperties.instance().firstLine(131L));
                    XWPFRun run = paragraph.createRun();
                    RunUtil.position(run, WordUtil.ptToHalfPoints(11));
                    run.setText("Подп.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.6));
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.55));
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.48));
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.03));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.borderBottom(cell);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.0));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.13));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 3L);
                }

                { // 9
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.03));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("4");
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.36));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Зам");
                    run.setFontSize(10);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("АТБС.7-18");
                    run.setFontSize(10);
                }
                { // 12
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.01));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 4L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.68));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.gridSpan(cell, 2L);
                }
            }

            // 5
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.85));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(19.77));
                    CellUtil.borderLeft(cell);
                    CellUtil.gridSpan(cell, 14L);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.69));
                    CellUtil.borderLeft(cell);
                    CellUtil.gridSpan(cell, 5L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    XWPFRun run = paragraph.createRun();
                    run.setText("АТБС.01200.00001");
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.54));
                    CellUtil.borderLeft(cell);
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    XWPFRun run = paragraph.createRun();
                    run.setText("32");
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.68));
                    CellUtil.borderLeft(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    XWPFRun run = paragraph.createRun();
                    run.setText("1");
                }
            }

            // 6
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.07));
                {  // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9.62));
                    CellUtil.gridSpan(cell, 6L);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                {  // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.25));
                    CellUtil.gridSpan(cell, 3L);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    XWPFRun run = paragraph.createRun();
                    run.setText("Text");
                }
                {  // 3
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.86));
                    CellUtil.gridSpan(cell, 3L);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                {  // 4
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.35));
                    CellUtil.gridSpan(cell, 5L);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.6));
                    CellUtil.gridSpan(cell, 7L);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    XWPFRun run = paragraph.createRun();
                    run.setText("АТБС.55201.00003");
                }
            }

            // 7
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.32));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9.62));
                    CellUtil.gridSpan(cell, 6L);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(14.92));
                    CellUtil.gridSpan(cell, 14L);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    XWPFRun run = paragraph.createRun();
                    run.setText("Модули и платы электронные");
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.07));
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.04));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.03));
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                }
            }

            // 8
            {
                XWPFTableRow row = table.createRow();
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13.83));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.gridSpan(cell, 11L);
                    //
                    XWPFParagraph paragraph_1 = cell.getParagraphs().get(0);
                    paragraph_1.setStyle(tableContentId);
                    paragraph_1.setAlignment(ParagraphAlignment.CENTER);
                    //
                    XWPFParagraph paragraph_2 = cell.addParagraph();
                    paragraph_2.setStyle(tableContentId);
                    paragraph_2.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_2_1 = paragraph_2.createRun();
                    run_2_1.setText("“СОГЛАСОВАНО”");
                    //
                    XWPFParagraph paragraph_3 = cell.addParagraph();
                    paragraph_3.setStyle(tableContentId);
                    paragraph_3.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_3_1 = paragraph_3.createRun();
                    run_3_1.setText("Старший инженер 477 ВП МО РФ");
                    //
                    XWPFParagraph paragraph_4 = cell.addParagraph();
                    paragraph_4.setStyle(tableContentId);
                    paragraph_4.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_4_1 = paragraph_4.createRun();
                    run_4_1.setText("А.В.Шелешнев");
                    //
                    XWPFParagraph paragraph_5 = cell.addParagraph();
                    paragraph_5.setStyle(tableContentId);
                    paragraph_5.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph_5, CTSpacingProperties.instance().after(0L));
                    XWPFRun run_5_1 = paragraph_5.createRun();
                    RunUtil.position(run_5_1, 20L);
                    run_5_1.setText("“____”_____________ 2018г");
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13.85));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.gridSpan(cell, 13L);
                    //
                    XWPFParagraph paragraph_1 = cell.getParagraphs().get(0);
                    paragraph_1.setStyle(tableContentId);
                    paragraph_1.setAlignment(ParagraphAlignment.CENTER);
                    //
                    XWPFParagraph paragraph_2 = cell.addParagraph();
                    paragraph_2.setStyle(tableContentId);
                    paragraph_2.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_2_1 = paragraph_2.createRun();
                    run_2_1.setText("“УТВЕРЖДАЮ”");
                    //
                    XWPFParagraph paragraph_3 = cell.addParagraph();
                    paragraph_3.setStyle(tableContentId);
                    paragraph_3.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_3_1 = paragraph_3.createRun();
                    run_3_1.setText("Главный технолог");
                    //
                    XWPFParagraph paragraph_4 = cell.addParagraph();
                    paragraph_4.setStyle(tableContentId);
                    paragraph_4.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_4_1 = paragraph_4.createRun();
                    run_4_1.setText("А.А. Руденко");
                    //
                    XWPFParagraph paragraph_5 = cell.addParagraph();
                    paragraph_5.setStyle(tableContentId);
                    paragraph_5.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph_5, CTSpacingProperties.instance().after(0L));
                    XWPFRun run_5_1 = paragraph_5.createRun();
                    RunUtil.position(run_5_1, 20L);
                    run_5_1.setText("“____”_____________ 2018г");
                }
            }

            // 9
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.AT_LEAST, WordUtil.cmToDXA(2.01));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(27.68));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.gridSpan(cell, 24L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    XWPFRun run = paragraph.createRun();
                    run.setText("КАРТА ТИПОВОГО ТЕХНОЛОГИЧЕСКОГО ПРОЦЕССА");
                }
            }

            // 10
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.AT_LEAST, WordUtil.cmToDXA(4.99));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(27.68));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.gridSpan(cell, 24L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("на автоматическую сборку");
                }
            }

            // 11
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.AT_LEAST, WordUtil.cmToDXA(2.59));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13.83));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 11L);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13.85));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 13L);
                    //
                    XWPFParagraph paragraph = cell.addParagraph();
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    XWPFRun run = paragraph.createRun();
                    run.setText("Начальник технологического отдела                                     И.В. Стельмах");
                }
            }

            // 12
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.85));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    XWPFRun run = paragraph.createRun();
                    run.setText("ТЛ");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.18));
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.gridSpan(cell, 23L);
                }
            }
        }

        // Стр.2

        // Пустая стока абзаца перед таблицей
        {
            XWPFParagraph paragraph = document.createParagraph();
            ParagraphUtil.runSize(paragraph, WordUtil.ptToHalfPoints(6));
            ParagraphUtil.runSizeCs(paragraph, WordUtil.ptToHalfPoints(6));
        }

        {
            // Настройки таблицы
            XWPFTable table = document.createTable();
            TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(28.18));
            TableUtil.leftIndent(table, WordUtil.cmToDXA(-0.23));
            TableUtil.cellAutoFit(table, Boolean.FALSE);
            TableUtil.cellMargin(table, null, WordUtil.cmToDXA(0.19), null, WordUtil.cmToDXA(0.19));

            // 1
            {
                XWPFTableRow row = table.getRow(0);
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(19.61));
                    CellUtil.gridSpan(cell, 29L);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.39));
                    CellUtil.gridSpan(cell, 15L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ГОСТ 3.1118-82                                            Форма 1");
                    run.setFontSize(10);
                }
            }

            // 2
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.38));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1); // так, потому что gridSpan работает со вставкой одной лишней ячейки
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.94));
                    CellUtil.gridSpan(cell, 5L);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 2L);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 9
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.27));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.23));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderRight(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.78));
                    CellUtil.gridSpan(cell, 4L);
                }

                { // 12
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.98));
                }
                { // 14
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.28));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 15
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 6L);
                }
                { // 16
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.13));
                    CellUtil.gridSpan(cell, 3L);
                }
            }

            // 3
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.38));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Дубл.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.94));
                    CellUtil.gridSpan(cell, 5L);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 2L);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 9
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.27));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.23));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.78));
                    CellUtil.gridSpan(cell, 4L);
                }

                { // 12
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.98));
                }
                { // 14
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.28));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 15
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 6L);
                }
                { // 16
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.13));
                    CellUtil.gridSpan(cell, 3L);
                }
            }

            // 4
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.38));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Взам.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.94));
                    CellUtil.gridSpan(cell, 5L);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 2L);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderRight(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 3L);
                }

                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 9
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.27));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.23));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.78));
                    CellUtil.gridSpan(cell, 4L);
                }

                { // 12
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.98));
                }
                { // 14
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.28));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 15
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 6L);
                }
                { // 16
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.13));
                    CellUtil.gridSpan(cell, 3L);
                }
            }

            // 5
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.38));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Подп.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.94));
                    CellUtil.gridSpan(cell, 5L);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 2L);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 9
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.27));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.23));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.78));
                    CellUtil.gridSpan(cell, 4L);
                }

                { // 12
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("4");
                    run.setFontSize(12);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.98));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Зам");
                    run.setFontSize(10);
                }
                { // 14
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.28));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("АТБС.7-18");
                    run.setFontSize(10);
                }
                { // 15
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 6L);
                }
                { // 16
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.13));
                    CellUtil.gridSpan(cell, 3L);
                }
            }

            // 6
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.85));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(19.61));
                    CellUtil.gridSpan(cell, 29L);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.75));
                    CellUtil.gridSpan(cell, 8L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("АТБС.01200.00001");
                    run.setFontSize(12);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.51));
                    CellUtil.gridSpan(cell, 4L);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.13));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("2");
                    run.setFontSize(11);
                }
            }

            // 7
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.37));
                    CellUtil.gridSpan(cell, 5L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Разраб.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.94));
                    CellUtil.gridSpan(cell, 7L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("М. В. Макарова");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.2));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Text");
                    run.setFontSize(11);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.8));
                    CellUtil.gridSpan(cell, 9L);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.25));
                    CellUtil.gridSpan(cell, 6L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.95));
                    CellUtil.gridSpan(cell, 10L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("АТБС.55201.00003");
                    run.setFontSize(12);
                }
            }

            // 8
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.37));
                    CellUtil.gridSpan(cell, 5L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Проверил");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.94));
                    CellUtil.gridSpan(cell, 7L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Т. Е. Тащанина");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.2));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.8));
                    CellUtil.gridSpan(cell, 9L);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.25));
                    CellUtil.gridSpan(cell, 6L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.95));
                    CellUtil.gridSpan(cell, 10L);
                }
            }

            // 9
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.37));
                    CellUtil.gridSpan(cell, 5L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Метролог");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.94));
                    CellUtil.gridSpan(cell, 7L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("В. К. Соколов");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.2));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.8));
                    CellUtil.gridSpan(cell, 9L);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.25));
                    CellUtil.gridSpan(cell, 6L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.95));
                    CellUtil.gridSpan(cell, 10L);
                }
            }

            // 10
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.37));
                    CellUtil.gridSpan(cell, 5L);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.94));
                    CellUtil.gridSpan(cell, 7L);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(15));
                    CellUtil.gridSpan(cell, 22L);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Модули и платы электронные");
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.2));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                }
            }

            // 11
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 99L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.37));
                    CellUtil.gridSpan(cell, 5L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Н. контр.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.94));
                    CellUtil.gridSpan(cell, 7L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Ю. В. Благов");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(15));
                    CellUtil.gridSpan(cell, 22L);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.2));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                }
            }

            // 12
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 99L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.89));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("А");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.14));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Цех");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.99));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Уч");
                    run.setFontSize(11);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.04));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("РМ");
                    run.setFontSize(11);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.41));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Опер");
                    run.setFontSize(11);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.46));
                    CellUtil.gridSpan(cell, 6L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Код, наименование операции");
                    run.setFontSize(11);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(15.07));
                    CellUtil.gridSpan(cell, 25L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Обозначение документа");
                    run.setFontSize(11);
                }
            }

            // 13
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 99L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.89));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Б");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(11.04));
                    CellUtil.gridSpan(cell, 15L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Код, наименование оборудования");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.18));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("СМ");
                    run.setFontSize(11);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.41));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Проф");
                    run.setFontSize(11);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.97));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Р");
                    run.setFontSize(11);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.18));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("УТ");
                    run.setFontSize(11);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.24));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("КР");
                    run.setFontSize(11);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.47));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("КОИД");
                    run.setFontSize(11);
                }
                { // 9
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.38));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ЕН");
                    run.setFontSize(11);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.23));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ОП");
                    run.setFontSize(11);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.23));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_1 = paragraph.createRun();
                    run_1.setText("К");
                    run_1.setFontSize(11);
                    XWPFRun run_2 = paragraph.createRun();
                    run_2.setText("шт");
                    run_2.setSubscript(VerticalAlign.SUBSCRIPT);
                    run_2.setFontSize(11);
                }
                { // 12
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.69));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_1 = paragraph.createRun();
                    run_1.setText("Т");
                    run_1.setFontSize(11);
                    XWPFRun run_2 = paragraph.createRun();
                    run_2.setText("пз");
                    run_2.setSubscript(VerticalAlign.SUBSCRIPT);
                    run_2.setFontSize(11);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.08));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_1 = paragraph.createRun();
                    run_1.setText("Т");
                    run_1.setFontSize(11);
                    XWPFRun run_2 = paragraph.createRun();
                    run_2.setText("шт");
                    run_2.setSubscript(VerticalAlign.SUBSCRIPT);
                    run_2.setFontSize(11);
                }
            }

            // 14
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 99L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.89));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("К/М");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(11.04));
                    CellUtil.gridSpan(cell, 15L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Наименование детали, сб. единицы или материала");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7.45));
                    CellUtil.gridSpan(cell, 12L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Обозначение, код");
                    run.setFontSize(11);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.38));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ОПП");
                    run.setFontSize(11);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.23));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ЕВ");
                    run.setFontSize(11);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.23));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ЕН");
                    run.setFontSize(11);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.69));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("КИ");
                    run.setFontSize(11);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.08));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Н. расх.");
                    run.setFontSize(11);
                }
            }

            // 15
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 99L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.89));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Р");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.11));
                    CellUtil.gridSpan(cell, 40L);
                }
            }

            // 16 - 31
            for (int rowNum = 1; rowNum <= 16; rowNum++) {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 99L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.94));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    if (rowNum == 1 || rowNum == 5) {
                        XWPFParagraph paragraph = cell.getParagraphs().get(0);
                        paragraph.setAlignment(ParagraphAlignment.CENTER);
                        XWPFRun run = paragraph.createRun();
                        run.setText("Р");
                        run.setFontSize(11);
                        run.setBold(true);
                    }
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.96));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("" + (rowNum < 10 ? "0" + rowNum : rowNum));
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.11));
                    CellUtil.gridSpan(cell, 40L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setFontSize(11);
                    run.setBold(Boolean.TRUE);
                    if (rowNum == 1) {
                        run.setText("При выполнении всех операций, предусмотренных данным КТТП, все оборудование должно быть заземлено, монтажные работы выполнять с");
                    } else if (rowNum == 2) {
                        run.setText("браслетом заземления.");
                    } else if (rowNum == 3) {
                        run.setText("Перемещение изделий между участками должно осуществляться только в специализированной таре (боксах).");
                    } else if (rowNum == 5) {
                        run.setText("При выполнении операций по сборке, монтажу и проверке, предусмотренных данным КТТП, на рабочем месте");
                    } else if (rowNum == 6) {
                        run.setText("должны находиться сборочный чертеж и спецификация на изготавливаемое изделие.");
                    } else if (rowNum == 8) {
                        run.setText("Внимание! В производственных помещениях, исключая участки термо-, вибро-тестирования, комплектации и ОТК:");
                    } else if (rowNum == 9) {
                        run.setText("температура рабочего помещения должна быть 22±2С°,относительная влажность воздуха- не более 60%.");
                    }
                }
            }

            /// 32
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 99L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.21));
                    CellUtil.gridSpan(cell, 5L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("КТТП");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(25.8));
                    CellUtil.gridSpan(cell, 39L);
                }
            }
        }

        // Стр.3

        // Пустая стока абзаца перед таблицей
        {
            XWPFParagraph paragraph = document.createParagraph();
            ParagraphUtil.runSize(paragraph, WordUtil.ptToHalfPoints(6));
            ParagraphUtil.runSizeCs(paragraph, WordUtil.ptToHalfPoints(6));
            XWPFParagraph paragraph_1 = document.createParagraph();
            ParagraphUtil.runSize(paragraph_1, WordUtil.ptToHalfPoints(6));
            ParagraphUtil.runSizeCs(paragraph_1, WordUtil.ptToHalfPoints(6));
        }

        {
            // Настройки таблицы
            XWPFTable table = document.createTable();
            TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(28.18));
            TableUtil.leftIndent(table, WordUtil.cmToDXA(-0.23));
            TableUtil.cellAutoFit(table, Boolean.FALSE);
            TableUtil.cellMargin(table, null, WordUtil.cmToDXA(0.19), null, WordUtil.cmToDXA(0.19));

            // 1
            {
                XWPFTableRow row = table.getRow(0);
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 98L);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(19.6));
                    CellUtil.gridSpan(cell, 31L);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.41));
                    CellUtil.gridSpan(cell, 12L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ГОСТ 3.1118-82                                            Форма 1б");
                    run.setFontSize(10);
                }
            }

            // 2
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 98L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.06));
                    CellUtil.gridSpan(cell, 4L);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.3));
                    CellUtil.gridSpan(cell, 5L);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.74));
                    CellUtil.gridSpan(cell, 3L);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.71));
                    CellUtil.gridSpan(cell, 4L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.29));
                    CellUtil.gridSpan(cell, 8L);
                }

                { // 9
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.91));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.06));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 12
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.03));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.16));
                    CellUtil.gridSpan(cell, 2L);
                }
            }

            // 3
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 98L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.06));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Дубл.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.3));
                    CellUtil.gridSpan(cell, 5L);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.74));
                    CellUtil.gridSpan(cell, 3L);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.71));
                    CellUtil.gridSpan(cell, 4L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.29));
                    CellUtil.gridSpan(cell, 8L);
                }

                { // 9
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.91));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.06));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 12
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.03));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.16));
                    CellUtil.gridSpan(cell, 2L);
                }
            }

            // 4
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 98L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.06));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Взам.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.3));
                    CellUtil.gridSpan(cell, 5L);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.74));
                    CellUtil.gridSpan(cell, 3L);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderRight(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.71));
                    CellUtil.gridSpan(cell, 4L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.29));
                    CellUtil.gridSpan(cell, 8L);
                }

                { // 9
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.91));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.06));
                    CellUtil.gridSpan(cell, 2L);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 12
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.03));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.16));
                    CellUtil.gridSpan(cell, 2L);
                }
            }

            // 5
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 98L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.06));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Подп.");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.3));
                    CellUtil.gridSpan(cell, 5L);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.74));
                    CellUtil.gridSpan(cell, 3L);
                }

                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell, nilBorderProperties);
                    CellUtil.borderBottom(cell, nilBorderProperties);
                    CellUtil.borderRight(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.71));
                    CellUtil.gridSpan(cell, 4L);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderLeft(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.75));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.29));
                    CellUtil.gridSpan(cell, 8L);
                }

                { // 9
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.91));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("4");
                    run.setFontSize(11);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.06));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Зам");
                    run.setFontSize(10);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("АТБС.7-18");
                    run.setFontSize(10);
                }
                { // 12
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.03));
                    CellUtil.gridSpan(cell, 3L);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.16));
                    CellUtil.gridSpan(cell, 2L);
                }
            }

            // 6
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridBefore(row, 1L);
                RowUtil.wBefore(row, TableWidthType.DXA, 98L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.85));
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(20.61));
                    CellUtil.gridSpan(cell, 34L);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.23));
                    CellUtil.gridSpan(cell, 7L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("АТБС.01200.00001");
                    run.setFontSize(12);
                }
                { // 2
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.16));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("3");
                    run.setFontSize(11);
                }
            }

            // 7 - 9
            {
                for (int rowNum = 7; rowNum <= 9; rowNum++) {
                    XWPFTableRow row = table.createRow();
                    RowUtil.gridBefore(row, 1L);
                    RowUtil.wBefore(row, TableWidthType.DXA, 98L);
                    RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                    STMerge.Enum vMergeVal = rowNum == 7 ? STMerge.RESTART : STMerge.CONTINUE;
                    { // 1
                        XWPFTableCell cell = row.getCell(0);
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.82));
                        CellUtil.verticalMerge(cell, vMergeVal);
                    }
                    { // 2
                        XWPFTableCell cell = row.getCell(1);
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.48));
                        CellUtil.gridSpan(cell, 4L);
                    }
                    { // 3
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                        CellUtil.gridSpan(cell, 2L);
                    }
                    { // 4
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                        CellUtil.gridSpan(cell, 4L);
                    }
                    { // 5
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                        CellUtil.gridSpan(cell, 3L);
                    }
                    { // 6
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                        CellUtil.gridSpan(cell, 2L);
                    }
                    { // 7
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    }
                    { // 8
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    }
                    { // 9
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                        CellUtil.gridSpan(cell, 6L);
                    }
                    { // 10
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.51));
                        CellUtil.gridSpan(cell, 2L);
                    }
                    { // 11
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.75));
                        CellUtil.gridSpan(cell, 2L);
                    }
                    { // 12
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.01));
                        CellUtil.gridSpan(cell, 9L);
                        CellUtil.verticalMerge(cell, vMergeVal);
                    }
                    { // 13
                        XWPFTableCell cell = row.createCell();
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                        CellUtil.unsetBorders(cell);
                        CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.94));
                        CellUtil.gridSpan(cell, 6L);
                        CellUtil.verticalMerge(cell, vMergeVal);
                        //
                        if (rowNum == 7) {
                            XWPFParagraph paragraph = cell.getParagraphs().get(0);
                            paragraph.setAlignment(ParagraphAlignment.CENTER);
                            XWPFRun run = paragraph.createRun();
                            run.setText("АТБС.55201.00003");
                            run.setFontSize(12);
                        }
                    }
                }
            }

            // 10
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 109L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.99));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("А");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.23));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Цех");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.99));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Уч");
                    run.setFontSize(11);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.04));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("РМ");
                    run.setFontSize(11);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.39));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Опер");
                    run.setFontSize(11);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.35));
                    CellUtil.gridSpan(cell, 9L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Код, наименование операции");
                    run.setFontSize(11);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(14.98));
                    CellUtil.gridSpan(cell, 20L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Обозначение документа");
                    run.setFontSize(11);
                }
            }

            // 11
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 109L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.99));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Б");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.99));
                    CellUtil.gridSpan(cell, 19L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Код, наименование оборудования");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.12));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("СМ");
                    run.setFontSize(11);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.38));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Проф");
                    run.setFontSize(11);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.87));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Р");
                    run.setFontSize(11);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.13));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("УТ");
                    run.setFontSize(11);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("КР");
                    run.setFontSize(11);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("КОИД");
                    run.setFontSize(11);
                }
                { // 9
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ЕН");
                    run.setFontSize(11);
                }
                { // 10
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ОП");
                    run.setFontSize(11);
                }
                { // 11
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_1 = paragraph.createRun();
                    run_1.setText("К");
                    run_1.setFontSize(11);
                    XWPFRun run_2 = paragraph.createRun();
                    run_2.setText("шт");
                    run_2.setSubscript(VerticalAlign.SUBSCRIPT);
                    run_2.setFontSize(11);
                }
                { // 12
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_1 = paragraph.createRun();
                    run_1.setText("Т");
                    run_1.setFontSize(11);
                    XWPFRun run_2 = paragraph.createRun();
                    run_2.setText("пз");
                    run_2.setSubscript(VerticalAlign.SUBSCRIPT);
                    run_2.setFontSize(11);
                }
                { // 13
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run_1 = paragraph.createRun();
                    run_1.setText("Т");
                    run_1.setFontSize(11);
                    XWPFRun run_2 = paragraph.createRun();
                    run_2.setText("шт");
                    run_2.setSubscript(VerticalAlign.SUBSCRIPT);
                    run_2.setFontSize(11);
                }
            }

            // 12
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 109L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.99));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("К/М");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.99));
                    CellUtil.gridSpan(cell, 19L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Наименование детали, сб. единицы или материала");
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7.25));
                    CellUtil.gridSpan(cell, 11L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Обозначение, код");
                    run.setFontSize(11);
                }
                { // 4
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ОПП");
                    run.setFontSize(11);
                }
                { // 5
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ЕВ");
                    run.setFontSize(11);
                }
                { // 6
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("ЕН");
                    run.setFontSize(11);
                }
                { // 7
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("КИ");
                    run.setFontSize(11);
                }
                { // 8
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Н. расх.");
                    run.setFontSize(11);
                }
            }

            // 13
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 109L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.99));
                    CellUtil.gridSpan(cell, 3L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("Р");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(25.99));
                    CellUtil.gridSpan(cell, 40L);
                }
            }

            // 14 - 29
            for (int rowNum = 1; rowNum <= 16; rowNum++) {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 109L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderRight(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setFontSize(11);
                    run.setBold(true);
                    if (rowNum == 1) {
                        run.setText("А");
                    } else if (rowNum == 3) {
                        run.setText("Б");
                    } else if (rowNum == 4) {
                        run.setText("М");
                    } else if (rowNum == 5) {
                        run.setText("О");
                    }
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.borderLeft(cell, nilBorderProperties);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("" + (rowNum < 10 ? "0" + rowNum : rowNum));
                    run.setFontSize(11);
                }
                { // 3
                    XWPFTableCell cell = row.createCell();
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(25.99));
                    CellUtil.gridSpan(cell, 40L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    XWPFRun run = paragraph.createRun();
                    run.setFontSize(11);
                    if (rowNum == 1) {
                        run.setBold(Boolean.TRUE);
                        run.setText("              17                  005            Подготовительная                         ИОТ№2");
                    } else if (rowNum == 2) {
                        run.setText("Изготовление штрихового кода изделия.");
                    } else if (rowNum == 3) {
                        run.setText("Рабочий стол \"Viking\", термотрансферный принтер BRADY ВВР11 или подобный, компьютер с базой «АСУ производства».");
                    } else if (rowNum == 4) {
                        run.setText("THT-46-727-10 этикетки 19.05х6.35мм BRADY");
                    } else if (rowNum == 5) {
                        run.setText("1. Сформировать спецификацию на подбор согласно базе «АСУ производства».");
                    } else if (rowNum == 6) {
                        run.setText("2. Распечатать QR-код изделия.");
                    } else if (rowNum == 7) {
                        run.setText("3. Передать на 15 участок.");
                    }
                }
            }

            /// 30
            {
                XWPFTableRow row = table.createRow();
                RowUtil.gridAfter(row, 1L);
                RowUtil.wAfter(row, TableWidthType.DXA, 109L);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                RowUtil.hAlign(row, STJc.CENTER);
                { // 1
                    XWPFTableCell cell = row.getCell(0);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.15));
                    CellUtil.gridSpan(cell, 4L);
                    //
                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                    XWPFRun run = paragraph.createRun();
                    run.setText("КТТП");
                    run.setFontSize(11);
                }
                { // 2
                    XWPFTableCell cell = row.getCell(1);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.unsetBorders(cell);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(25.83));
                    CellUtil.gridSpan(cell, 39L);
                }
            }
        }

        Paths.get("\\\\fileserver\\home\\OASU\\pakhunov_an\\Рабочий стол\\simpleTable2.docx").toFile().delete();
        OutputStream out2 = new FileOutputStream("\\\\fileserver\\home\\OASU\\pakhunov_an\\Рабочий стол\\simpleTable2.docx");
        document.write(out2);
        out2.close();
    }
}