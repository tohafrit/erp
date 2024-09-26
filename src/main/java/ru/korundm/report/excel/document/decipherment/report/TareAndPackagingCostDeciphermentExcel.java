package ru.korundm.report.excel.document.decipherment.report;

import eco.dao.EcoProductService;
import eco.entity.EcoProduct;
import kotlin.Pair;
import org.apache.commons.collections4.CollectionUtils;
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
import ru.korundm.constant.BaseConstant;
import ru.korundm.dao.ProductDeciphermentAttrValService;
import ru.korundm.dao.ProductDeciphermentService;
import ru.korundm.dto.decipherment.DeciphermentDataInvoiceComponent;
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
import java.time.LocalDate;
import java.util.*;

import static ru.korundm.enumeration.ProductDeciphermentAttr.HEAD_CONSTRUCT_DEPARTMENT;
import static ru.korundm.enumeration.ProductDeciphermentAttr.HEAD_PL_EC_DEPARTMENT;

/**
 * Класс для формирования документа "Расшифровка затрат на тару и упаковку"
 * @author mazur_ea
 * Date:   30.10.2019
 */
public class TareAndPackagingCostDeciphermentExcel implements DeciphermentExcel {

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

    private final static int SHEET_CELL_WIDTH = 24; // ширина страницы в количестве ячеек (от нуля)
    private final static List<String> rowColumnNumberValueList = List.of( // список номеров столбцов
        "1", "", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13.1",
        "13.2", "14", "15", "16", "17", "18", "19", "20.1", "20.2", "21", "22"
    );

    private final Locale locale = LocaleContextHolder.getLocale();
    private Workbook workbook; // текущая excel-книга
    private int dataRowCount = 13; // счетчик строки с которой начинается заполнения данных по изделиям (фактически курсор)
    private final List<String> totalRangeList = new ArrayList<>(); // список диапазонов для построения формулы общего итого
    private List<DeciphermentDataInvoiceComponent> componentInvoiceList; // список накладных по компонентам

    private final BaseFontProperty fontProperty = new BaseFontProperty(); // объект для работы со стилями шрифтов
    private final BaseCellStyleProperty cellStyleProperty = new BaseCellStyleProperty(); // объект для работы со стилями ячеек

    private String getMessage(String msgKey) {
        return messageSource.getMessage(msgKey, null, locale);
    }

    private String getMessage(String msgKey, Object... params) {
        return messageSource.getMessage(msgKey, params, locale);
    }

    private void addTotalRange(int start, int end) {
        if (start > end) return;
        totalRangeList.add("U" + start + ":U" + end);
    }

