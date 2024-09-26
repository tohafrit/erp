package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import ru.korundm.report.word.helper.CTFontsProperties;
import ru.korundm.report.word.helper.CTSpacingProperties;

/**
 * Утилити класс для работы со стилями в word-отчетах
 * <br>
 * В xml отображении - pkg:part pkg:name="/word/styles.xml"
 * <br>
 * Имеет тег - <b>w:style</b>
 * <br>
 * Со стороны документа меню находится в "изменении стиля" (Alt + Ctrl + Shift + S)
 * @author mazur_ea
 * Date:   21.01.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StyleUtil {

    public final static String DEFAULT_PARAGRAPH_STYLE_ID = "a";
    public final static String DEFAULT_CHARACTER_STYLE_ID = "a0";
    public final static String DEFAULT_TABLE_STYLE_ID = "a1";
    public final static String DEFAULT_NUMBERING_STYLE_ID = "a2";

    private final static String DEFAULT_PARAGRAPH_STYLE_NAME = "Normal";
    private final static String DEFAULT_CHARACTER_STYLE_NAME = "Default Paragraph Font";
    private final static String DEFAULT_TABLE_STYLE_NAME = "Normal Table";
    private final static String DEFAULT_NUMBERING_STYLE_NAME = "No List";

    // Методы узлов

    /**
     * Получение узла свойств абзаца
     * @param style стиль {@link XWPFStyle}
     * @return узел свойств абзаца {@link CTPPr}
     */
    public static CTPPr paragraphPropertyNode(@NonNull XWPFStyle style) {
        CTStyle ctStyle = style.getCTStyle();
        return ctStyle.isSetPPr() ? ctStyle.getPPr() : ctStyle.addNewPPr();
    }

    /**
     * Получение узла свойств текста
     * @param style стиль {@link XWPFStyle}
     * @return узел свойств текста {@link CTRPr}
     */
    public static CTRPr runPropertyNode(@NonNull XWPFStyle style) {
        CTStyle ctStyle = style.getCTStyle();
        return ctStyle.isSetRPr() ? ctStyle.getRPr() : ctStyle.addNewRPr();
    }

    // Метоы настроек абзацев

    /**
     * Запрет висячих ссылок
     * @param style стиль {@link XWPFStyle}
     * @param turn true - включить, false - отключить
     */
    public static void paragraphWindowControl(@NonNull XWPFStyle style, Boolean turn) {
        CTPPr ctpPr = paragraphPropertyNode(style);
        WordUtil.defaultOnOffSetCTOnOff(ctpPr.isSetWidowControl() ? ctpPr.getWidowControl() : ctpPr.addNewWidowControl(), turn);
    }

    /**
     * Запрет нумерации строк
     * @param style стиль {@link XWPFStyle}
     * @param turn true - включить, false - отключить
     */
    public static void paragraphSuppressLineNumbers(@NonNull XWPFStyle style, Boolean turn) {
        CTPPr ctpPr = paragraphPropertyNode(style);
        WordUtil.defaultOnOffSetCTOnOff(ctpPr.isSetSuppressLineNumbers() ? ctpPr.getSuppressLineNumbers() : ctpPr.addNewSuppressLineNumbers(), turn);
    }

    /**
     * Запрет автоматического переноса строк
     * @param style стиль {@link XWPFStyle}
     * @param turn true - включить, false - отключить
     */
    public static void paragraphSuppressAutoHyphens(@NonNull XWPFStyle style, Boolean turn) {
        CTPPr ctpPr = paragraphPropertyNode(style);
        WordUtil.defaultOnOffSetCTOnOff(ctpPr.isSetSuppressAutoHyphens() ? ctpPr.getSuppressAutoHyphens() : ctpPr.addNewSuppressAutoHyphens(), turn);
    }

    /**
     * Интервалы абзаца
     * @param style стиль {@link XWPFStyle}
     * @param properties настройки {@link CTSpacingProperties}
     */
    public static void paragraphSpacing(@NonNull XWPFStyle style, CTSpacingProperties properties) {
        CTPPr ctpPr = paragraphPropertyNode(style);
        WordUtil.defaultSetCTSpacing(ctpPr.isSetSpacing() ? ctpPr.getSpacing() : ctpPr.addNewSpacing(), properties);
    }

    // Методы настроек текста

    /**
     * Размер шрифта
     * @param style текст {@link XWPFStyle}
     * @param size размер в pt
     */
    public static void runSize(@NonNull XWPFStyle style, Long size) {
        CTRPr ctrPr = runPropertyNode(style);
        WordUtil.defaultSetCTHpsMeasure(ctrPr.isSetSz() ? ctrPr.getSz() : ctrPr.addNewSz(), size);
    }

    /**
     * Размер комплексно-скриптового шрифта
     * @param style текст {@link XWPFStyle}
     * @param size размер в pt
     */
    public static void runSizeCs(@NonNull XWPFStyle style, Long size) {
        CTRPr ctrPr = runPropertyNode(style);
        WordUtil.defaultSetCTHpsMeasure(ctrPr.isSetSzCs() ? ctrPr.getSzCs() : ctrPr.addNewSzCs(), size);
    }

    /**
     * Параметры шрифта
     * @param style стиль {@link XWPFStyle}
     * @param properties настройки {@link CTFontsProperties}
     */
    public static void runFonts(@NonNull XWPFStyle style, CTFontsProperties properties) {
        CTRPr ctrPr = runPropertyNode(style);
        WordUtil.defaultSetCTFonts(ctrPr.isSetRFonts() ? ctrPr.getRFonts() : ctrPr.addNewRFonts(), properties);
    }

    // Методы настроек стиля

    /**
     * Название стиля
     * @param style стиль {@link XWPFStyle}
     * @param name название
     */
    public static void styleName(@NonNull XWPFStyle style, String name) {
        CTStyle ctStyle = style.getCTStyle();
        WordUtil.defaultSetCTString(ctStyle.isSetName() ? ctStyle.getName() : ctStyle.addNewName(), name);
    }

    /**
     * Основание (стиль на котором будет основан данный стиль)
     * @param style стиль {@link XWPFStyle}
     * @param baseId id стиля-основания
     */
    public static void styleBasedOn(@NonNull XWPFStyle style, String baseId) {
        CTStyle ctStyle = style.getCTStyle();
        WordUtil.defaultSetCTString(ctStyle.isSetBasedOn() ? ctStyle.getBasedOn() : ctStyle.addNewBasedOn(), baseId);
    }

    /**
     * Стиль следующего абзаца
     * @param style стиль {@link XWPFStyle}
     * @param nextId id стиля следующего абзаца
     */
    public static void styleNext(@NonNull XWPFStyle style, String nextId) {
        CTStyle ctStyle = style.getCTStyle();
        WordUtil.defaultSetCTString(ctStyle.isSetNext() ? ctStyle.getNext() : ctStyle.addNewNext(), nextId);
    }

    /**
     * Отображать стиль в панели форматов
     * @param style стиль {@link XWPFStyle}
     * @param turn true - включено, false - выключено
     */
    public static void styleQFormat(@NonNull XWPFStyle style, Boolean turn) {
        CTStyle ctStyle = style.getCTStyle();
        WordUtil.defaultOnOffSetCTOnOff(ctStyle.isSetQFormat() ? ctStyle.getQFormat() : ctStyle.addNewQFormat(), turn);
    }

    // Переиспользуемые методы

    /**
     * Создание стиля
     * @param styleType тип стиля {@link STStyleType.Enum}
     * @return стиль {@link XWPFStyle}
     */
    public static XWPFStyle createStyle(@NonNull STStyleType.Enum styleType) {
        XWPFStyle style = new XWPFStyle(CTStyle.Factory.newInstance());
        style.setType(styleType);
        return style;
    }

    /**
     * Получение/создание стандартного стиля документа по типу
     * @param styles стили {@link XWPFStyles}
     * @param styleType тип стиля {@link STStyleType.Enum}
     * @return стандартный стиль {@link XWPFStyle}
     */
    public static XWPFStyle defaultStyle(@NonNull XWPFStyles styles, @NonNull STStyleType.Enum styleType) {
        String id = null;
        String name = null;
        if (STStyleType.PARAGRAPH.equals(styleType)) {
            id = DEFAULT_PARAGRAPH_STYLE_ID;
            name = DEFAULT_PARAGRAPH_STYLE_NAME;
        } else if (STStyleType.CHARACTER.equals(styleType)) {
            id = DEFAULT_CHARACTER_STYLE_ID;
            name = DEFAULT_CHARACTER_STYLE_NAME;
        } else if (STStyleType.TABLE.equals(styleType)) {
            id = DEFAULT_TABLE_STYLE_ID;
            name = DEFAULT_TABLE_STYLE_NAME;
        } else if (STStyleType.NUMBERING.equals(styleType)) {
            id = DEFAULT_NUMBERING_STYLE_ID;
            name = DEFAULT_NUMBERING_STYLE_NAME;
        }
        XWPFStyle existsStyle = styles.getStyleWithName(name);
        XWPFStyle style = existsStyle == null ? createStyle(styleType) : existsStyle;
        style.setStyleId(id);
        styleName(style, name);
        return style;
    }
}