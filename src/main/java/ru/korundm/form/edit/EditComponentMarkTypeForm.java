package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

@Getter
@Setter
public class EditComponentMarkTypeForm implements Validatable {

    private Long id; // идентификатор
    private String mark; // обозначение

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(mark)) {
            errors.putError("name", ValidatorMsg.REQUIRED);
        }
    }
}