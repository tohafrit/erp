package ru.korundm.report.word.document.journal;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import ru.korundm.report.enumeration.FontName;
import ru.korundm.report.word.helper.CTBorderProperties;
import ru.korundm.report.word.helper.CTFontsProperties;
import ru.korundm.report.word.helper.CTSpacingProperties;
import ru.korundm.report.word.util.*;

/**
 * Создание документа "Журнал регистрации предъявлений"
 * @author mertsalova_y
 * Date:   13.03.2020
 */
public class PresentationJournal {

    public static void create() throws Exception {
        generate();
    }

    private static void generate() throws Exception {
        XWPFDocument document = new XWPFDocument();
        DocumentUtil.orientSize(document, STPageOrientation.PORTRAIT, WordUtil.cmToDXA(21), WordUtil.cmToDXA(29.7));
        // Отступы документа
        long valueRL = WordUtil.cmToDXA(2.0), valueTB = WordUtil.cmToDXA(0.4), valueHF = WordUtil.cmToDXA(1.25);

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

        //Стр.1
        {
            XWPFTable table = document.createTable();
            TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(17));
            TableUtil.leftIndent(table, WordUtil.cmToDXA(0));
            TableUtil.cellAutoFit(table, Boolean.FALSE);
            TableUtil.unsetBorders(table);
            TableUtil.cellMargin(table, null, WordUtil.cmToDXA(0), null, WordUtil.cmToDXA(0));

            //1
            {
                XWPFTableRow row = table.getRow(0);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0));
                //1.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                }
            }
            {
                XWPFTableRow row = table.createRow();
                //1.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(11));
                    CellUtil.gridSpan(cell, 46L);
                }
                //1.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 22L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Первичное");
                    run.setFontSize(11);
                    XWPFRun run1 = paragraph.createRun();
                    run1.setText("                  ");
                    run1.setFontSize(11);
                    XWPFRun run2 = paragraph.createRun();
                    run2.setText("Вторичное");
                    run2.setFontSize(11);
                    run2.setStrikeThrough(Boolean.TRUE);
                }
            }
            //2
            {
                XWPFTableRow row = table.createRow();
                //2.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(11));
                    CellUtil.gridSpan(cell, 46L);
                }
                //2.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 22L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ненужное зачеркнуть");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
