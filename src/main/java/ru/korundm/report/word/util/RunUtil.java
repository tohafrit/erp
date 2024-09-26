package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import ru.korundm.report.word.helper.CTFontsProperties;

/**
 * Утилити класс для работы с текстом документа в word-отчетах
 * <br>
 * В xml отображении - pkg:part pkg:name="/word/document.xml"
 * <br>
 * Имеет тег - <b>w:r</b>
 * @author mazur_ea
 * Date:   21.01.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RunUtil {

    // Методы узлов

    /**
     * Получение узла свойств
     * @param run текст {@link XWPFRun}
     * @return узел свойств {@link CTRPr}
     */
    public static CTRPr propertyNode(@NonNull XWPFRun run) {
        CTR ctr = run.getCTR();
        return ctr.isSetRPr() ? ctr.getRPr() : ctr.addNewRPr();
    }

    // Методы настроек

    /**
     * Размер шрифта
     * @param run текст {@link XWPFRun}
     * @param size размер в pt
     */
    public static void size(@NonNull XWPFRun run, Long size) {
        CTRPr ctrPr = propertyNode(run);
        WordUtil.defaultSetCTHpsMeasure(ctrPr.isSetSz() ? ctrPr.getSz() : ctrPr.addNewSz(), size);
    }

    /**
     * Размер комплексно-скриптового шрифта
     * @param run текст {@link XWPFRun}
     * @param size размер в pt
     */
    public static void sizeCs(@NonNull XWPFRun run, Long size) {
        CTRPr ctrPr = propertyNode(run);
        WordUtil.defaultSetCTHpsMeasure(ctrPr.isSetSzCs() ? ctrPr.getSzCs() : ctrPr.addNewSzCs(), size);
    }

    /**
     * Позиция
     * @param run текст {@link XWPFRun}
     * @param value значение в pt
     */
    public static void position(@NonNull XWPFRun run, Long value) {
        CTRPr ctrPr = propertyNode(run);
        WordUtil.defaultSetCTSignedHpsMeasure(ctrPr.isSetPosition() ? ctrPr.getPosition() : ctrPr.addNewPosition(), value);
    }

    /**
     * Привязка стиля
     * @param run текст {@link XWPFRun}
     * @param styleId идентификатор стиля
     */
    public static void style(@NonNull XWPFRun run, String styleId) {
        CTRPr ctrPr = propertyNode(run);
        WordUtil.defaultSetCTString(ctrPr.isSetRStyle() ? ctrPr.getRStyle() : ctrPr.addNewRStyle(), styleId);
    }

    /**
     * Шрифт
     * @param run текст {@link XWPFRun}
     * @param properties настройки {@link CTFontsProperties}
     */
    public static void fonts(@NonNull XWPFRun run, CTFontsProperties properties) {
        CTRPr ctrPr = propertyNode(run);
        WordUtil.defaultSetCTFonts(ctrPr.isSetRFonts() ? ctrPr.getRFonts() : ctrPr.addNewRFonts(), properties);
    }

    // Переиспользуемые методы
}