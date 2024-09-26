package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

@Getter
@Setter
public class EditAdministrationOfficeDemandForm implements Validatable {

    private Long id; // идентификатор элемента
    private String reason; // причина
    private String roomNumber; // номер комнаты

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(roomNumber) || roomNumber.length() > 32) {
            errors.putError("roomNumber", ValidatorMsg.RANGE_LENGTH, 1, 32);
        }
        if (StringUtils.isBlank(reason) || reason.length() > 1024) {
            errors.putError("reason", ValidatorMsg.RANGE_LENGTH, 1, 1024);
        }
    }
}