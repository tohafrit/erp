package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

@Getter
@Setter
public class EditProductionAreaForm implements Validatable {

    private Long id; // идентификатор
    private String name; // наименование
    private String code; // код
    private boolean technological; // технологическая

    @Override
    public void validate(@NotNull ValidatorErrors errors) {
        if (StringUtils.isBlank(name) || name.length() > 64) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 64);
        }
        if (StringUtils.isBlank(code) || code.length() > 3) {
            errors.putError("code", ValidatorMsg.RANGE_LENGTH, 1, 3);
        }
    }
}