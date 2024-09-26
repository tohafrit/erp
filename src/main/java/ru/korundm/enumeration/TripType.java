package ru.korundm.enumeration;

import lombok.Getter;
import ru.korundm.helper.converter.EnumConverter;
import ru.korundm.helper.converter.EnumConvertible;

import javax.persistence.Converter;
import java.util.Arrays;

public enum TripType implements EnumConvertible<Long> {

    BUSINESS(Types.BUSINESS, "trip.business", "Служебная"),
    PERSONAL(Types.PERSONAL, "trip.personal", "Личная");

    @Getter
    private Long id;

    @Getter
    private String property;

    @Getter
    private String name;

    TripType(long id, String property, String name) {
        this.id = id;
        this.property = property;
        this.name = name;
    }

    public static TripType getById(long id) {
        return Arrays.stream(values()).filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    @Converter
    public static class CustomConverter extends EnumConverter<TripType, Long> {
        public CustomConverter() { super(TripType.class); }
    }

    @Override
    public Long toValue() {
        return this.id;
    }

    public static class Types {

        /** Служебная командировка */
        static final long BUSINESS = 1;

        /** Личная командировка */
        static final long PERSONAL = 2;
    }
}