package ru.korundm.report.word.helper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STThemeColor;

/**
 * Класс хранения настроек границ ячеек для {@link CTBorder}
 * @author mazur_ea
 * Date:   24.01.2020
 */
@Getter @Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CTBorderProperties {

    public static CTBorderProperties instance() {
        return new CTBorderProperties();
    }

    private STBorder.Enum val = STBorder.SINGLE; // тип границы
    private Long sz; // размер границы
    private Long space; // интервал смещения
    private Object color; // цвет границы
    private STOnOff.Enum frame; // рамка
    private STThemeColor.Enum themeColor; // тема
    private STOnOff.Enum shadow; // тень
}