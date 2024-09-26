package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.dto.DropdownOption;
import ru.korundm.entity.ComponentCategory;
import ru.korundm.enumeration.ComponentAttributeType;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Форма для описаний данных о атрибутах категории
 * @author mazur_ea
 * Date:   31.03.2019
 */
@Getter
@Setter
public class EditComponentAttributeForm implements Validatable {

    // Константные значения при создании настроек атрибута

    public final static int MIN_OPTION_LIMIT = 1; // минимальный лимит количества опций в селекте
    public final static int MAX_OPTION_LIMIT = 20; // максимальный лимит количества опций в селекте

    public final static int OPTION_NAME_MIN_LENGTH = 1; // минимальная длина наименования пункта меню селекта
    public final static int OPTION_NAME_MAX_LENGTH = 32; // максимальная длина наименования пункта меню селекта

    public final static long INPUT_MIN_LENGTH_LIMIT = 0; // лимит минимальной длины поля ввода
    public final static long INPUT_MAX_LENGTH_LIMIT = 64; // лимит максимальной длины поля ввода

    // Основные параметры
    private Long id; // идентификатор
    private ComponentAttributeType type; // тип
    private ComponentCategory category; // категория
    private String name; // наименование
    private String description; // описание

    // Динамические параметры
    private boolean required; // требуется к заполнению
    private boolean disabled; // запретить редактирование
    private boolean multiple; // множественный выбор
    private Integer inputMinLength; // минимальная длинна поля
    private Integer inputMaxLength; // максимальная длинна поля
    private List<DropdownOption> selectOptionList = new ArrayList<>(); // опции выпадающего списка
    private String selectPostfix; // постфикс пунктов меню выпадающего списка
    private boolean techCharInclude = true; // добавление атрибута в поле техничеких характеристик компонента

    @Override
    public void validate(@NotNull ValidatorErrors errors) {
        // Общие
        if (StringUtils.isBlank(name) || name.length() > 64) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 64);
        }
        // Проверки для выпадающего списка
        if (Objects.equals(type, ComponentAttributeType.SELECT)) {
            // Проверка пунктов выпадающего списка
            int optionSize = selectOptionList.size();
            if (optionSize < MIN_OPTION_LIMIT || optionSize > MAX_OPTION_LIMIT) {
                errors.putError("selectOptionList", "validator.editComponentAttributeForm.selectOptionRange", MIN_OPTION_LIMIT, MAX_OPTION_LIMIT);
            } else {
                boolean isError = selectOptionList.stream().anyMatch(option -> {
                    int optionLength = StringUtils.isBlank(option.getValue()) ? 0 : option.getValue().length();
                    return optionLength < OPTION_NAME_MIN_LENGTH || optionLength > OPTION_NAME_MAX_LENGTH;
                });
                if (isError) {
                    errors.putError("selectOptionList", "validator.editComponentAttributeForm.optionsNameLength", OPTION_NAME_MIN_LENGTH, OPTION_NAME_MAX_LENGTH);
                }
            }
        }
        // Проверки для поля ввода
        if (Objects.equals(type, ComponentAttributeType.INPUT)) {
            // Проверка минимальной и максимальной длинны допустимой для поля
            if (inputMinLength == null || inputMinLength < INPUT_MIN_LENGTH_LIMIT || inputMinLength > INPUT_MAX_LENGTH_LIMIT) {
                errors.putError("inputMinLength", "validator.form.fixRange", INPUT_MIN_LENGTH_LIMIT, INPUT_MAX_LENGTH_LIMIT);
            }
            if (inputMaxLength == null || inputMaxLength < INPUT_MIN_LENGTH_LIMIT || inputMaxLength > INPUT_MAX_LENGTH_LIMIT) {
                errors.putError("inputMaxLength", "validator.form.fixRange", INPUT_MIN_LENGTH_LIMIT, INPUT_MAX_LENGTH_LIMIT);
            }
            if (inputMinLength != null && inputMaxLength != null && inputMinLength > inputMaxLength) {
                errors.putError("inputMinLength", "validator.editComponentAttributeForm.minMoreMax");
                errors.putError("inputMaxLength", "validator.editComponentAttributeForm.maxLessMin");
            }
        }
    }
}