package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

@Getter
@Setter
public class EditComponentGroupForm implements Validatable {

    private Long id; // идентификатор
    private Integer number; // номер
    private String name; // наименование

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name) || name.length() > 128) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 128);
        }
        if (number == null) {
            errors.putError("number", ValidatorMsg.REQUIRED);
        }
        if (number > 999) {
            errors.putError("number", "validator.form.fixRange", 1, 999);
        }
    }
}