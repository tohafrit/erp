package ru.korundm.helper.converter;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Objects;

/**
 * Класс-конвертор значений перечислений для сохранения в базу данных и обратно
 * @author mazur_ea
 * Date:   17.12.2019
 */
public abstract class EnumConverter<E extends Enum<E> & EnumConvertible<V>, V> implements AttributeConverter<E, V> {

    private final Class<E> cl;

    public EnumConverter(Class<E> cl) {
        this.cl = cl;
    }

    @Override
    public V convertToDatabaseColumn(E enumeration) {
        return enumeration == null ? null : enumeration.toValue();
    }

    @Override
    public E convertToEntityAttribute(V value) {
        return Arrays.stream(cl.getEnumConstants()).filter(e -> Objects.equals(e.toValue(), value)).findFirst().orElse(null);
    }
}