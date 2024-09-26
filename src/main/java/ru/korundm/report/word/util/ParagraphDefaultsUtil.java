package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.XWPFDefaultParagraphStyle;
import org.apache.poi.xwpf.usermodel.XWPFDefaultRunStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Утилити класс для работы со стандартными настройками абзацев документа в word-отчетах
 * <br>
 * В xml отображении - pkg:part pkg:name="/word/styles.xml"
 * <br>
 * Имеет тег - <b>w:styles -> w:docDefaults -> w:pPrDefault -> w:pPr</b>
 * @author mazur_ea
 * Date:   21.01.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParagraphDefaultsUtil {

    // Методы узлов

    /**
     * Получение узла свойств
     * @param style стиль {@link XWPFDefaultRunStyle}
     * @return узел свойств {@link CTPPr}
     */
    public static CTPPr propertyNode(@NonNull XWPFDefaultParagraphStyle style) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method method = style.getClass().getDeclaredMethod("getPPr");
        method.setAccessible(true);
        return (CTPPr) method.invoke(style);
    }

    // Методы настроек
}