//        3
            {
                XWPFTableRow row = table.createRow();
                //3.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 25L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Предприятие изготовитель");
                    run.setFontSize(11);
                }
                //3.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 43L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Text");
                    run.setFontSize(11);
                }
            }
            //4
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.4));
                //4.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 25L);
                }
                //4.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 43L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("условное обозначение");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //5
            {
                XWPFTableRow row = table.createRow();
                //5.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.5));
                    CellUtil.gridSpan(cell, 34L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Начальник представительства заказчика");
                    run.setFontSize(11);
                }
                //5.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(185));
                    CellUtil.borderBottom(cell);
                    CellUtil.gridSpan(cell, 34L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("477 ВП МО РФ");
                    run.setFontSize(11);
                }
            }
            //6
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.4));
                //6.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.5));
                    CellUtil.gridSpan(cell, 34L);
                }
                //6.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.5));
                    CellUtil.gridSpan(cell, 34L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("условное обозначение");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //7
            {
                XWPFTableRow row = table.createRow();
                //7.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Линин С.А.");
                    run.setFontSize(11);
                }
            }
            //8
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.4));
                //8.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ф а м и л и я,           и н и ц и а л ы");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //9
            {
                XWPFTableRow row = table.createRow();
                //9.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10));
                    CellUtil.gridSpan(cell, 40L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("И З В Е Щ Е Н И Е  №");
                    run.setBold(true);
                    run.setFontSize(12);
                }
                //9.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 18L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("6/	83-401");
                    run.setFontSize(12);
                }
                //9.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 10L);
                }
            }
            //10
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //10.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("от  «");
                    run.setFontSize(12);
                }
                //10.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10");
                    run.setFontSize(12);
                }
                //10.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("» ");
                    run.setFontSize(12);
                }
                //10.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("января");
                    run.setFontSize(12);
                }
                //10.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7));
                    CellUtil.gridSpan(cell, 28L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2018" + " г");
                    run.setFontSize(12);
                }
            }
            //11
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //11.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("о предъявлении изделий на");
                    run.setFontSize(11);
                }
                //11.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("приёмку");
                    run.setFontSize(11);
                }
            }
            //12
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.3));
                //12.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                }
                //12.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("п р и ё м о-с д а т о ч н ы е    и с п ы т а н и я");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //13
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.5));
                //13.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //14
            {
                XWPFTableRow row = table.createRow();
                //14.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и  (и л и) п р и ё м к а");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //15
            {
                XWPFTableRow row = table.createRow();
                //15.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Настоящим извещением предъявляются изделия");
                    run.setFontSize(11);
                }
                //15.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("БТ83-401");
                    run.setFontSize(11);
                }
            }
            //16
            {
                XWPFTableRow row = table.createRow();
                //16.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);
                }
                //16.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("н а и м е н о в а н и е   и л и   и н д е к с");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //17
            {
                XWPFTableRow row = table.createRow();
                //17.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10" + " комплект(а/ов)");
                    run.setFontSize(11);
                }
                //17.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав№");
                    run.setFontSize(11);
                }
                //17.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 18L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
            }
            //18
            {
                XWPFTableRow row = table.createRow();
                //18.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и з д е л и я,  к о л и ч е с т в о   п а р т и й,  к о м п л е к т о в,  ш т у к");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //18.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                }
            }
            //19
            {
                XWPFTableRow row = table.createRow();
                //19.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("083401600610, 0618, 0619, 0620, 0621, 0622, 0623, 0624, 0625, 0626");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
            }
            {
                XWPFTableRow row = table.createRow();
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                }
            }
            //20
            {
                XWPFTableRow row = table.createRow();
                //20.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 14L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("по договору №");

                    run.setFontSize(11);
                }
                //20.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 14L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("1718187320162412208001271/208/КБ-ПП-17");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
                //20.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("от  «");
                    run.setFontSize(11);
                }
                //20.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("25");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
                //20.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("»");
                    run.setFontSize(11);
                }
                //20.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("мая");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
                //20.7
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2017 г");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
                //20.8
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Позиция№");
                    run.setFontSize(11);
                }
                //20.9
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
            }
            //21
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //21.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 14L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Спецификация№");
                    run.setFontSize(11);
                }
                //21.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ведомости поставки");
                    run.setFontSize(11);
                }
                //21.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7.5));
                    CellUtil.gridSpan(cell, 30L);
                }
            }
            //22
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //22.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(1));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.BOTH);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Указанные изделия проверены и приняты ОТК, полностью соответствуют");
                    run.setFontSize(11);
                }
            }
            //23
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //23.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 22L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("требованиям действующей");
                    run.setFontSize(11);
                }
                //23.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 22L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("конструкторской");
                    run.setFontSize(11);
                }
                //23.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("документации, подтверждены");
                    run.setFontSize(11);
                }
            }
            //24
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //24.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(11.5));
                    CellUtil.gridSpan(cell, 46L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("предыдущими периодическими испытаниями (акт(отчёт) №");
                    run.setFontSize(11);
                }
                //24.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 22L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("А83-00/17");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
            }
            //25
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //25.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("от  «");
                    run.setFontSize(11);
                }
                //25.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("28");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
                //25.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("»");
                    run.setFontSize(11);
                }
                //25.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("апреля");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
                //25.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2017" + "г");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
                //25.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("признаны годными для сдачи представителю заказчика.");
                    run.setFontSize(11);
                }
            }
            //26
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //26.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Предъявляемые изделия укомплектованы в соответствии с требованиями");
                    run.setFontSize(11);
                }
            }
            //27
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //26.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ЮКСУ.469535.003 ТУ");
                    run.setFontSize(11);
                }
            }
            //28
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //28.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("н а и м е н о в а н и е   и л и   ш и ф р   д о к у м е н т а ц и и");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //28
            {
                XWPFTableRow row = table.createRow();

                //28.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(1.5));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Предъявляются документы:");
                    run.setFontSize(10);
                }
            }
            //29
            {
                XWPFTableRow row = table.createRow();
                //29.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.5));
                    CellUtil.gridSpan(cell, 34L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(1.5));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("1.Формуляр(ы) (паспорт)");
                    run.setFontSize(10);
                }
                //29.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.5));
                    CellUtil.gridSpan(cell, 34L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("3. Заявление о соответствии");
                    run.setFontSize(10);
                }
            }
            //30
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //30.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 32L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(1.5));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2.Протоколы испытания ОТК№");
                    run.setFontSize(11);
                }
                //30.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("0626");
                    run.setItalic(true);
                    run.setFontSize(11);
                }
                //30.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("от  «");
                    run.setFontSize(11);
                }
                //30.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10");
                    run.setFontSize(11);
                }
                //30.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.25));
                    CellUtil.gridSpan(cell, 1L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("»");
                    run.setFontSize(11);
                }
                //30.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("января");
                    run.setFontSize(11);
                }
                //30.7
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.25));
                    CellUtil.gridSpan(cell, 9L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2018 " + "г");
                    run.setFontSize(11);
                }
            }
            //31
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //31.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("на");
                    run.setFontSize(11);
                }
                //31.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
                //31.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(16));
                    CellUtil.gridSpan(cell, 64L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("листах");
                    run.setFontSize(11);
                }
            }
            //32
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //32.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Акт№");
                    run.setFontSize(11);
                }
                //32.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("XXX");
                    run.setFontSize(11);
                }
                //32.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("от  «");
                    run.setFontSize(11);
                }
                //32.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10");
                    run.setFontSize(11);
                }
                //32.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.25));
                    CellUtil.gridSpan(cell, 1L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("»");
                    run.setFontSize(11);
                }
                //32.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("января");
                    run.setFontSize(11);
                }
                //32.7
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2018 " + "г");
                    run.setFontSize(11);
                }
                //32.8
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.25));
                    CellUtil.gridSpan(cell, 25L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("об анализе и устранении дефектов и");
                    run.setFontSize(11);
                }
            }
            //33
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //33.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("повторной проверке ОТК изделий возвращенных представителем заказчика");
                    run.setFontSize(11);
                }
            }
            //34
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //34.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.5));
                    CellUtil.gridSpan(cell, 34L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("(в случае повторного предъявления)");
                    run.setFontSize(11);
                }
                //34.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.5));
                    CellUtil.gridSpan(cell, 34L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
            }
            //35
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //35.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7.5));
                    CellUtil.gridSpan(cell, 30L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Руководитель предприятия");
                    run.setFontSize(11);
                }
                //35.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9.5));
                    CellUtil.gridSpan(cell, 38L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("                  В.Ю.Царахов");
                    run.setFontSize(11);
                }
            }
            //36
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.3));
                //36.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7.5));
                    CellUtil.gridSpan(cell, 30L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
                //36.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 14L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("п о д п и с ь");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //36.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и н и ц и а л ы,  ф а м и л и я");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //37
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //37.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5));
                    CellUtil.gridSpan(cell, 20L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Начальник ОТК");
                    run.setFontSize(11);
                }
                //35.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(12));
                    CellUtil.gridSpan(cell, 48L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("                                         А.В. Князев");
                    run.setFontSize(11);
                }
            }
            //38
            {
                XWPFTableRow row = table.createRow();
                //38.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5));
                    CellUtil.gridSpan(cell, 20L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
                //38.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("п о д п и с ь");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //38.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и н и ц и а л ы,  ф а м и л и я");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //39
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));

                //39.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Поступило в представительство заказчика");
                    run.setFontSize(11);
                }
            }
            //40
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //40.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
                //40.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ч.");
                    run.setFontSize(11);
                }
                //40.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
                //40.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Мин.      «");
                    run.setFontSize(11);
                }
                //40.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
                //40.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("»");
                    run.setFontSize(11);
                }
                //40.7
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
                //40.8
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2018г");
                    run.setFontSize(11);
                }
            }
            //41
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //41.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);
                }
                //41.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(11));
                    CellUtil.gridSpan(cell, 44L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
            }
            //42
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //42.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Решение представительства заказчика о проведении испытаний и (или) приемки:");
                    run.setFontSize(11);
                }
            }
            //43
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //43.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7));
                    CellUtil.gridSpan(cell, 28L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("а) испытания (приемку) провести");
                    run.setFontSize(11);
                }
                //43.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10));
                    CellUtil.gridSpan(cell, 40L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
            }
            //44
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.3));
                //44.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7));
                    CellUtil.gridSpan(cell, 28L);
                }
                //44.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10));
                    CellUtil.gridSpan(cell, 40L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ф а м и л и я,  и н и ц и а л ы");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //45
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //45.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("б) отклонить от приемки ");
                    run.setFontSize(11);
                }
                //45.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(11));
                    CellUtil.gridSpan(cell, 44L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
            }
            //46
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.3));
                //46.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);
                }
                //46.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(11));
                    CellUtil.gridSpan(cell, 44L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("причины отклонения от приемки");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //47
            {
                XWPFTableRow row = table.createRow();
                //47.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7.5));
                    CellUtil.gridSpan(cell, 30L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Начальник представительства\n" + "заказчика");
                    run.setFontSize(11);
                }
                //47.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 14L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(11);
                }
                //47.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.25));
                    CellUtil.gridSpan(cell, 1L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                //47.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.75));
                    CellUtil.gridSpan(cell, 23L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("А.В.Шелешнев");
                    run.setFontSize(11);
                }
            }
            //48
            {
                XWPFTableRow row = table.createRow();

                //48.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7.5));
                    CellUtil.gridSpan(cell, 30L);
                }
                //47.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 14L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("п о д п и с ь");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //47.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.25));
                    CellUtil.gridSpan(cell, 1L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                }
                //47.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.75));
                    CellUtil.gridSpan(cell, 23L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText(" и н и ц и а л ы,  ф а м и л и я");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
        }
        {
            XWPFParagraph paragraph = document.createParagraph();
            ParagraphUtil.runSize(paragraph, WordUtil.ptToHalfPoints(6));
            ParagraphUtil.runSizeCs(paragraph, WordUtil.ptToHalfPoints(6));
            paragraph.setPageBreak(true);
        }
        //Стр.2
        {
            XWPFTable table = document.createTable();
            TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(17));
            TableUtil.leftIndent(table, WordUtil.cmToDXA(0));
            TableUtil.cellAutoFit(table, Boolean.FALSE);
            TableUtil.unsetBorders(table);
            TableUtil.cellMargin(table, null, WordUtil.cmToDXA(0), null, WordUtil.cmToDXA(0));
            //1
            {
                XWPFTableRow row = table.getRow(0);
                //1.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ЗАКЛЮЧЕНИЕ");
                    run.setBold(true);
                    run.setFontSize(12);
                }
            }
            //2
            {
                XWPFTableRow row = table.createRow();
                //2.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ПРЕДСТАВИТЕЛЬСТВА ЗАКАЗЧИКА");
                    run.setBold(true);
                    run.setFontSize(12);
                }
            }
            //3
            {
                XWPFTableRow row = table.createRow();
                //3.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                }
            }
            //4
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //4.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10));
                    CellUtil.gridSpan(cell, 40L);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(1));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Из общего количества предъявленных изделий");
                    run.setFontSize(12);
                }
                //4.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7));
                    CellUtil.gridSpan(cell, 28L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("БТ83-401");
                    run.setItalic(true);
                    run.setFontSize(14);
                }
            }
            //5
            {
                XWPFTableRow row = table.createRow();
                //5.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10));
                    CellUtil.gridSpan(cell, 40L);
                }
                //5.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7));
                    CellUtil.gridSpan(cell, 28L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("н а и м е н ов а н и е   и л и   и н д е к с  и з д е л и я ");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(14);
                }
            }
            //6
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //6.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(12));
                    CellUtil.gridSpan(cell, 48L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("по настоящему извещению: соответствуют требованиям");
                    run.setFontSize(12);
                }
                //6.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5));
                    CellUtil.gridSpan(cell, 20L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ЮКСУ.469535.003ТУ");
                    run.setFontSize(12);
                }
            }
            //7
            {
                XWPFTableRow row = table.createRow();
                //7.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(12));
                    CellUtil.gridSpan(cell, 48L);
                }
                //7.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5));
                    CellUtil.gridSpan(cell, 20L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("о б о з н а ч е н и е   д о к у м е н т а");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(14);
                }
            }
            //8
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //8.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10 комплект(а/ов)");
                    run.setFontSize(12);
                }
                //8.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 6L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав.№");
                    run.setFontSize(12);
                }
                //8.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5));
                    CellUtil.gridSpan(cell, 20L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //9
            {
                XWPFTableRow row = table.createRow();
                //9.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("к о л и ч е с т в о   п а р т и й,  к о м п л е к т о в,  ш т у к");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
                //9.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                }
            }
            //10
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //10.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("083401600610, 0618, 0619, 0620, 0621, 0622, 0623, 0624, 0625, 0626");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
            }
            //11
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //11.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 10L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("возвращено");
                    run.setFontSize(12);
                }
                //11.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 32L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //11.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав№");
                    run.setFontSize(12);
                }
                //11.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5));
                    CellUtil.gridSpan(cell, 20L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //12
            {
                XWPFTableRow row = table.createRow();
                //12.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 10L);
                }
                //12.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 32L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("к о л и ч е с т в о   п а р т и й,  к о м п л е к т о в,  ш т у к");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
                //12.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                }
            }
            //13
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //13.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.25));
                    CellUtil.gridSpan(cell, 21L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("в том числе забраковано");
                    run.setFontSize(12);
                }
                //13.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.75));
                    CellUtil.gridSpan(cell, 23L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //13.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав№");
                    run.setFontSize(12);
                }
                //13.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 18L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //14
            {
                XWPFTableRow row = table.createRow();
                //14.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.75));
                    CellUtil.gridSpan(cell, 19L);
                }
                //14.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.25));
                    CellUtil.gridSpan(cell, 25L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("к о л и ч е с т в о   п а р т и й,  к о м п л е к т о в,  ш т у к");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //14.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);
                }
            }
            //15
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //15.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(1));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Причина возврата (забракования)");
                    run.setFontSize(12);
                }
            }
            //16
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //16.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //17
            {
                XWPFTableRow row = table.createRow();
                //17.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("о б о з н а ч е н и е   д о к у м е н т о в   и   н о м е р а   п у н к т ов   д о к у м е н т о в,");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //18
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //18.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //19
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //19.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("к о т о р ы м   н е   с о о т в е т с т в у е т   и з д е л и е");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
            }
            //20
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //20.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(12.5));
                    CellUtil.gridSpan(cell, 50L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(1));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Основание: протокол приёмо-сдаточных испытаний №");
                    run.setFontSize(12);
                }
                //20.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 18L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("0626");
                    run.setFontSize(12);
                }
            }
            //21
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //21.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.25));
                    CellUtil.gridSpan(cell, 5L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.BOTH);

                    XWPFRun run = paragraph.createRun();
                    run.setText("от   «");
                    run.setFontSize(12);
                }
                //21.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10");
                    run.setFontSize(12);
                }
                //21.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.25));
                    CellUtil.gridSpan(cell, 1L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("»");
                    run.setFontSize(12);
                }
                //21.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 10L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("января");
                    run.setFontSize(12);
                }
                //21.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(12));
                    CellUtil.gridSpan(cell, 48L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2018г");
                    run.setFontSize(12);
                }
            }
            //22
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //22.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 22L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Представитель заказчика");
                    run.setFontSize(12);
                }
                //22.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //22.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //22.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //22.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //22.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 18L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //23
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.3));
                //23.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 22L);
                }
                //23.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("в р е м я ,  д а т а  ");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //23.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //23.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("п о д п и с ь");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //23.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //23.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 18L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и н и ц и а л ы,  ф а м и л и я ");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //24
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //24.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 14L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Изделия");
                    run.setFontSize(12);
                }
                //24.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5));
                    CellUtil.gridSpan(cell, 20L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("БТ83-401");
                    run.setFontSize(12);
                }
                //24.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8.5));
                    CellUtil.gridSpan(cell, 34L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10 комплект(а/ов)");
                    run.setFontSize(12);
                }
            }
            //25
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //25.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав №");
                    run.setFontSize(12);
                }
                //25.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(15));
                    CellUtil.gridSpan(cell, 60L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("083401600610, 0618, 0619, 0620, 0621, 0622, 0623, 0624, 0625, 0626");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
            }
            //26
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.7));
                //26.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 32L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Как соответствующие требованиям");
                    run.setFontSize(12);
                }
                //26.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9));
                    CellUtil.gridSpan(cell, 36L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ЮКСУ.469535.003ТУ");
                    run.setFontSize(12);
                }
            }
            //27
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.4));
                //27.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 32L);
                }
                //27.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9));
                    CellUtil.gridSpan(cell, 36L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("о б о з н а ч е н и е   д о к у м е н т а");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //28
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //28.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9));
                    CellUtil.gridSpan(cell, 36L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Считать принятыми и подлежащими передаче на ");
                    run.setFontSize(12);
                }
                //28.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 32L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ответственное хранение");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
            }
            //29
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.4));
                //29.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9));
                    CellUtil.gridSpan(cell, 36L);
                }
                //29.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 32L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("п е р и о д и ч е с к и е   и с п ы т а н и я,");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //30
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //30.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Text");
                    run.setFontSize(12);
                }
            }
            //31
            {
                XWPFTableRow row = table.createRow();
                //31.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("о т в е т с т в е н н о е   х р а н е н и  е,  о т г р у з к а");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //32
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.2));
                //32.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.2), null, WordUtil.cmToDXA(1.5));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Начальник представи- тельства заказчика");
                    run.setFontSize(12);
                }
                //32.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 10L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.BOTTOM);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //32.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //32.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.BOTTOM);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //32.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //32.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 16L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.BOTTOM);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("А.В.Шелешнев");
                    run.setFontSize(11);
                }
            }
            //33
            {
                XWPFTableRow row = table.createRow();
                //33.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                }
                //33.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 10L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("д а т а");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //33.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //33.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("п о д п и с ь");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //33.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //33.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 16L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и н и ц и а л ы,  ф а м и л и я ");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //34
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //34.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 10L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Ознакомлен");
                    run.setFontSize(12);
                }
                //34.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Начальник ОТК");
                    run.setFontSize(12);
                }
                //34.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //34.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //34.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //34.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 18L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.BOTTOM);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("А.В. Князев");
                    run.setFontSize(12);
                }
            }
            //35
            {
                XWPFTableRow row = table.createRow();
                //35.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.5));
                    CellUtil.gridSpan(cell, 10L);
                }
                //35.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("д о л ж н о с т ь   п р е д с т а в и т е л я   О Т К ");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //35.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //35.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("п о д п и с ь");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //35.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //35.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 18L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и н и ц и а л ы,  ф а м и л и я ");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //36
            {
                XWPFTableRow row = table.createRow();
                //36.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Изделия");
                    run.setFontSize(12);
                }
                //36.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("БТ83-401");
                    run.setFontSize(12);
                }
                //36.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5));
                    CellUtil.gridSpan(cell, 20L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10 комплект(а/ов)");
                    run.setFontSize(12);
                }
                //36.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав №");
                    run.setFontSize(12);
                }
                //36.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 16L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("083401600610, 0618, 0619, 0620, 0621, 0622, 0623, 0624, 0625, 0626");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
            }
            //37
            {
                XWPFTableRow row = table.createRow();
                //37.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 12L);
                }
                //37.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 32L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("к о л и ч е с т в о   п а р т и й,  к о м п л е к т о в  ,ш т у к");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
                //37.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 24L);
                }
            }
            //38
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //38.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Приняты");
                    run.setFontSize(12);
                }
                //38.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("«");
                    run.setFontSize(12);
                }
                //38.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //38.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("»");
                    run.setFontSize(12);
                }
                //38.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //38.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1.5));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2018г.");
                    run.setFontSize(12);
                }
                //38.7
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9.5));
                    CellUtil.gridSpan(cell, 38L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("на ответственное хранение");
                    run.setFontSize(12);
                }
            }
            //39
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //39.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(7));
                    CellUtil.gridSpan(cell, 24L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Предприятием-изготовителем до");
                    run.setFontSize(12);
                }
                //39.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10));
                    CellUtil.gridSpan(cell, 40L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("отгрузки по разнарядке заказчика");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
            }
            //40
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //40.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                }
                //40.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10.5));
                    CellUtil.gridSpan(cell, 42L);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(1));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("о т г р у з к а   п о   р а з н а р я д к е   з а к а з ч и к а");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //41
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.6));
                //41.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.75));
                    CellUtil.gridSpan(cell, 11L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Начальник");
                    run.setFontSize(12);
                }
                //41.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.25));
                    CellUtil.gridSpan(cell, 13L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("склада");
                    run.setFontSize(12);
                }
                //41.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //41.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 14L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
                //41.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //41.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("М.С. Зиньковская");
                    run.setFontSize(12);
                }
            }
            //42
            {
                XWPFTableRow row = table.createRow();
                //42.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2.75));
                    CellUtil.gridSpan(cell, 11L);
                }
                //42.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.25));
                    CellUtil.gridSpan(cell, 13L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("о т д е л   с б ы т а,   с к л ад  ");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //42.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //42.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 14L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("п о д п и с ь");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(11);
                }
                //42.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 2L);
                }
                //42.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 26L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и н и ц и а л ы,  ф а м и л и я ");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //43
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.6));
                //43.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(17));
                    CellUtil.gridSpan(cell, 68L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("М.П.");
                    run.setFontSize(14);
                }
            }
        }
        DocumentUtil.changeOrientation(document, STPageOrientation.LANDSCAPE, WordUtil.cmToDXA(29.7), WordUtil.cmToDXA(21), valueTB, valueRL, valueTB, valueRL);
        {
            XWPFParagraph paragraph = document.createParagraph();
            ParagraphUtil.runSize(paragraph, WordUtil.ptToHalfPoints(6));
            ParagraphUtil.runSizeCs(paragraph, WordUtil.ptToHalfPoints(6));
            paragraph.setPageBreak(true);
        }
        {
            XWPFParagraph paragraph = document.createParagraph();
        }
        {
            //Стр.3
            XWPFTable table = document.createTable();
            TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
            TableUtil.leftIndent(table, WordUtil.cmToDXA(0));
            TableUtil.cellAutoFit(table, Boolean.FALSE);
            TableUtil.unsetBorders(table);
            TableUtil.cellMargin(table, null, WordUtil.cmToDXA(0), null, WordUtil.cmToDXA(0));
            //0
            {
                XWPFTableRow row = table.getRow(0);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.1));
                //0.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                }
            }
            //1
            {
                XWPFTableRow row = table.createRow();
                //1.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13));
                    CellUtil.gridSpan(cell, 26L);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.3), null, null);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Протокол№");
                    run.setFontSize(14);
                }
                //1.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("0626");
                    run.setItalic(true);
                    run.setFontSize(13);
                }
                //1.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(11.5));
                    CellUtil.gridSpan(cell, 23L);
                }
            }
            //2
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1));
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                }
            }
            //3
            {
                XWPFTableRow row = table.createRow();
                //3.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 7L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Приёмки модуля");
                    run.setFontSize(12);
                }
                //3.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 7L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("БТ83-401");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
                //3.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 7L);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.3), WordUtil.cmToDXA(0.1), null);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав.№");
                    run.setFontSize(12);
                }
                //3.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(10));
                    CellUtil.gridSpan(cell, 20L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("083401600610, 0618, 0619, 0620, 0621, 0622, 0623, 0624, 0625, 0626");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
                //3.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 12L);
                }
            }
            //4
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1));
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                }
            }
            //5
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.5));
                //5.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderLeft(cell);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("№ п/п");
                    run.setFontSize(14);
                }
                //5.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 7L);
                    CellUtil.verticalMerge(cell, STMerge.RESTART);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Наименование параметра");
                    run.setFontSize(14);
                }
                //5.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6));
                    CellUtil.gridSpan(cell, 12L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.15), null, WordUtil.cmToDXA(0.15));
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Номера пунктов по ЮКСУ.460000.004ТУ");
                    run.setFontSize(14);
                }
                //5.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 16L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Результаты проверки ОТК");
                    run.setFontSize(14);
                }
                //5.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(8));
                    CellUtil.gridSpan(cell, 16L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Результаты проверки ПЗ");
                    run.setFontSize(14);
                }
            }
            //6
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.5));
                //6.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell);
                }
                //6.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 7L);
                    CellUtil.verticalMerge(cell, STMerge.CONTINUE);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell);
                }
                //6.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.15), null, WordUtil.cmToDXA(0.15));
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("технические требования");
                    run.setFontSize(14);
                }
                //6.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.15), null, WordUtil.cmToDXA(0.15));
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("методы испытания");
                    run.setFontSize(14);
                }
                //6.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Соответствие ТУ");
                    run.setFontSize(14);
                }
                //6.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("дата");
                    run.setFontSize(14);
                }
                //6.7
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("подпись");
                    run.setFontSize(14);
                }
                //6.8
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Соответствие ТУ");
                    run.setFontSize(14);
                }
                //6.9
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("дата");
                    run.setFontSize(14);
                }
                //6.10
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderTop(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("подпись");
                    run.setFontSize(14);
                }
            }
            //7
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(2));
                //7.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);

                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("1.");
                    run.setFontSize(14);
                }
                //7.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 7L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(0.1));
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Проверка состава и качества ЭД");
                    run.setFontSize(14);
                }
                //7.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("1.2.5");
                    run.setFontSize(14);
                }
                //7.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.15), null, WordUtil.cmToDXA(0.15));
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("4.5.1");
                    run.setFontSize(14);
                }
                //7.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFParagraph paragraph1 = cell.addParagraph();
                    paragraph1.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph1, CTSpacingProperties.instance().after(0L));
                    paragraph1.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("соответствует");
                    run.setFontSize(14);
                    XWPFRun run1 = paragraph1.createRun();
                    run1.setText("не соответствует");
                    run1.setStrikeThrough(Boolean.TRUE);
                    run1.setFontSize(14);
                }
                //7.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10.01.18");
                    run.setFontSize(14);
                }
                //7.7
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(14);
                }
                //7.8
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFParagraph paragraph1 = cell.addParagraph();
                    paragraph1.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph1, CTSpacingProperties.instance().after(0L));
                    paragraph1.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("соответствует");
                    run.setFontSize(14);
                    XWPFRun run1 = paragraph1.createRun();
                    run1.setText("не соответствует");
                    run1.setStrikeThrough(Boolean.TRUE);
                    run1.setFontSize(14);
                }
                //7.9
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("  .01.18");
                    run.setFontSize(14);
                }
                //7.10
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(14);
                }
            }
            //8
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(2));
                //8.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("2.");
                    run.setFontSize(14);
                }
                //8.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 7L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(0.1));

                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Проверка комплектности  и маркировки");
                    run.setFontSize(14);
                }
                //8.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("1.6.1");
                    run.setFontSize(14);

                    XWPFParagraph paragraph1 = cell.addParagraph();
                    paragraph1.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph1, CTSpacingProperties.instance().after(0L));
                    paragraph1.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run1 = paragraph1.createRun();
                    run1.setText("1.7.2");
                    run1.setFontSize(14);

                    XWPFParagraph paragraph2 = cell.addParagraph();
                    paragraph2.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph2, CTSpacingProperties.instance().after(0L));
                    paragraph2.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run2 = paragraph2.createRun();
                    run2.setText("1.1.7");
                    run2.setFontSize(14);
                }
                //8.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.15), null, WordUtil.cmToDXA(0.15));
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("4.5.2");
                    run.setFontSize(14);
                }
                //8.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFParagraph paragraph1 = cell.addParagraph();
                    paragraph1.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph1, CTSpacingProperties.instance().after(0L));
                    paragraph1.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("соответствует");
                    run.setFontSize(14);
                    XWPFRun run1 = paragraph1.createRun();
                    run1.setText("не соответствует");
                    run1.setStrikeThrough(Boolean.TRUE);
                    run1.setFontSize(14);
                }
                //8.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("10.01.18");
                    run.setFontSize(14);
                }
                //8.7
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(14);
                }
                //8.8
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFParagraph paragraph1 = cell.addParagraph();
                    paragraph1.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph1, CTSpacingProperties.instance().after(0L));
                    paragraph1.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("соответствует");
                    run.setFontSize(14);
                    XWPFRun run1 = paragraph1.createRun();
                    run1.setText("не соответствует");
                    run1.setStrikeThrough(Boolean.TRUE);
                    run1.setFontSize(14);
                }
                //8.9
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("  .01.18");
                    run.setFontSize(14);
                }
                //8.10
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(14);
                }
            }
            //9
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.5));
                //9.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(1));
                    CellUtil.gridSpan(cell, 2L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("3.");
                    run.setFontSize(14);
                }
                //9.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3.5));
                    CellUtil.gridSpan(cell, 7L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(0.1));
                    CellUtil.borderBottom(cell);
                    CellUtil.borderRight(cell);
                    CellUtil.borderLeft(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Проверка упаковки*");
                    run.setFontSize(14);
                }
                //9.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("1.8.1");
                    run.setFontSize(14);
                }
                //9.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.15), null, WordUtil.cmToDXA(0.15));
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("4.5.3");
                    run.setFontSize(14);
                }
                //9.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFParagraph paragraph1 = cell.addParagraph();
                    paragraph1.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph1, CTSpacingProperties.instance().after(0L));
                    paragraph1.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("соответствует");
                    run.setFontSize(14);
                    XWPFRun run1 = paragraph1.createRun();
                    run1.setText("не соответствует");
                    run1.setStrikeThrough(Boolean.TRUE);
                    run1.setFontSize(14);
                }
                //9.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("  .01.18");
                    run.setFontSize(14);
                }
                //9.7
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(14);
                }
                //9.8
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4));
                    CellUtil.gridSpan(cell, 8L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFParagraph paragraph1 = cell.addParagraph();
                    paragraph1.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph1, CTSpacingProperties.instance().after(0L));
                    paragraph1.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("соответствует");
                    run.setFontSize(14);
                    XWPFRun run1 = paragraph1.createRun();
                    run1.setText("не соответствует");
                    run1.setStrikeThrough(Boolean.TRUE);
                    run1.setFontSize(14);
                }
                //9.9
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("  .01.18");
                    run.setFontSize(14);
                }
                //9.10
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.borderRight(cell);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(14);
                }
            }
            //10
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.7));
                //10.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(1));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("* Проверка производится совместно ОТК и ПЗ");
                    run.setFontSize(14);
                }
            }
            //11
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.7));
                //11.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("В графах «соответствие ТУ» проверки ОТК и проверки ПЗ ненужное зачеркнуть");
                    run.setFontSize(14);
                }
            }
        }
        {
            XWPFParagraph paragraph = document.createParagraph();
            ParagraphUtil.runSize(paragraph, WordUtil.ptToHalfPoints(6));
            ParagraphUtil.runSizeCs(paragraph, WordUtil.ptToHalfPoints(6));
            paragraph.setPageBreak(true);
        }
        // Стр.4
        {
            XWPFTable table = document.createTable();
            TableUtil.width(table, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
            TableUtil.leftIndent(table, WordUtil.cmToDXA(0));
            TableUtil.cellAutoFit(table, Boolean.FALSE);
            TableUtil.unsetBorders(table);
            TableUtil.cellMargin(table, null, WordUtil.cmToDXA(0), null, WordUtil.cmToDXA(0));
            //0
            {
                XWPFTableRow row = table.getRow(0);
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.5));
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                }
            }
            //1
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.2));
                //1.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ЗАКЛЮЧЕНИЕ  ОТК");
                    run.setFontSize(16);
                }
            }
            //2
            {
                XWPFTableRow row = table.createRow();
                //2.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.3), null, null);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Модуль");
                    run.setFontSize(12);
                }
                //2.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("БТ83-401");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
                //2.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав №");
                    run.setFontSize(12);
                }
                //2.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);
                    CellUtil.borderBottom(cell);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(0.1));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("083401600610, 0618, 0619, 0620, 0621, 0622, 0623, 0624, 0625, 0626");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
                //2.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13));
                    CellUtil.gridSpan(cell, 26L);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(0.2));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("соответствует");
                    run.setFontSize(12);

                    XWPFRun run1 = paragraph.createRun();
                    run1.setText(", ");
                    run1.setFontSize(12);

                    XWPFRun run2 = paragraph.createRun();
                    run2.setText("не соответствует");
                    run2.setStrikeThrough(true);
                    run2.setFontSize(12);

                    XWPFRun run3 = paragraph.createRun();
                    run3.setText(" требованиям ЮКСУ.460000.004ТУ");
                    run3.setFontSize(12);
                }
            }
            //3
            {
                XWPFTableRow row = table.createRow();
                //3.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13.5));
                    CellUtil.gridSpan(cell, 27L);
                }
                //3.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13));
                    CellUtil.gridSpan(cell, 26L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("(н е н у ж н о е   з а ч е р к н у т ь)");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //4
            {
                XWPFTableRow row = table.createRow();
                //4.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и может быть предъявлен представителю заказчика для приёмки.");
                    run.setFontSize(12);
                }
            }
            //5
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.7));
                //5.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13.5));
                    CellUtil.gridSpan(cell, 27L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Заключение ОТК о причинах возврата изделия цеху - изготовителю");
                    run.setFontSize(12);
                }
                //5.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13));
                    CellUtil.gridSpan(cell, 26L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //6
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.7));
                //6.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                    CellUtil.borderBottom(cell);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //7
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.2));
                //7.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("(к о н к р е т н ы е   п р и ч и н ы, о б о з н а ч е н и е   д о к у м е н т о в   и   н о м е р о в   п у н к т о  в ТУ , к о т о р ы м   н е с о о т в е т с т в у е т и  з д е л и е)");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //8
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.7));
                //8.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.5), null, null);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Представитель ОТК");
                    run.setFontSize(12);
                }
                //8.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Грибанов М.В.");
                    run.setItalic(true);
                    run.setFontSize(13);
                }
                //8.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 1L);
                }
                //8.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("  .01.18");
                    run.setFontSize(12);
                }
                //8.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 1L);
                }
                //8.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9));
                    CellUtil.gridSpan(cell, 18L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //9
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.7));
                //9.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);
                }
                //9.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("(Ф.И.О.)");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(13);
                }
                //9.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 1L);
                }
                //9.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText(" (дата)");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
                //9.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 1L);
                }
                //9.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9));
                    CellUtil.gridSpan(cell, 18L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("(подпись)");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
            //10
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1.2));
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                }
            }
            //11
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(1));
                //11.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("ЗАКЛЮЧЕНИЕ  ПЗ");
                    run.setFontSize(16);
                }
            }
            //12
            {
                XWPFTableRow row = table.createRow();
                //12.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.3), null, null);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Модуль");
                    run.setFontSize(12);
                }
                //12.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("БТ83-401");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
                //12.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав №");
                    run.setFontSize(12);
                }
                //12.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);
                    CellUtil.borderBottom(cell);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(0.1));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("083401600610, 0618, 0619, 0620, 0621, 0622, 0623, 0624, 0625, 0626");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
                //12.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13));
                    CellUtil.gridSpan(cell, 26L);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(0.2));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("соответствует требованиям ЮКСУ.460000.004ТУ");
                    run.setFontSize(12);
                }
            }
            //13
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.9));
                //13.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, WordUtil.cmToDXA(0.2), null);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("принят, годен для использования по назначению и подлежит сдаче на ответственное  хранение предприятию-изготовителю.");
                    run.setFontSize(12);
                }
            }
            //14
            {
                XWPFTableRow row = table.createRow();
                //14.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.3), null, null);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Модуль");
                    run.setFontSize(12);
                }
                //14.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(3));
                    CellUtil.gridSpan(cell, 6L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("БТ83-401");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
                //14.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(2));
                    CellUtil.gridSpan(cell, 4L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("зав №");
                    run.setFontSize(12);
                }
                //14.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);
                    CellUtil.borderBottom(cell);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(0.1));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("083401600610, 0618, 0619, 0620, 0621, 0622, 0623, 0624, 0625, 0626");
                    run.setItalic(true);
                    run.setFontSize(12);
                }
                //14.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(13));
                    CellUtil.gridSpan(cell, 26L);
                    CellUtil.margin(cell, null, null, null, WordUtil.cmToDXA(0.2));

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("не соответствует требованиям ЮКСУ.460000.004ТУ");
                    run.setFontSize(12);
                }

            }
            //15
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.9));
                //15.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(26.5));
                    CellUtil.gridSpan(cell, 53L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, null, WordUtil.cmToDXA(0.2), null);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.LEFT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("и подлежит возврату ОТК.");
                    run.setFontSize(12);
                }
            }
            //16
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.7));
                //16.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 13L);
                    cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                    CellUtil.margin(cell, null, WordUtil.cmToDXA(0.5), null, null);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.RIGHT);

                    XWPFRun run = paragraph.createRun();
                    run.setText("Представитель заказчика");
                    run.setFontSize(12);
                }
                //16.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 9L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setItalic(true);
                    run.setFontSize(13);
                }
                //16.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 1L);
                }
                //16.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("  .01.18");
                    run.setFontSize(12);
                }
                //16.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 1L);
                }
                //16.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9));
                    CellUtil.gridSpan(cell, 18L);
                    CellUtil.borderBottom(cell);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("");
                    run.setFontSize(12);
                }
            }
            //17
            {
                XWPFTableRow row = table.createRow();
                RowUtil.height(row, STHeightRule.EXACT, WordUtil.cmToDXA(0.7));
                //17.1
                {
                    XWPFTableCell cell = row.getCell(0);
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(6.5));
                    CellUtil.gridSpan(cell, 13L);
                }
                //17.2
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(4.5));
                    CellUtil.gridSpan(cell, 9L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("(Ф.И.О.)");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(13);
                }
                //17.3
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 1L);
                }
                //17.4
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(5.5));
                    CellUtil.gridSpan(cell, 11L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText(" (дата)");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
                //17.5
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(0.5));
                    CellUtil.gridSpan(cell, 1L);
                }
                //17.6
                {
                    XWPFTableCell cell = row.createCell();
                    CellUtil.width(cell, TableWidthType.DXA, WordUtil.cmToDXA(9));
                    CellUtil.gridSpan(cell, 18L);

                    XWPFParagraph paragraph = cell.getParagraphs().get(0);
                    paragraph.setStyle(tableContentId);
                    ParagraphUtil.spacing(paragraph, CTSpacingProperties.instance().after(0L));
                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                    XWPFRun run = paragraph.createRun();
                    run.setText("(подпись)");
                    run.setSubscript(VerticalAlign.SUPERSCRIPT);
                    run.setFontSize(12);
                }
            }
        }
//        Paths.get("\\\\fileserver\\home\\OASU\\pakhunov_an\\Рабочий стол\\simpleTable.docx").toFile().delete();
//        OutputStream out2 = new FileOutputStream("\\\\fileserver\\home\\OASU\\pakhunov_an\\Рабочий стол\\simpleTable.docx");
//        document.write(out2);
//        out2.close();
    }
}
