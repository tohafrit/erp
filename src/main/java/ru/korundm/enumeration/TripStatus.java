package ru.korundm.enumeration;

import lombok.Getter;
import ru.korundm.helper.converter.EnumConverter;
import ru.korundm.helper.converter.EnumConvertible;

import javax.persistence.Converter;
import java.util.Arrays;

public enum TripStatus implements EnumConvertible<Long> {

    ON_REVIEW(TripStatus.Types.ON_REVIEW, "trip.onReview", "Ожидание"),
    CONFIRMED(TripStatus.Types.CONFIRMED, "trip.confirmed", "Подтверждена"),
    REJECTED(TripStatus.Types.REJECTED, "trip.rejected", "Не подтверждена");

    @Getter
    private Long id;

    @Getter
    private String property;

    @Getter
    private String name;

    TripStatus(long id, String property, String name) {
        this.id = id;
        this.property = property;
        this.name = name;
    }

    public static TripStatus getById(long id) {
        return Arrays.stream(values()).filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    @Converter
    public static class CustomConverter extends EnumConverter<TripStatus, Long> {
        public CustomConverter() { super(TripStatus.class); }
    }

    @Override
    public Long toValue() {
        return this.id;
    }

    public static class Types {

        /** Подтверждена */
        static final long ON_REVIEW = 1;

        /** Подтверждена */
        static final long CONFIRMED = 2;

        /** Не подтверждена */
        static final long REJECTED = 3;
    }
}