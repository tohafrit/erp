package ru.korundm.report.word.helper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;

/**
 * Класс хранения настроек отступов абзаца для {@link CTInd}
 * @author mazur_ea
 * Date:   24.01.2020
 */
@Getter @Setter
@Accessors(fluent = true, chain = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CTIndProperties {

    public static CTIndProperties instance() {
        return new CTIndProperties();
    }

    private Long left; // Абзац -> Отступы и интервалы -> Отступ -> Слева
    private Long right; // Абзац -> Отступы и интервалы -> Отступ -> Справа
    private Long firstLine; // Абзац -> Отступы и интервалы -> Отступ -> Первая строка -> Отступ
    private Long hanging; // Абзац -> Отступы и интервалы -> Отступ -> Первая строка -> Выступ (Если установлен, то firstLine игнорируется)
}