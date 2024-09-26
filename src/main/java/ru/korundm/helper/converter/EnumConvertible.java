package ru.korundm.helper.converter;

/**
 * Интерфейс для преобразования значений перечислений в базу данных и обратно
 * @author mazur_ea
 * Date:   17.12.2019
 */
@FunctionalInterface
public interface EnumConvertible<V> {

    V toValue();
}