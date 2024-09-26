package ru.korundm.enumeration;

import lombok.Getter;
import ru.korundm.helper.converter.EnumConverter;
import ru.korundm.helper.converter.EnumConvertible;

import javax.persistence.Converter;

/**
 * Типы атрибутов для групп компонентов
 * @author mazur_ea
 * Date:   19.03.2019
 */
public enum ComponentAttributeType implements EnumConvertible<Long> {

    SELECT(1, "componentAttributeType.select"), // выпадающий список
    CHECKBOX(2, "componentAttributeType.checkbox"), // чекбокс
    INPUT(3, "componentAttributeType.input"); // поле

    @Getter
    private long id;

    @Getter
    private String nameProperty;

    ComponentAttributeType(long id, String nameProperty) {
        this.id = id;
        this.nameProperty = nameProperty;
    }

    @Converter
    public static class CustomConverter extends EnumConverter<ComponentAttributeType, Long> {
        public CustomConverter() { super(ComponentAttributeType.class); }
    }

    @Override
    public Long toValue() {
        return this.id;
    }
}