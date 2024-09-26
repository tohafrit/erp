package ru.korundm.enumeration;

import lombok.Getter;
import ru.korundm.helper.converter.EnumConverter;
import ru.korundm.helper.converter.EnumConvertible;

import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AdministrationOfficeStatus implements EnumConvertible<Long> {

    NEW(Statuses.NEW, "Новая", "#ffffff", "#000000"),
    IN_PROGRESS(Statuses.IN_PROGRESS, "В работе", "#ffffff", "#000000"),
    FINISHED(Statuses.FINISHED, "Выполнена", "#e80b3e", "#ffffff");

    @Getter
    private Long id;

    @Getter
    private String property;

    @Getter
    private String background;

    @Getter
    private String color;

    AdministrationOfficeStatus(long id, String property,String background, String color) {
        this.id = id;
        this.property = property;
        this.background = background;
        this.color = color;
    }

    public static AdministrationOfficeStatus getById(long id) {
        return Arrays.stream(values()).filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    public static List<AdministrationOfficeStatus> getAllButNew() {
        return Arrays.stream(values()).filter(item -> item.getId() != 1).collect(Collectors.toList());
    }


    @Converter
    public static class CustomConverter extends EnumConverter<AdministrationOfficeStatus, Long> {
        public CustomConverter() { super(AdministrationOfficeStatus.class); }
    }

    @Override
    public Long toValue() {
        return this.id;
    }

    public static class Statuses {

        /** Новая заявка */
        static final long NEW = 1;

        /** В процессе выполнения */
        static final long IN_PROGRESS = 2;

        /** Заявка выполнена */
        static final long FINISHED = 3;
    }
}