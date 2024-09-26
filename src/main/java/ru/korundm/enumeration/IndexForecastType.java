package ru.korundm.enumeration;

import lombok.Getter;


/**
 * Список типов прогнозов индексов
 * @author pakhunov_an
 * Date:   09.10.2018
 */
public enum IndexForecastType {

    PRICE(Types.PRICE, "indexForecast.price"),
    DEFLATOR(Types.DEFLATOR, "indexForecast.deflator");

    @Getter
    private final String value;

    @Getter
    private final String description;

    IndexForecastType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static class Types {

        /** Индекс цен */
        public static final String PRICE = "price";

        /** Дефлятор */
        public static final String DEFLATOR = "deflator";
    }
}