    @NotNull
    @Override
    public Workbook generate(long deciphermentId) throws IOException {
        // Инициализация данных
        AutowireHelper.autowire(this);
        ProductDecipherment decipherment = deciphermentService.read(deciphermentId);
        EcoProduct ecoProduct = ecoProductService.read(decipherment.getPeriod().getProduct().getId());
        this.workbook = new XSSFWorkbook();
        this.componentInvoiceList = manager.getComponentInvoiceList(decipherment);

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
        Sheet sheet = workbook.createSheet(getMessage("tareAndPackagingCost.sheetName"));
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
        sheet.getFooter().setCenter(getMessage("tareAndPackagingCost.footer.print.page") + HeaderFooter.page()); // Добавление нижнего колонтитула
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
        sheet.groupColumn(1, 1);
        sheet.setColumnGroupCollapsed(1, Boolean.TRUE);
        sheet.groupColumn(4, 5);
        sheet.setColumnGroupCollapsed(4, Boolean.FALSE);
        sheet.groupColumn(17, SHEET_CELL_WIDTH);
        sheet.setColumnGroupCollapsed(17, Boolean.FALSE);

        // Ширина столбцов
        sheet.setColumnWidth(2, 12000); // наименование
        sheet.setColumnWidth(6, 3000); // наименование
        sheet.setColumnWidth(13, 5500); // первичные документы (номер и дата договора, протокола, счета, иное)
        sheet.setColumnWidth(14, 4000); // метод определения цены
        sheet.setColumnWidth(15, 5000); // наименование поставщика
        sheet.setColumnWidth(16, 4000); // инн поставщика
        sheet.setColumnWidth(21, 3500); // обосновывающие документы
        sheet.setColumnWidth(22, 3000); // метод определения цены
        sheet.setColumnWidth(23, 5000); // наименование поставщика
        sheet.setColumnWidth(24, 4000); // инн поставщика

        // Название формы
        {
            Cell cell = sheet.createRow(0).createCell(SHEET_CELL_WIDTH);
            cell.setCellValue(getMessage("tareAndPackagingCost.header.formNumber"));

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
                    value = getMessage("tareAndPackagingCost.header.part1");
                } else if (rowNum == 3) {
                    value = getMessage("tareAndPackagingCost.header.part2");
                } else {
                    fontProperty.setBold(Boolean.FALSE);
                    var dNumber = StringUtils.defaultString(ecoProduct.getDNumber(), "").trim();
                    var productName = ecoProduct.getFullName();
                    if (ecoProduct.getFullName().contains(dNumber) && !dNumber.isEmpty()) productName = ecoProduct.getFullName();
                    else if (!dNumber.isEmpty()) productName = ecoProduct.getFullName() + " " + dNumber + "ТУ";
                    value = getMessage("tareAndPackagingCost.header.part3") + " " + productName;
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
            cell.setCellValue(getMessage("tareAndPackagingCost.header.part4"));
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
            cell.setCellValue(getMessage("tareAndPackagingCost.header.stage"));
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), cell.getColumnIndex(), 2));

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
                    if (cellNum >= 3 && cellNum <= 5) {
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
            for (int cellNum = 0; cellNum <= 6; cellNum++) {
                sheet.addMergedRegion(new CellRangeAddress(8, 10, cellNum, cellNum));
            }
            sheet.addMergedRegion(new CellRangeAddress(8, 8, 7, 16));
            for (int cellNum = 7; cellNum <= 15; cellNum += 2) {
                sheet.addMergedRegion(new CellRangeAddress(9, 9, cellNum, cellNum + 1));
            }
            sheet.addMergedRegion(new CellRangeAddress(8, 10, 17, 17));
            sheet.addMergedRegion(new CellRangeAddress(8, 8, 18, SHEET_CELL_WIDTH));
            sheet.addMergedRegion(new CellRangeAddress(9, 10, 18, 18));
            sheet.addMergedRegion(new CellRangeAddress(9, 10, 19, 19));
            sheet.addMergedRegion(new CellRangeAddress(9, 10, 20, 20));
            sheet.addMergedRegion(new CellRangeAddress(9, 9, 21, 22));
            sheet.addMergedRegion(new CellRangeAddress(9, 9, 23, SHEET_CELL_WIDTH));

            // Установка значений
            Object[][] headerValues = {
                {8, 0, getMessage("tareAndPackagingCost.header.column.number")}, // № п/п
                {8, 1, getMessage("tareAndPackagingCost.header.column.code")}, // Код
                {8, 2, getMessage("tareAndPackagingCost.header.column.name")}, // Наименование
                {8, 3, getMessage("tareAndPackagingCost.header.column.codeOKP")}, // Код ОКП/ОКПД2
                {8, 4, getMessage("tareAndPackagingCost.header.column.codeEKPS")}, // Код ЕКПС (при наличии)
                {8, 5, getMessage("tareAndPackagingCost.header.column.FNN")}, // ФНН (при наличии)
                {8, 6, getMessage("tareAndPackagingCost.header.column.uom")}, // Единица измерения
                {8, 7, getMessage("tareAndPackagingCost.header.column.reportPeriod")}, // Отчетный период/период, предшествующий планируемому
                {9, 7, getMessage("tareAndPackagingCost.header.column.consumptionRate")}, // расход на единицу продукции
                {9, 9, getMessage("tareAndPackagingCost.header.column.umCostRate")}, // цена за единицу измерения (руб.)
                {9, 11, getMessage("tareAndPackagingCost.header.column.expenses")}, // затраты (руб.)
                {9, 13, getMessage("tareAndPackagingCost.header.column.priceJustification")}, // обоснование цены поставки
                {9, 15, getMessage("tareAndPackagingCost.header.column.supplier")}, // организация-поставщик (подрядчик, исполнитель)
                {10, 7, getMessage("tareAndPackagingCost.header.column.plan")}, // план
                {10, 8, getMessage("tareAndPackagingCost.header.column.fact")}, // факт
                {10, 9, getMessage("tareAndPackagingCost.header.column.plan")}, // план
                {10, 10, getMessage("tareAndPackagingCost.header.column.fact")}, // факт
                {10, 11, getMessage("tareAndPackagingCost.header.column.plan")}, // план
                {10, 12, getMessage("tareAndPackagingCost.header.column.fact")}, // факт
                {10, 13, getMessage("tareAndPackagingCost.header.column.srcDocuments")}, // первичные документы (номер и дата договора, протокола, счета, иное)
                {10, 14, getMessage("tareAndPackagingCost.header.column.priceDefineMethod")}, // метод определения цены
                {10, 15, getMessage("tareAndPackagingCost.header.column.supplierName")}, // наименование
                {10, 16, getMessage("tareAndPackagingCost.header.column.supplierINN")}, // ИНН
                {8, 17, getMessage("tareAndPackagingCost.header.column.applicablePriceIndex")}, // Применяемый индекс цен
                {8, 18, getMessage("tareAndPackagingCost.header.column.planPeriod", periodYear)}, // Планируемый период (год _____)
                {9, 18, getMessage("tareAndPackagingCost.header.column.normConsumptionRate")}, // норма расхода на единицу продукции
                {9, 19, getMessage("tareAndPackagingCost.header.column.umCostRate")}, // цена за единицу измерения (руб.)
                {9, 20, getMessage("tareAndPackagingCost.header.column.expenses")}, // затраты (руб.)
                {9, 21, getMessage("tareAndPackagingCost.header.column.priceJustification")}, // обоснование цены поставки
                {9, 23, getMessage("tareAndPackagingCost.header.column.supplier")}, // организация-поставщик (подрядчик, исполнитель)
                {10, 21, getMessage("tareAndPackagingCost.header.column.justifyDocuments")}, // обосновывающие документы
                {10, 22, getMessage("tareAndPackagingCost.header.column.priceDefineMethod")}, // метод определения цены
                {10, 23, getMessage("tareAndPackagingCost.header.column.supplierName")}, // наименование
                {10, 24, getMessage("tareAndPackagingCost.header.column.supplierINN")} // ИНН
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

            // Тара и упаковка
            Row row12 = sheet.createRow(12);
            for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
                Cell cell = row12.createCell(cellNum);
                cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
                if (cellNum == 2) {
                    fontProperty.setFontHeightInPoints((short) 12);
                    cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT);
                    cell.setCellValue(getMessage("tareAndPackagingCost.header.tareAndPackage"));
                } else if (cellNum == 3) {
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
        {
            compositionProductToRowData(manager.createRootDataProduct(decipherment));
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
        sheet.createRow(dataRowCount);
        for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
            Cell cell = sheet.getRow(dataRowCount).createCell(cellNum);
            if (cellNum == 2) {
                cell.setCellValue(getMessage("tareAndPackagingCost.body.total"));
            } else if (cellNum == 20 && !totalRangeList.isEmpty()) {
                cellStyleProperty.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
                cell.setCellFormula("SUM(" + String.join(",", totalRangeList) + ")");
            }
            ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
        }
        cellStyleProperty.reset();
        fontProperty.reset();

        // Окончание документа
        final int endDocStartRow = dataRowCount + 2;
        {
            sheet.addMergedRegion(new CellRangeAddress(endDocStartRow, endDocStartRow + 1, 3, 8));
            sheet.addMergedRegion(new CellRangeAddress(endDocStartRow, endDocStartRow + 1, 18, 23));

            // Массив сообщений
            Object[][] signMessages = {
                {endDocStartRow, List.of(
                    new Pair<>(3, getMessage("tareAndPackagingCost.footer.position1")),
                    new Pair<>(18, getMessage("tareAndPackagingCost.footer.position2"))
                )},
                {endDocStartRow + 2, List.of(
                    new Pair<>(3, "                                               " + headEco),
                    new Pair<>(18, "                                                    " + headConstruct)
                )},
                {endDocStartRow + 3, List.of(
                    new Pair<>(3, getMessage("tareAndPackagingCost.footer.initialsDefinition")),
                    new Pair<>(18, getMessage("tareAndPackagingCost.footer.initialsDefinition"))
                )},
                {endDocStartRow + 4, List.of(
                    new Pair<>(3, getMessage("tareAndPackagingCost.footer.signDate", periodYear)),
                    new Pair<>(18, getMessage("tareAndPackagingCost.footer.signDate", periodYear))
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
                    if (rowNum == endDocStartRow + 2 && ((cellNum >= 3 && cellNum <= 8) || (cellNum >= 18 && cellNum <= 22))) {
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

    /**
     * Метод формирования строки для изделия структуры состава
     * @param deciphermentDataProduct изделие состава {@link DeciphermentDataProduct}
     */
    private void compositionProductToRowData(DeciphermentDataProduct deciphermentDataProduct) {
        // Создание и заполнение строки изделия
        int productRowNumber = dataRowCount; // номер строки изделия
        if (deciphermentDataProduct.getUniqueNumber() != null) {
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
                    cell.setCellValue(generateRowNumber(deciphermentDataProduct.getParentProduct()));
                } else if (cellNum == 2) { // Наименование
                    fontProperty.setFontHeightInPoints((short) 12);
                    fontProperty.setBold(Boolean.TRUE);
                    cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.GENERAL);
                    cellStyleProperty.setWrapText(Boolean.TRUE);
                    cell.setCellValue(deciphermentDataProduct.getName() + " " +  getMessage("tareAndPackagingCost.body.inComposition"));
                } else if (cellNum == 3) { // Код ОКП/ОКПД2
                    cell.setCellValue("-");
                } else if (cellNum == 4 || cellNum == 5) {
                    cell.setCellValue(getMessage("tareAndPackagingCost.markUnknown"));
                } else if (cellNum == 6) { // единица измерения
                    cell.setCellValue(getMessage("tareAndPackagingCost.body.thingUnitMeasure"));
                } else if (cellNum == 18) { // расход на единицу продукции
                    fontProperty.setBold(Boolean.TRUE);
                    cell.setCellValue(deciphermentDataProduct.getProductCount());
                }
                cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
                ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
                cellStyleProperty.reset();
                fontProperty.reset();
            }
        }

        // Заполнение строк с компонентами изделия
        int startTotalRange = dataRowCount + 1; // Начало диапазона подсчета строк итого
        fillCompositionProductData(deciphermentDataProduct);
        int endTotalRange = dataRowCount; // Окончание диапазона подсчета строк итого

        // Выполняем подсчет итого внутри изделия по компонентам, если изделие в количестве более одного и состоит из компонентов
        if (deciphermentDataProduct.getUniqueNumber() != null && deciphermentDataProduct.getProductCount() > 1 && CollectionUtils.isNotEmpty(deciphermentDataProduct.getComponentList())) {
            for (int rowCount = 0; rowCount < 2; rowCount++) {
                Row row = workbook.getSheetAt(0).createRow(dataRowCount++);
                for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
                    fontProperty.setFontHeightInPoints((short) 9);
                    cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    cellStyleProperty.setVerticalAlignment(VerticalAlignment.CENTER);
                    cellStyleProperty.setBorderTop(BorderStyle.THIN);
                    cellStyleProperty.setBorderRight(BorderStyle.THIN);
                    cellStyleProperty.setBorderBottom(BorderStyle.THIN);
                    cellStyleProperty.setBorderLeft(BorderStyle.THIN);

                    Cell cell = row.createCell(cellNum);
                    if (cellNum == 2) {
                        fontProperty.setBold(Boolean.TRUE);
                        cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.LEFT);
                        if (rowCount == 0) {
                            cell.setCellValue(getMessage("tareAndPackagingCost.body.totalOneProduct", 1));
                        } else {
                            cell.setCellValue(getMessage("tareAndPackagingCost.body.totalManyProduct", deciphermentDataProduct.getProductCount()));
                        }
                    } else if (cellNum == 20) {
                        cellStyleProperty.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
                        if (rowCount == 0) {
                            cell.setCellFormula(String.format("SUM(U%d:U%d)", startTotalRange, endTotalRange));
                        } else {
                            cell.setCellFormula(String.format("S%d*U%d", productRowNumber + 1, row.getRowNum())); // 2 аргумент - предыдущая строка итого для 1ого изделия + 1
                            addTotalRange(row.getRowNum() + 1, row.getRowNum() + 1);
                        }
                    }
                    cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
                    ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
                    cellStyleProperty.reset();
                    fontProperty.reset();
                }
            }
        } else { // Иначе берем цену по каждому компоненту в изделии, так как изделие в единственном количестве
            addTotalRange(startTotalRange, endTotalRange);
        }

        // Построение строк для вложенных изделий
        deciphermentDataProduct.getSubProductList().forEach(this::compositionProductToRowData);
    }

    /**
     * Метод формирования строки для компонентов изделия
     * @param deciphermentDataProduct продукт состава {@link DeciphermentDataProduct}
     */
    private void fillCompositionProductData(DeciphermentDataProduct deciphermentDataProduct) {
        short floatDataFormat = workbook.createDataFormat().getFormat("#,##0.00");
        String groupName = null;
        for (var deciphermentDataComponent : deciphermentDataProduct.getComponentList()) {
            // Строка с группой компонента
            // Работает, если компоненты отсортированны по cell
            String formatComponentGroup = CompositionManager.formatGroupName(deciphermentDataComponent.getGroupName());
            if (!Objects.equals(groupName, formatComponentGroup)) {
                groupName = formatComponentGroup;
                cellStyleProperty.setVerticalAlignment(VerticalAlignment.CENTER);
                cellStyleProperty.setWrapText(Boolean.TRUE);
                cellStyleProperty.setBorderTop(BorderStyle.THIN);
                cellStyleProperty.setBorderRight(BorderStyle.THIN);
                cellStyleProperty.setBorderBottom(BorderStyle.THIN);
                cellStyleProperty.setBorderLeft(BorderStyle.THIN);
                Row row = workbook.getSheetAt(0).createRow(dataRowCount++);
                for (int cellNum = 0; cellNum <= SHEET_CELL_WIDTH; cellNum++) {
                    Cell cell = row.createCell(cellNum);
                    if (cellNum == 2) {
                        fontProperty.setFontHeightInPoints((short) 12);
                        cell.setCellValue(groupName + ":");
                    } else if (cellNum == 3) {
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
            // Строка компонента
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

                DeciphermentDataInvoiceComponent invoiceData = componentInvoiceList.stream().
                    filter(invoice -> Objects.equals(invoice.getComponentId(), deciphermentDataComponent.getComponentId())).findFirst().orElse(null);
                if (cellNum == 0) { // №№
                    cell.setCellValue(generateRowNumber(deciphermentDataProduct));
                } else if (cellNum == 1) { // Код
                    cell.setCellValue(deciphermentDataComponent.getCell());
                } else if (cellNum == 2) { // Наименование
                    fontProperty.setFontHeightInPoints((short) 12);
                    cellStyleProperty.setHorizontalAlignment(HorizontalAlignment.GENERAL);
                    cellStyleProperty.setWrapText(Boolean.TRUE);
                    cell.setCellValue(CompositionManager.formattedName(deciphermentDataComponent));
                } else if (cellNum == 3) { // Код ОКП/ОКПД2
                    fontProperty.setFontHeightInPoints((short) 8);
                    cell.setCellValue(deciphermentDataComponent.getOkpdCode());
                } else if (cellNum == 6) { // единица измерения
                    cell.setCellValue(StringUtils.defaultIfBlank(deciphermentDataComponent.getUnitMeasure(), StringUtils.EMPTY));
                } else if (cellNum == 18) { // расход на единицу продукции
                    cell.setCellValue(deciphermentDataComponent.getQuantity());
                } else if (cellNum == 19) { // цена
                    cellStyleProperty.setDataFormat(floatDataFormat);
                    cell.setCellValue(invoiceData != null ? invoiceData.getPrice() : 0);
                } else if (cellNum == 20) { // затраты
                    cellStyleProperty.setDataFormat(floatDataFormat);
                    cell.setCellFormula(String.format("ROUND(S%1$d*T%1$d,2)", row.getRowNum() + 1));
                } else if (cellNum == 21) { // накладная
                    cellStyleProperty.setWrapText(Boolean.TRUE);
                    if (invoiceData != null) {
                        cell.setCellValue(
                            getMessage("tareAndPackagingCost.body.invoice") + " "
                                + invoiceData.getName() + " "
                                + invoiceData.getDate().format(BaseConstant.INSTANCE.getDATE_FORMATTER())
                        );
                    }
                } else if (cellNum == 23) { // поставщик
                    cellStyleProperty.setWrapText(Boolean.TRUE);
                    if (invoiceData != null) cell.setCellValue(invoiceData.getSupplier());
                } else if (cellNum == 24) { // ИНН
                    if (invoiceData != null) cell.setCellValue(invoiceData.getInn());
                } else {
                    cell.setCellValue(getMessage("tareAndPackagingCost.markUnknown"));
                }
                cellStyleProperty.setFontIndex(ExcelUtil.INSTANCE.defineFontIndex(workbook, fontProperty));
                ExcelUtil.INSTANCE.mergeApply(cell, cellStyleProperty);
                cellStyleProperty.reset();
                fontProperty.reset();
            }
        }
    }

    /**
     * Метод иерархической генерации номера строки. Собирает строку номера увеличивая счетчик строки только для продукта состава
     * @param deciphermentDataProduct продукт состава {@link DeciphermentDataProduct}
     * @return номер строки
     */
    private String generateRowNumber(DeciphermentDataProduct deciphermentDataProduct) {
        if (deciphermentDataProduct != null) {
            List<String> rowNumberFormerList = new ArrayList<>();
            rowNumberFormerList.add(String.valueOf(deciphermentDataProduct.getRowCount()));
            deciphermentDataProduct.setRowCount(deciphermentDataProduct.getRowCount() + 1);
            deciphermentDataProduct = deciphermentDataProduct.getParentProduct();
            while (deciphermentDataProduct != null) {
                rowNumberFormerList.add(String.valueOf(deciphermentDataProduct.getRowCount() - 1));
                deciphermentDataProduct = deciphermentDataProduct.getParentProduct();
            }
            Collections.reverse(rowNumberFormerList);
            return String.join( ".", rowNumberFormerList);
        }
        return "";
    }
}