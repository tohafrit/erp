package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitEventType;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;
import ru.korundm.constant.BaseConstant;

import java.time.LocalDate;

import static ru.korundm.util.FormValidatorUtil.*;

@Getter @Setter
public class EditEquipmentUnitEventForm implements Validatable {

    private Long id; // идентификатор
    private EquipmentUnit equipmentUnit; // единица оборудования
    private EquipmentUnitEventType equipmentUnitEventType; // тип события
    private String name; // наименование
    private String commentary; // комментарий
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate dateEventOn; // дата события для валидации

    @Override
    public void validate(ValidatorErrors errors) {
        if (formIdNotValid(getEquipmentUnit())) {
            errors.putError("equipmentUnit", ValidatorMsg.REQUIRED);
        }
        if (formIdNotValid(getEquipmentUnitEventType())) {
            errors.putError("equipmentUnitEventType", ValidatorMsg.REQUIRED);
        }
        if (getDateEventOn() == null) {
            errors.putError("dateEventOn", "validator.form.dateRequired");
        }
    }
}