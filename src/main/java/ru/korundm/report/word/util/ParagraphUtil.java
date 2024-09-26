package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPr;
import ru.korundm.report.word.helper.CTIndProperties;
import ru.korundm.report.word.helper.CTSpacingProperties;

/**
 * Утилити класс для работы с абзацами документа в word-отчетах
 * <br>
 * В xml отображении - pkg:part pkg:name="/word/document.xml"
 * <br>
 * Имеет тег - <b>w:p</b>
 * @author mazur_ea
 * Date:   21.01.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParagraphUtil {

    // Методы узлов

    /**
     * Получение узла свойств
     * @param paragraph абзац {@link XWPFParagraph}
     * @return узел свойств {@link CTPPr}
     */
    public static CTPPr propertyNode(@NonNull XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        return ctp.isSetPPr() ? ctp.getPPr() : ctp.addNewPPr();
    }

    /**
     * Получение узла свойств текста
     * @param paragraph абзац {@link XWPFParagraph}
     * @return узел свойств текста {@link CTPPr}
     */
    public static CTParaRPr runPropertyNode(@NonNull XWPFParagraph paragraph) {
        CTPPr ctpPr = propertyNode(paragraph);
        return ctpPr.isSetRPr() ? ctpPr.getRPr() : ctpPr.addNewRPr();
    }

    // Методы настроек

    /**
     * Запрет висячих ссылок
     * <br>
     * Абзац -> Положение на странице -> Разбивка страницы -> запрет висячих ссылок
     * @param paragraph абзац {@link XWPFParagraph}
     * @param turn true - включить, false - отключить
     */
    public static void windowControl(@NonNull XWPFParagraph paragraph, Boolean turn) {
        CTPPr ctpPr = propertyNode(paragraph);
        WordUtil.defaultOnOffSetCTOnOff(ctpPr.isSetWidowControl() ? ctpPr.getWidowControl() : ctpPr.addNewWidowControl(), turn);
    }

    /**
     * Запрет нумерации строк
     * <br>
     * Абзац -> Положение на странице -> Исключения форматирования -> запретить нумерацию строк
     * @param paragraph абзац {@link XWPFParagraph}
     * @param turn true - включить, false - отключить
     */
    public static void suppressLineNumbers(@NonNull XWPFParagraph paragraph, Boolean turn) {
        CTPPr ctpPr = propertyNode(paragraph);
        WordUtil.defaultOnOffSetCTOnOff(ctpPr.isSetSuppressLineNumbers() ? ctpPr.getSuppressLineNumbers() : ctpPr.addNewSuppressLineNumbers(), turn);
    }

    /**
     * Запрет автоматического переноса строк
     * <br>
     * Абзац -> Положение на странице -> Исключения форматирования -> запретить автоматический перенос строк
     * @param paragraph абзац {@link XWPFParagraph}
     * @param turn true - включить, false - отключить
     */
    public static void suppressAutoHyphens(@NonNull XWPFParagraph paragraph, Boolean turn) {
        CTPPr ctpPr = propertyNode(paragraph);
        WordUtil.defaultOnOffSetCTOnOff(ctpPr.isSetSuppressAutoHyphens() ? ctpPr.getSuppressAutoHyphens() : ctpPr.addNewSuppressAutoHyphens(), turn);
    }

    /**
     * Интервалы абзаца
     * <br>
     * Абзац -> Оступы и интервалы -> Интервал
     * @param paragraph абзац {@link XWPFParagraph}
     * @param properties настройки {@link CTSpacingProperties}
     */
    public static void spacing(@NonNull XWPFParagraph paragraph, CTSpacingProperties properties) {
        CTPPr ctpPr = propertyNode(paragraph);
        WordUtil.defaultSetCTSpacing(ctpPr.isSetSpacing() ? ctpPr.getSpacing() : ctpPr.addNewSpacing(), properties);
    }

    /**
     * Отступы абзаца
     * <br>
     * Абзац -> Оступы и интервалы -> Отступ
     * @param paragraph абзац {@link XWPFParagraph}
     * @param properties настройки {@link CTIndProperties}
     */
    public static void indentation(@NonNull XWPFParagraph paragraph, CTIndProperties properties) {
        CTPPr ctpPr = propertyNode(paragraph);
        WordUtil.defaultSetCTInd(ctpPr.isSetInd() ? ctpPr.getInd() : ctpPr.addNewInd(), properties);
    }

    /**
     * Привязка стиля
     * @param paragraph абзац {@link XWPFParagraph}
     * @param styleId идентификатор стиля
     */
    public static void style(@NonNull XWPFParagraph paragraph, String styleId) {
        CTPPr ctpPr = propertyNode(paragraph);
        WordUtil.defaultSetCTString(ctpPr.isSetPStyle() ? ctpPr.getPStyle() : ctpPr.addNewPStyle(), styleId);
    }

    /**
     * Размер шрифта для текста
     * @param paragraph абзац {@link XWPFParagraph}
     * @param size размер
     */
    public static void runSize(@NonNull XWPFParagraph paragraph, Long size) {
        CTParaRPr ctParaRPr = runPropertyNode(paragraph);
        WordUtil.defaultSetCTHpsMeasure(ctParaRPr.isSetSz() ? ctParaRPr.getSz() : ctParaRPr.addNewSz(), size);
    }

    /**
     * Размер комплексно-скриптового шрифта
     * @param paragraph абзац {@link XWPFParagraph}
     * @param size размер
     */
    public static void runSizeCs(@NonNull XWPFParagraph paragraph, Long size) {
        CTParaRPr ctParaRPr = runPropertyNode(paragraph);
        WordUtil.defaultSetCTHpsMeasure(ctParaRPr.isSetSzCs() ? ctParaRPr.getSzCs() : ctParaRPr.addNewSzCs(), size);
    }

    // Переиспользуемые методы
}