package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

@Getter
@Setter
public class EditMessageTypeForm implements Validatable {

    private Long id; // идентификатор
    private String name; // название типа сообщения
    private String description; // описание типа сообщения, может хранить передаваемые параментры по шаблону #FIELD_NAME#
    private String code; // уникальный код типа сообщения
    private boolean isCheckUniqueCode; // флаг проверки типа сообщения на уникальность кода

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name) || name.length() > 128) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 128);
        }
        if (description != null && description.length() > 1024) {
            errors.putError("description", ValidatorMsg.RANGE_LENGTH, 0, 1024);
        }
        if (StringUtils.isBlank(code)) {
            errors.putError("code", ValidatorMsg.REQUIRED);
        }
        if (isCheckUniqueCode) {
            errors.putError("code", ValidatorMsg.UNIQUE);
        }
    }
}