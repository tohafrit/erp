package ru.korundm.dto.component;

import lombok.Getter;
import lombok.Setter;
import ru.korundm.dto.DropdownOption;
import ru.korundm.enumeration.ComponentAttributeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения данных атрибута компонента
 * @author mazur_ea
 * Date:   20.12.2020
 */
@Getter
@Setter
public final class ComponentAttributeItem {

    // Общие атрибуты
    private Long id; // идентификатор
    private ComponentAttributeType type; // тип
    private String name; // наименование

    // Настройки
    private boolean required; // требуется к заполнению
    private boolean disabled; // запретить редактирование
    private boolean multiple; // множественный выбор
    private List<DropdownOption> selectOptionList = new ArrayList<>(); // опции выпадающего списка
    private String selectPostfix; // постфикс пункта меню
    private int inputMinLength; // минимальная длинна поля
    private int inputMaxLength; // максимальная длинна поля

    // Значения атрибутов
    private boolean boolValue; // boolean значение
    private String stringValue; // строковое значение
    private List<Long> selectOptionIdList = new ArrayList<>(); // выбранные опции выпадающего списка
    private Long selectOptionId; // выбранная опция селекта
}