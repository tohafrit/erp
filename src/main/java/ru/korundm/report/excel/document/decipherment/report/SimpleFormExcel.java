package ru.korundm.report.excel.document.decipherment.report;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.korundm.dao.ProductDeciphermentAttrValService;
import ru.korundm.dao.ProductDeciphermentService;
import ru.korundm.helper.AutowireHelper;
import ru.korundm.report.excel.document.decipherment.DeciphermentExcel;
import ru.korundm.util.KtCommonUtil;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static ru.korundm.enumeration.ProductDeciphermentAttr.*;
/**
 * Класс для формирования форм: 5, 7, 7.1, 8, 14, 14.1, 16, 17, 19
 *
 * @author mertsalova_uv
 * Date:   29.09.2021
 */

public class SimpleFormExcel implements DeciphermentExcel {

    @Autowired
    private ProductDeciphermentService deciphermentService;

    @Autowired
    private ProductDeciphermentAttrValService productDeciphermentAttrValService;

    @NotNull
    @Override
    public Workbook generate(long deciphermentId) throws IOException {
        // Инициализация данных
        AutowireHelper.autowire(this);
        XSSFWorkbook workbook = null;

        var decipherment = deciphermentService.read(deciphermentId);
        if (decipherment == null) {
            return workbook;
        }

        var path = "";

        switch (decipherment.getType().getEnum()) {
            case FORM_5: path = "form5.xlsx"; break;
            case FORM_7: path = "form7.xlsx"; break;
            case FORM_7_1: path = "form7_1.xlsx"; break;
            case FORM_8: path = "form8.xlsx"; break;
            case FORM_10: path = "form10.xlsx"; break;
            case FORM_11: path = "form11.xlsx"; break;
            case FORM_12: path = "form12.xlsx"; break;
            case FORM_13: path = "form13.xlsx"; break;
            case FORM_14: path = "form14.xlsx"; break;
            case FORM_14_1: path = "form14_1.xlsx"; break;
            case FORM_16: path = "form16.xlsx"; break;
            case FORM_17: path = "form17.xlsx"; break;
            case FORM_19: path = "form19.xlsx"; break;
            case FORM_21: path = "form21.xlsx"; break;
            case FORM_22: path = "form22.xlsx"; break;
            case FORM_23: path = "form23.xlsx"; break;
            case EXPLANATION_NOTE: path = "explanationNote.xlsx"; break;
        }

        workbook = new XSSFWorkbook(KtCommonUtil.INSTANCE.sourceFile("blank" + File.separator + "excel", path));

        var period = decipherment.getPeriod();
        if (period == null) {
            return workbook;
        }

        var product = period.getProduct();
        if (product == null) {
            return workbook;
        }

        var planPeriodYear = period.getPricePeriod() != null ? String.valueOf(period.getPricePeriod().getStartDate().getYear()) : "    ";
        var reportPeriodYear = period.getPrevPeriod() != null ? String.valueOf(period.getPrevPeriod().getPricePeriod().getStartDate().getYear()) : "    ";
        var productName = "на изготовление и поставку изделия " + product.getTechSpecName() + " " + product.getDecimalNumber();
        var year = planPeriodYear.isEmpty() ? String.valueOf(LocalDate.now().getYear()) : planPeriodYear;

        XSSFFont defaultHeaderFont = workbook.createFont();
        defaultHeaderFont.setFontName("Times New Roman");
        defaultHeaderFont.setFontHeightInPoints((short) 12);
        defaultHeaderFont.setBold(true);

        XSSFFont defaultFont = workbook.createFont();
        defaultFont.setFontName("Times New Roman");
        defaultFont.setFontHeightInPoints((short) 10);

        XSSFFont fontInfoSuperscript = workbook.createFont();
        fontInfoSuperscript.setTypeOffset(Font.SS_SUPER);

        XSSFRichTextString text;

        Map<String, Object> map = new HashMap<>();
        map.put("&REPORT_DATE&", reportPeriodYear);
        map.put("&PLAN_DATE&", planPeriodYear);
        map.put("&PRODUCT&", productName);
        map.put("&YEAR&", year);
        map.put("&CURYEAR&", String.valueOf(LocalDate.now().getYear()));

        String constructorHead, headPlEc, headProduction, ecoDirector, chiefTech, accountant = "";
        switch (decipherment.getType().getEnum()) {
            case FORM_5:
                constructorHead = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&CONSTRUCT_HEAD&", constructorHead);
                break;
            case FORM_7:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                headProduction = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PRODUCTION) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PRODUCTION).getUser().getUserShortName() : "";
                map.put("&PL_EC_DEP&", headPlEc);
                map.put("&PROD&", headProduction);
                break;
            case FORM_8:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PL_EC_DEP&", headPlEc);
                break;
            case FORM_7_1:
            case FORM_14:
            case FORM_14_1:
            case FORM_16:
            case FORM_17:
                constructorHead = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_CONSTRUCT_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&CONSTRUCT_HEAD&", constructorHead);
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PL_EC_DEP&", headPlEc);
                break;
            case FORM_19:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PL_EC_DEP&", headPlEc);
                ecoDirector = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO).getUser().getUserShortName() : "";
                map.put("&ECO_DIRECTOR&", ecoDirector);
                break;
            case FORM_10:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PEO&", headPlEc);
                chiefTech = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CHIEF_TECHNOLOGIST) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, CHIEF_TECHNOLOGIST).getUser().getUserShortName() : "";
                map.put("&CHIEF_TECH&", chiefTech);
                text = new XSSFRichTextString("Расчет-обоснование");
                text.applyFont(defaultHeaderFont);
                text.append("1", fontInfoSuperscript);
                text.append("уровня (%) дополнительной заработной платы основных работников", defaultHeaderFont);
                text.append("2, 3, 4", fontInfoSuperscript);
                text.append(") по ГОЗ на " + year + " год", defaultHeaderFont);
                map.put("&HEADER&", text);
                break;
            case FORM_11:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PEO&", headPlEc);
                accountant = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT).getUser().getUserShortName() : "";
                map.put("&BUCH&", accountant);
                text = new XSSFRichTextString("Отчетный период / период, предшествующий планируемому (год " + reportPeriodYear + ")");
                text.applyFont(defaultFont);
                text.append("4", fontInfoSuperscript);
                map.put("&REPORT_DATE&", text);
                break;
            case FORM_12:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PEO&", headPlEc);
                accountant = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT).getUser().getUserShortName() : "";
                map.put("&BUCH&", accountant);
                text = new XSSFRichTextString("общехозяйственных затрат / административно-управленческих расходов");
                text.applyFont(defaultHeaderFont);
                text.append("1, 2", fontInfoSuperscript);
                text.append("на " + year + " год", defaultHeaderFont);
                map.put("&HEADER&", text);
                break;
            case FORM_13:
            case FORM_21:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PEO&", headPlEc);
                accountant = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, ACCOUNTANT).getUser().getUserShortName() : "";
                map.put("&BUCH&", accountant);
                break;
            case FORM_22:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PEO&", headPlEc);
                ecoDirector = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO).getUser().getUserShortName() : "";
                map.put("&DIR_ECO&", ecoDirector);
                break;
            case FORM_23:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PEO&", headPlEc);
                ecoDirector = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, DIRECTOR_ECO).getUser().getUserShortName() : "";
                map.put("&DIR_ECO&", ecoDirector);
                text = new XSSFRichTextString("Отчетный период / период, предшествующий планируемому (год " + reportPeriodYear + ")");
                text.applyFont(defaultFont);
                text.append("5", fontInfoSuperscript);
                map.put("&REPORT_DATE&", text);
                break;
            case EXPLANATION_NOTE:
                headPlEc = productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT) != null ? productDeciphermentAttrValService.getFirstByDeciphermentAndAttributeKey(decipherment, HEAD_PL_EC_DEPARTMENT).getUser().getUserShortName() : "";
                map.put("&PEO&", headPlEc);
        }

        XSSFSheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            var row = sheet.getRow(i);
            if (row != null) {
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    var cell = row.getCell(j);
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        var textCell = cell.getStringCellValue();
                        for (var key : map.keySet()) {
                            if (textCell.contains(key)) {
                                if (map.get(key) instanceof XSSFRichTextString) {
                                    cell.setCellValue((XSSFRichTextString)map.get(key));
                                } else {
                                    textCell = textCell.replaceAll(key, map.get(key).toString());
                                    cell.setCellValue(textCell);
                                }
                            }
                        }
                    }
                }
            }
        }
        return workbook;
    }
}