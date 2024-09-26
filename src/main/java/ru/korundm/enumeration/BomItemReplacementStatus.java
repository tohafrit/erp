package ru.korundm.enumeration;

import lombok.Getter;
import ru.korundm.helper.converter.EnumConverter;
import ru.korundm.helper.converter.EnumConvertible;

import javax.persistence.Converter;
import java.util.Arrays;

/**
 * Справочник статусов замен для вхождений в спецификацию
 * @author mazur_ea
 * Date:   01.08.2020
 */
@Getter
public enum BomItemReplacementStatus implements EnumConvertible<Long> {

    NOT_PROCESSED(1, "Не обработано"),
    CATALOG(2, "Справочник"),
    ALLOWED(3, "Разрешено"),
    PROHIBITED(4, "Запрещено");

    private final long id;
    private final String name;

    BomItemReplacementStatus(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static BomItemReplacementStatus getById(long id) {
        return Arrays.stream(values()).filter(item -> item.getId() == id).findFirst().orElse(null);
    }

    @Converter
    public static class CustomConverter extends EnumConverter<BomItemReplacementStatus, Long> {
        public CustomConverter() { super(BomItemReplacementStatus.class); }
    }

    @Override
    public Long toValue() {
        return this.id;
    }
}