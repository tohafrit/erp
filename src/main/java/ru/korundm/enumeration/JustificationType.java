package ru.korundm.enumeration;

import lombok.Getter;

import java.util.Arrays;

/**
 * Список типов обоснований
 * @author pakhunov_an
 * Date:   08.10.2018
 */
public enum JustificationType {

    INDEX(Types.INDEX, "justificationType.index", "/indexForecast"),
    SPECIAL_CHECK(Types.SPECIAL_CHECK, "justificationType.specialCheck", "/specialPrice/check"),
    SPECIAL_RESEARCH(Types.SPECIAL_RESEARCH, "justificationType.specialResearch", "/specialPrice/research"),
    WORK(Types.WORK, "justificationType.work", "/workTypePrice"),
    TECHNICAL_PROCESS(Types.TECHNICAL_PROCESS, "justification.technicalProcess", "/productTechnicalProcess");

    @Getter
    private final String type;

    @Getter
    private final String property;

    @Getter
    private final String url;

    JustificationType(String type, String property, String url) {
        this.type = type;
        this.property = property;
        this.url = url;
    }

    /**
     * Метод для поиска типа обоснования по его значению
     * @param type значение
     * @return тип обоснования
     */
    public static JustificationType getByType(String type) {
        return Arrays.stream(values()).filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }

    public static class Types {

        /** Прогноз индексов */
        public static final String INDEX = "1";

        /** Специальная проверка */
        public static final String SPECIAL_CHECK = "2";

        /** Специальное исследование */
        public static final String SPECIAL_RESEARCH = "3";

        /** Виды работ */
        public static final String WORK = "4";

        /** Техпроцессы */
        public static final String TECHNICAL_PROCESS = "5";
    }
}