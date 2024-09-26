package ru.korundm.report.word.helper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;

/**
 * Класс хранения настроек интервалов абзаца для {@link CTSpacing}
 * @author mazur_ea
 * Date:   24.01.2020
 */
@Getter @Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CTSpacingProperties {

    public static CTSpacingProperties instance() {
        return new CTSpacingProperties();
    }

    private Long before; // Абзац -> Отступы и интервалы -> Интервал -> Перед
    private Long after; // Абзац -> Отступы и интервалы -> Интервал -> После
    private STLineSpacingRule.Enum lineRule; // Абзац -> Отступы и интервалы -> Интервал -> Междустрочный
    private Long line; // Абзац -> Отступы и интервалы -> Интервал -> Значение
    private Long beforeLines; // Интервал до начала всех линий абзаца
    private Long afterLines; // Интервал после окончания всех линий абзаца
    private STOnOff.Enum beforeAutoSpacing; // Автоматический интервал до начала всех линий абзаца - before и beforeLines игнорируются
    private STOnOff.Enum afterAutoSpacing; // Автоматический интервал после окончания всех линий абзаца - after и afterLines игнорируются
}