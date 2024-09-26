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
public class EditReasonChangeForm implements Validatable {

    private Long id; // идентификатор
    private Integer code; // код
    private String reason; // причина

    @Override
    public void validate(@NotNull ValidatorErrors errors) {
        if (code == null) {
            errors.putError("code", "javax.validation.constraints.NotNull.message");
        }
        if (StringUtils.isNotBlank(reason) && reason.length() > 256) {
            errors.putError("reason", ValidatorMsg.RANGE_LENGTH, 1, 256);
        }
    }
}