package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.dto.component.ComponentAttributeItem;
import ru.korundm.entity.*;
import ru.korundm.helper.Validatable;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.util.FormValidatorUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Форма для описаний данных редактирования компонента, включает в себя динамические атрибуты
 * @author mazur_ea
 * Date:   31.03.2019
 */
@Getter
@Setter
public class EditComponentForm implements Validatable {

    private Long id; // идентификатор
    private long lockVersion; // версия блокировки
    private boolean addAsDesign; // режим добавления компонента сразу в статусе кострукторского
    private String name; // наименование по ТС
    private Integer position; // позиция
    private boolean processed; // обработан
    private LocalDateTime modifiedDatetime; // дата и время изменения
    private String purchaseComponentData; // дата и время добавление компонента закупки
    private Component purchaseComponent; // компонент к закупке
    private Component substituteComponent; // заместитель
    private ComponentKind kind; // тип компонента
    private Company producer; // производитель
    private Okei okei; // единицы измерения
    private ComponentPurpose purpose; // назначение
    private ComponentInstallationType installation; // тип установки
    private Double price; // ориентировочная цена
    private Integer deliveryTime; // срок поставки с завода, нед.
    private ComponentCategory category; // категория
    private String description; // описание

    private List<ComponentAttributeItem> attributeList = new ArrayList<>(); // список атрибутов

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name) || name.length() > 128) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 128);
        }
        if (StringUtils.isNotBlank(description) && description.length() > 1024) {
            errors.putError("description", ValidatorMsg.RANGE_LENGTH, 0, 1024);
        }
        if (FormValidatorUtil.formIdNotValid(category)) {
            errors.putError("category", ValidatorMsg.REQUIRED, 0, 1024);
        }
        // Проверка атрибутов
        attributeList.forEach(attribute -> {
            String attributeField = "attribute" + attribute.getId();
            switch (attribute.getType()) {
                case INPUT:
                    int strLength = attribute.getStringValue() == null ? 0 : attribute.getStringValue().trim().length();
                    int min = attribute.getInputMinLength();
                    int max = attribute.getInputMaxLength();
                    if (!attribute.isDisabled() && (strLength < min || strLength > max)) {
                        errors.putError(attributeField, ValidatorMsg.RANGE_LENGTH, min, max);
                    }
                    break;
                case SELECT:
                    if (attribute.isRequired() && !attribute.isDisabled()) {
                        if (
                            (attribute.isMultiple() && attribute.getSelectOptionIdList().isEmpty())
                            || (!attribute.isMultiple() && attribute.getSelectOptionId() == null)
                        ) {
                            errors.putError(attributeField, ValidatorMsg.REQUIRED);
                        }
                    }
                    break;
            }
        });
    }
}