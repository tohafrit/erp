package ru.korundm.report.word.helper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHint;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTheme;

/**
 * Класс хранения настроек шрифта для текста {@link CTFonts}
 * @author mazur_ea
 * Date:   24.01.2020
 */
@Getter @Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CTFontsProperties {

    public static CTFontsProperties instance() {
        return new CTFontsProperties();
    }

    private String cs; // шрифт для форматирования символов комплексных скриптов в юникоде
    private STTheme.Enum csTheme;
    private String ascii; // шрифт для юникода в диапазоне с U+0000 до U+007F
    private STTheme.Enum asciiTheme;
    private String eastAsia; // шрифт для формата символов в восточно-азиатском диапазоне юникода
    private STTheme.Enum eastAsiaTheme;
    private String hAnsi; // шрифт для формата символов в том случае, если не подошли остальные настройки
    private STTheme.Enum hAnsiTheme;
    private STHint.Enum hint;
}