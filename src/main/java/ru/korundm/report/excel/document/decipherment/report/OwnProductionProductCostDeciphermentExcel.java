package ru.korundm.report.excel.document.decipherment.report;

import eco.dao.EcoProductService;
import eco.entity.EcoProduct;
import kotlin.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFHeaderFooter;
import org.jetbrains.annotations.NotNull;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import ru.korundm.dao.ProductDeciphermentAttrValService;
import ru.korundm.dao.ProductDeciphermentService;
import ru.korundm.dto.decipherment.DeciphermentDataProduct;
import ru.korundm.entity.ProductDecipherment;
import ru.korundm.helper.AutowireHelper;
import ru.korundm.helper.manager.decipherment.CompositionManager;
import ru.korundm.report.excel.document.decipherment.DeciphermentExcel;
import ru.korundm.report.excel.helper.BaseCellStyleProperty;
import ru.korundm.report.excel.helper.BaseFontProperty;
import ru.korundm.report.excel.util.ExcelUtil;
import ru.korundm.util.CommonUtil;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static ru.korundm.enumeration.ProductDeciphermentAttr.HEAD_CONSTRUCT_DEPARTMENT;
import static ru.korundm.enumeration.ProductDeciphermentAttr.HEAD_PL_EC_DEPARTMENT;

/**
 * Класс для формирования документа "Расшифровка затрат на изделия собственного производства"
 * @author mazur_ea
 * Date:   30.10.2019
 */
public class OwnProductionProductCostDeciphermentExcel implements DeciphermentExcel {

    @Autowired
    private MessageSource messageSource; // источник для получения сообщений

    @Autowired
    private EcoProductService ecoProductService;

    @Autowired
    private ProductDeciphermentService deciphermentService;

    @Autowired
    private ProductDeciphermentAttrValService productDeciphermentAttrValService;

    @Autowired
    private CompositionManager manager;

    private final static int SHEET_CELL_WIDTH = 23; // ширина страницы в количестве ячеек (от нуля)
    private final static List<String> rowColumnNumberValueList = List.of( // список номеров столбцов
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13.1",
        "13.2", "14", "15", "16", "17", "18", "19", "20.1", "20.2", "21", "22"
    );

    private final Locale locale = LocaleContextHolder.getLocale();
    private int dataRowCount = 13; // счетчик строки с которой начинается заполнения данных по изделиям (фактически курсор)

    private final BaseFontProperty fontProperty = new BaseFontProperty(); // объект для работы со стилями шрифтов
    private final BaseCellStyleProperty cellStyleProperty = new BaseCellStyleProperty(); // объект для работы со стилями ячеек

    private String getMessage(String msgKey) {
        return messageSource.getMessage(msgKey, null, locale);
    }

    private String getMessage(String msgKey, Object... params) {
        return messageSource.getMessage(msgKey, params, locale);
    }

