package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

@Getter
@Setter
public class EditProductTypeForm implements Validatable {

    private Long id; // идентификатор
    private String name; // наименование
    private String description; // описание

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name) || name.length() > 64) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 64);
        }
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            errors.putError("description", ValidatorMsg.RANGE_LENGTH, 0, 512);
        }
    }
}