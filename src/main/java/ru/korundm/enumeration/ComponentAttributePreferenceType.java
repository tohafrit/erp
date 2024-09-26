package ru.korundm.enumeration;

import lombok.Getter;
import ru.korundm.helper.converter.EnumConverter;
import ru.korundm.helper.converter.EnumConvertible;

import javax.persistence.Converter;

/**
 * Типы настроек атрибутов для компонентов
 * @author mazur_ea
 * Date:   19.03.2019
 */
public enum ComponentAttributePreferenceType implements EnumConvertible<Long> {

    REQUIRED(1), // обязательно для заполнения/выбора
    DISABLED(2), // доступность элемента
    MULTIPLE(3), // множественный выбор
    TECH_CHAR_INCLUDE(4), // флаг включения в технические характеристики компонента
    INPUT_MIN_LENGTH(5), // минимальная длина поля
    INPUT_MAX_LENGTH(6), // максимальная длина поля
    SELECT_OPTION(7), // опция выпадающего списка
    SELECT_POSTFIX(8); // постфикс выпадающего списка

    @Getter
    private final long id;

    ComponentAttributePreferenceType(long id) {
        this.id = id;
    }

    @Converter
    public static class CustomConverter extends EnumConverter<ComponentAttributePreferenceType, Long> {
        public CustomConverter() { super(ComponentAttributePreferenceType.class); }
    }

    @Override
    public Long toValue() {
        return this.id;
    }
}