    @NotNull
    @Override
    public Workbook generate(long deciphermentId) throws IOException {
        // Инициализация данных
        AutowireHelper.autowire(this);
        ProductDecipherment decipherment = deciphermentService.read(deciphermentId);
        EcoProduct ecoProduct = ecoProductService.read(decipherment.getPeriod().getProduct().getId());
        // текущая excel-книга
        Workbook workbook = new XSSFWorkbook();

        var headEcoAttr = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT);
        var headEcoUser = headEcoAttr == null ? null :  headEcoAttr.getUser();
        var headEco = headEcoUser == null ? null : headEcoUser.getUserOfficialName();
        headEco = headEco == null ? "" : headEco;
        var headConstructAttr = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT);
        var headConstructUser = headConstructAttr == null ? null :  headConstructAttr.getUser();
        var headConstruct = headConstructUser == null ? null : headConstructUser.getUserOfficialName();
        headConstruct = headConstruct == null ? "" : headConstruct;

        // Отчетный период
        var periodYear = LocalDate.now().getYear() + "";

        // Создания страницы
        Sheet sheet = workbook.createSheet(getMessage("ownProductionProductCost.sheetName"));
        sheet.setZoom(70);

        // Настройки печати
        PrintSetup printSetup = sheet.getPrintSetup();
        // Разметка страницы -> Параметры страницы
        // Страница
        sheet.setFitToPage(true); // Разместить не более чем на
        printSetup.setFitWidth((short) 1);
        printSetup.setFitHeight((short) 0);
        printSetup.setLandscape(true); // альбомная ориентация
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE); // А4
        // Поля
        // Сдвиги
        sheet.setMargin(Sheet.TopMargin, CommonUtil.cmToInch(1.4));
        sheet.setMargin(Sheet.BottomMargin, CommonUtil.cmToInch(1.4));
        sheet.setMargin(Sheet.LeftMargin, CommonUtil.cmToInch(1.5));
        sheet.setMargin(Sheet.RightMargin, CommonUtil.cmToInch(1.5));
        sheet.setMargin(Sheet.HeaderMargin, CommonUtil.cmToInch(0.8));
        sheet.setMargin(Sheet.FooterMargin, CommonUtil.cmToInch(0.8));
        sheet.setHorizontallyCenter(true); // выравнивание по горизонтали
        // Колонтитулы
        sheet.getFooter().setCenter(getMessage("ownProductionProductCost.footer.print.page") + HeaderFooter.page()); // Добавление нижнего колонтитула
        if (workbook instanceof XSSFWorkbook) {
            // Опция выравнивания относительно полей страницы (доступна только для xlsx)
            ((XSSFHeaderFooter) sheet.getHeader()).getHeaderFooter().setAlignWithMargins(false);
            // Режим просмотра книг - страничный режим
            ((XSSFSheet) sheet).getCTWorksheet().getSheetViews().getSheetViewArray(0).setView(STSheetViewType.PAGE_BREAK_PREVIEW);
        }
        // Лист
        sheet.setRepeatingRows(new CellRangeAddress(11, 11, 0, SHEET_CELL_WIDTH)); // сквозные строки
        sheet.setAutobreaks(true); // автоматический разрыв по страницей при печати

        // Группировка столбцов
        sheet.groupColumn(2, 4);
        sheet.setColumnGroupCollapsed(2, Boolean.FALSE);
        sheet.groupColumn(16, SHEET_CELL_WIDTH);
        sheet.setColumnGroupCollapsed(16, Boolean.FALSE);

        // Ширина столбцов
        sheet.setColumnWidth(1, 9000); // наименование
        sheet.setColumnWidth(5, 3000); // наименование
        sheet.setColumnWidth(12, 5500); // первичные документы (номер и дата договора, протокола, счета, иное)
        sheet.setColumnWidth(13, 4000); // метод определения цены
        sheet.setColumnWidth(14, 5000); // наименование поставщика
        sheet.setColumnWidth(15, 4000); // инн поставщика
        sheet.setColumnWidth(20, 3500); // обосновывающие документы
        sheet.setColumnWidth(21, 3000); // метод определения цены
        sheet.setColumnWidth(22, 5000); // наименование поставщика
        sheet.setColumnWidth(23, 4000); // инн поставщика

        // Название формы
        {
            Cell cell = sheet.createRow(0).createCell(SHEET_CELL_WIDTH);
            cell.setCellValue(getMessage("ownProductionProductCost.header.formNumber"));

            // Установка данных шрифта
            fontProperty.setFontHeightInPoints((short) 12);
            cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
            cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cellStyleProperty.setVerticalAlignment(VerticalAlignment.CENTER);
            ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
            cellStyleProperty.reset();
            fontProperty.reset();
        }

        // Шапка расшифровки
        {
            fontProperty.setFontHeightInPoints((short) 12);
            fontProperty.setBold(Boolean.TRUE);
            cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cellStyleProperty.setVerticalAlignment(VerticalAlignment.CENTER);

            for (int rowNum = 2; rowNum < 5; rowNum++) {
                Row row = sheet.createRow(rowNum);
                Cell cell = row.createCell(0);
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cell.getColumnIndex(), SHEET_CELL_WIDTH));
                String value;
                if (rowNum == 2) {
                    value = getMessage("ownProductionProductCost.header.part1");
                } else if (rowNum == 3) {
                    value = getMessage("ownProductionProductCost.header.part2");
                } else {
                    fontProperty.setBold(Boolean.FALSE);
                    var dNumber = StringUtils.defaultString(ecoProduct.getDNumber(), "").trim();
                    var productName = ecoProduct.getFullName();
                    if (ecoProduct.getFullName().contains(dNumber) && !dNumber.isEmpty()) productName = ecoProduct.getFullName();
                    else if (!dNumber.isEmpty()) productName = ecoProduct.getFullName() + " " + dNumber + "ТУ";
                    value = getMessage("ownProductionProductCost.header.part3") + " " + productName;
                }
                cell.setCellValue(value);
                cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
                ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
            }
            cellStyleProperty.reset();
            fontProperty.reset();
        }

        // строка под шапкой
        {
            Row row = sheet.createRow(5);
            Cell cell = row.createCell(0);
            cell.setCellValue(getMessage("ownProductionProductCost.header.part4"));
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cell.getColumnIndex(), SHEET_CELL_WIDTH));

            fontProperty.setFontHeightInPoints((short) 10);
            fontProperty.setItalic(Boolean.TRUE);
            cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
            cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cellStyleProperty.setVerticalAlignment(VerticalAlignment.CENTER);
            ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
            cellStyleProperty.reset();
            fontProperty.reset();
        }

        // Этап
        {
            Row row = sheet.createRow(6);
            Cell cell = row.createCell(0);
            cell.setCellValue(getMessage("ownProductionProductCost.header.stage"));
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cell.getColumnIndex(), 1));

            fontProperty.setFontHeightInPoints((short) 10);
            cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
            ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
            cellStyleProperty.reset();
            fontProperty.reset();
        }

        // Строка под этапом
        sheet.createRow(7).setHeight((short) 150);

        // Шапка таблицы
        {
            // Создание ячеек и строк со стандартным стилем
            fontProperty.setFontHeightInPoints((short) 10);
            cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
            cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cellStyleProperty.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleProperty.setBorderTop(BorderStyle.THIN);
            cellStyleProperty.setBorderRight(BorderStyle.THIN);
            cellStyleProperty.setBorderBottom(BorderStyle.THIN);
            cellStyleProperty.setBorderLeft(BorderStyle.THIN);
            cellStyleProperty.setWrapText(Boolean.TRUE);
            for (int rowNum = 8; rowNum <= 10; rowNum++) {
                Row row = sheet.createRow(rowNum);
                // Ширина строк
                if (rowNum == 9 || rowNum == 10) {
                    row.setHeight((short) 1500);
                }
                for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
                    if (cellNum >= 2 && cellNum <= 4) {
                        cellStyleProperty.setRotation((short) 90);
                    } else {
                        cellStyleProperty.setRotation((short) 0);
                    }
                    ExcelUtil.INSTANCE.mergeApply(row.createCell(cellNum), cellStyleProperty);
                }
            }
            cellStyleProperty.reset();
            fontProperty.reset();

            // Слияние ячеек
            for (int cellNum = 0; cellNum <= 5; cellNum++) {
                sheet.addMergedRegion(new CellRangeAddress(8, 10, cellNum, cellNum));
            }
            sheet.addMergedRegion(new CellRangeAddress(8, 8, 6, 15));
            for (int cellNum = 6; cellNum <= 14; cellNum += 2) {
                sheet.addMergedRegion(new CellRangeAddress(9, 9, cellNum, cellNum + 1));
            }
            sheet.addMergedRegion(new CellRangeAddress(8, 10, 16, 16));
            sheet.addMergedRegion(new CellRangeAddress(8, 8, 17, SHEET_CELL_WIDTH));
            sheet.addMergedRegion(new CellRangeAddress(9, 10, 17, 17));
            sheet.addMergedRegion(new CellRangeAddress(9, 10, 18, 18));
            sheet.addMergedRegion(new CellRangeAddress(9, 10, 19, 19));
            sheet.addMergedRegion(new CellRangeAddress(9, 9, 20, 21));
            sheet.addMergedRegion(new CellRangeAddress(9, 9, 22, SHEET_CELL_WIDTH));

            // Установка значений
            Object[][] headerValues = {
                {8, 0, getMessage("ownProductionProductCost.header.column.number")}, // № п/п
                {8, 1, getMessage("ownProductionProductCost.header.column.name")}, // Наименование
                {8, 2, getMessage("ownProductionProductCost.header.column.codeOKP")}, // Код ОКП/ОКПД2
                {8, 3, getMessage("ownProductionProductCost.header.column.codeEKPS")}, // Код ЕКПС (при наличии)
                {8, 4, getMessage("ownProductionProductCost.header.column.FNN")}, // ФНН (при наличии)
                {8, 5, getMessage("ownProductionProductCost.header.column.uom")}, // Единица измерения
                {8, 6, getMessage("ownProductionProductCost.header.column.reportPeriod")}, // Отчетный период/период, предшествующий планируемому
                {9, 6, getMessage("ownProductionProductCost.header.column.consumptionRate")}, // расход на единицу продукции
                {9, 8, getMessage("ownProductionProductCost.header.column.umCostRate")}, // цена за единицу измерения (руб.)
                {9, 10, getMessage("ownProductionProductCost.header.column.expenses")}, // затраты (руб.)
                {9, 12, getMessage("ownProductionProductCost.header.column.priceJustification")}, // обоснование цены поставки
                {9, 14, getMessage("ownProductionProductCost.header.column.supplier")}, // организация-поставщик (подрядчик, исполнитель)
                {10, 6, getMessage("ownProductionProductCost.header.column.plan")}, // план
                {10, 7, getMessage("ownProductionProductCost.header.column.fact")}, // факт
                {10, 8, getMessage("ownProductionProductCost.header.column.plan")}, // план
                {10, 9, getMessage("ownProductionProductCost.header.column.fact")}, // факт
                {10, 10, getMessage("ownProductionProductCost.header.column.plan")}, // план
                {10, 11, getMessage("ownProductionProductCost.header.column.fact")}, // факт
                {10, 12, getMessage("ownProductionProductCost.header.column.srcDocuments")}, // первичные документы (номер и дата договора, протокола, счета, иное)
                {10, 13, getMessage("ownProductionProductCost.header.column.priceDefineMethod")}, // метод определения цены
                {10, 14, getMessage("ownProductionProductCost.header.column.supplierName")}, // наименование
                {10, 15, getMessage("ownProductionProductCost.header.column.supplierINN")}, // ИНН
                {8, 16, getMessage("ownProductionProductCost.header.column.applicablePriceIndex")}, // Применяемый индекс цен
                {8, 17, getMessage("ownProductionProductCost.header.column.planPeriod", periodYear)}, // Планируемый период (год _____)
                {9, 17, getMessage("ownProductionProductCost.header.column.normConsumptionRate")}, // норма расхода на единицу продукции
                {9, 18, getMessage("ownProductionProductCost.header.column.umCostRate")}, // цена за единицу измерения (руб.)
                {9, 19, getMessage("ownProductionProductCost.header.column.expenses")}, // затраты (руб.)
                {9, 20, getMessage("ownProductionProductCost.header.column.priceJustification")}, // обоснование цены поставки
                {9, 22, getMessage("ownProductionProductCost.header.column.supplier")}, // организация-поставщик (подрядчик, исполнитель)
                {10, 20, getMessage("ownProductionProductCost.header.column.justifyDocuments")}, // обосновывающие документы
                {10, 21, getMessage("ownProductionProductCost.header.column.priceDefineMethod")}, // метод определения цены
                {10, 22, getMessage("ownProductionProductCost.header.column.supplierName")}, // наименование
                {10, 23, getMessage("ownProductionProductCost.header.column.supplierINN")} // ИНН
            };
            for (var headerValue : headerValues) {
                sheet.getRow((int) headerValue[0]).getCell((int) headerValue[1]).setCellValue((String) headerValue[2]);
            }

            // Создание строки с номерами под шапкой
            fontProperty.setFontHeightInPoints((short) 10);
            cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
            cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
            cellStyleProperty.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleProperty.setBorderTop(BorderStyle.THIN);
            cellStyleProperty.setBorderRight(BorderStyle.THIN);
            cellStyleProperty.setBorderBottom(BorderStyle.THIN);
            cellStyleProperty.setBorderLeft(BorderStyle.THIN);

            Row headerNumberRow = sheet.createRow(11);
            for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
                Cell cell = headerNumberRow.createCell(cellNum);
                cell.setCellValue(rowColumnNumberValueList.get(cellNum));
                ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
            }
            sheet.setAutoFilter(new CellRangeAddress(11, 11, 0, SHEET_CELL_WIDTH));

            // Изделия собственного производства
            Row row12 = sheet.createRow(12);
            for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
                Cell cell = row12.createCell(cellNum);
                cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
                if (cellNum == 1) {
                    fontProperty.setFontHeightInPoints((short) 12);
                    cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT);
                    cell.setCellValue(getMessage("ownProductionProductCost.header.ISP"));
                } else if (cellNum == 2) {
                    fontProperty.setFontHeightInPoints((short) 8);
                } else {
                    fontProperty.setFontHeightInPoints((short) 9);
                }
                cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
                ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
            }
            cellStyleProperty.reset();
            fontProperty.reset();
        }

        // Данные по изделиям иерархии
        List<DeciphermentDataProduct> productList = manager.readOwnProductionProductData(decipherment);
        {
            int rowNumberCount = 1;
            for (var product : productList) {
                Row row = workbook.getSheetAt(0).createRow(dataRowCount++);
                for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
                    Cell cell = row.createCell(cellNum);
                    // Стандартные стили ячейки
                    fontProperty.setFontHeightInPoints((short) 9);
                    cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    cellStyleProperty.setVerticalAlignment(VerticalAlignment.CENTER);
                    cellStyleProperty.setWrapText(Boolean.FALSE);
                    cellStyleProperty.setBorderTop(BorderStyle.THIN);
                    cellStyleProperty.setBorderRight(BorderStyle.THIN);
                    cellStyleProperty.setBorderBottom(BorderStyle.THIN);
                    cellStyleProperty.setBorderLeft(BorderStyle.THIN);

                    if (cellNum == 0) { // №№
                        cell.setCellValue(rowNumberCount++);
                    } else if (cellNum == 1) { // Наименование
                        fontProperty.setFontHeightInPoints((short) 12);
                        cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.GENERAL);
                        cellStyleProperty.setWrapText(Boolean.TRUE);
                        cell.setCellValue(product.getName());
                    } else if (cellNum == 2) { // Код ОКП/ОКПД2
                        fontProperty.setFontHeightInPoints((short) 8);
                        cell.setCellValue("-");
                    } else if (cellNum == 5) { // единица измерения
                        cell.setCellValue(getMessage("ownProductionProductCost.body.thingUnitMeasure"));
                    } else if (cellNum >= 6 && cellNum <= 16) {
                        cell.setCellValue(getMessage("ownProductionProductCost.markUnknown"));
                    } else if (cellNum == 17) { // расход на единицу продукции
                        cell.setCellValue(product.getProductCount());
                    } else if (cellNum == 19) {
                        cellStyleProperty.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
                        cell.setCellFormula(MessageFormat.format("ROUND(R{0}*S{0},2)", row.getRowNum() + 1));
                    } else if (cellNum == 21) {
                        cell.setCellValue(getMessage("ownProductionProductCost.body.priceDefineMethod"));
                    }
                    cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
                    ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
                    cellStyleProperty.reset();
                    fontProperty.reset();
                }
            }
            cellStyleProperty.reset();
            fontProperty.reset();
        }

        // Строка итого
        fontProperty.setFontHeightInPoints((short) 10);
        fontProperty.setBold(Boolean.TRUE);
        cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
        cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
        cellStyleProperty.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleProperty.setBorderTop(BorderStyle.THIN);
        cellStyleProperty.setBorderRight(BorderStyle.THIN);
        cellStyleProperty.setBorderBottom(BorderStyle.THIN);
        cellStyleProperty.setBorderLeft(BorderStyle.THIN);
        Row totalRow = sheet.createRow(dataRowCount);
        for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
            Cell cell = totalRow.createCell(cellNum);
            if (cellNum == 1) {
                cell.setCellValue(getMessage("ownProductionProductCost.body.total"));
            } else if (cellNum == 19) {
                cellStyleProperty.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
                cell.setCellFormula("SUM(T" + (dataRowCount - productList.size()) + ":T" + totalRow.getRowNum() + ")");
            }
            ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
        }
        cellStyleProperty.reset();
        fontProperty.reset();

        // Окончание документа
        final int endDocStartRow = dataRowCount + 2;
        {
            sheet.addMergedRegion(new CellRangeAddress(endDocStartRow, endDocStartRow + 1, 2, 7));
            sheet.addMergedRegion(new CellRangeAddress(endDocStartRow, endDocStartRow + 1, 17, 22));

            // Массив сообщений
            Object[][] signMessages = {
                {endDocStartRow, List.of(
                    new Pair<>(2, getMessage("ownProductionProductCost.footer.position1")),
                    new Pair<>(17, getMessage("ownProductionProductCost.footer.position2"))
                )},
                {endDocStartRow + 2, List.of(
                    new Pair<>(2, "                                               " + headEco),
                    new Pair<>(17, "                                                    " + headConstruct)
                )},
                {endDocStartRow + 3, List.of(
                    new Pair<>(2, getMessage("ownProductionProductCost.footer.initialsDefinition")),
                    new Pair<>(17, getMessage("ownProductionProductCost.footer.initialsDefinition"))
                )},
                {endDocStartRow + 4, List.of(
                    new Pair<>(2, getMessage("ownProductionProductCost.footer.signDate", periodYear)),
                    new Pair<>(17, getMessage("ownProductionProductCost.footer.signDate", periodYear))
                )}
            };

            // Заполнение должностей и расшифровки подписей
            for (var signMessage : signMessages) {
                int rowNum = (int) signMessage[0];
                Row row = sheet.getRow(rowNum) == null ? sheet.createRow(rowNum) : sheet.getRow(rowNum);
                for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
                    cellStyleProperty.reset();
                    fontProperty.reset();
                    Cell cell = row.getCell(cellNum) == null ? row.createCell(cellNum) : row.getCell(cellNum);
                    if (rowNum == endDocStartRow + 2 && ((cellNum >= 2 && cellNum <= 7) || (cellNum >= 17 && cellNum <= 22))) {
                        cellStyleProperty.setBorderBottom(BorderStyle.THIN);
                    }
                    @SuppressWarnings("unchecked")
                    List<Pair<Integer, String>> list = (List<Pair<Integer, String>>) signMessage[1];
                    for (Pair<Integer, String> pair : list) {
                        if (pair.getFirst() == cellNum) {
                            fontProperty.setFontHeightInPoints((short) 10);
                            fontProperty.setItalic(rowNum - endDocStartRow == 3);
                            cellStyleProperty.setWrapText(rowNum - endDocStartRow == 0);
                            cell.setCellValue(pair.getSecond());
                        }
                    }
                    cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
                    ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
                }
            }
            cellStyleProperty.reset();
            fontProperty.reset();
        }

        // Область печати
        workbook.setPrintArea(0, 0, SHEET_CELL_WIDTH, 0, endDocStartRow + 4);

        return workbook;
    }
}