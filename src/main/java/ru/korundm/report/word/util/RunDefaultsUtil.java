package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.XWPFDefaultRunStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Утилити класс для работы со стандартными настройками текста документа в word-отчетах
 * <br>
 * В xml отображении - pkg:part pkg:name="/word/styles.xml"
 * <br>
 * Имеет тег - <b>w:styles -> w:docDefaults -> w:rPrDefault -> w:rPr</b>
 * @author mazur_ea
 * Date:   21.01.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RunDefaultsUtil {

    // Методы узлов

    /**
     * Получение узла свойств
     * @param style стиль {@link XWPFDefaultRunStyle}
     * @return узел свойств {@link CTRPr}
     */
    public static CTRPr propertyNode(@NonNull XWPFDefaultRunStyle style) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method method = style.getClass().getDeclaredMethod("getRPr");
        method.setAccessible(true);
        return (CTRPr) method.invoke(style);
    }

    // Методы настроек

    /**
     * Размер шрифта
     * @param style стиль {@link XWPFDefaultRunStyle}
     * @param size размер
     */
    public static void size(@NonNull XWPFDefaultRunStyle style, Long size) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        CTRPr ctrPr = propertyNode(style);
        WordUtil.defaultSetCTHpsMeasure(ctrPr.isSetSz() ? ctrPr.getSz() : ctrPr.addNewSz(), size);
    }

    /**
     * Размер комплексно-скриптового шрифта
     * @param style стиль {@link XWPFDefaultRunStyle}
     * @param size размер
     */
    public static void sizeCs(@NonNull XWPFDefaultRunStyle style, Long size) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        CTRPr ctrPr = propertyNode(style);
        WordUtil.defaultSetCTHpsMeasure(ctrPr.isSetSzCs() ? ctrPr.getSzCs() : ctrPr.addNewSzCs(), size);
    }
}