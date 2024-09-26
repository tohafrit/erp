package ru.korundm.report.excel.enumeration;

import lombok.Getter;

import java.util.Arrays;

/**
 * Перечисление для хранения типов офсетов для шрифтов
 * @author mazur_ea
 * Date:   25.10.2019
 */
@Getter
public enum FontTypeOffset {

    /** Отсутствует */
    NONE(0),
    /** Надстрочный */
    SUPERSCRIPT(1),
    /** Подстрочный */
    SUBSCRIPT(2);

    /** Индекс */
    private final int index;

    FontTypeOffset(int index) {
        this.index = index;
    }

    public static FontTypeOffset forIndex(int index) {
        FontTypeOffset fontTypeOffset = Arrays.stream(values()).filter(offset -> offset.getIndex() == (index)).findFirst().orElse(null);
        if (fontTypeOffset == null) {
            fontTypeOffset = NONE;
        }
        return fontTypeOffset;
    }

    public byte getByteIndex() {
        return (byte) this.index;
    }
}