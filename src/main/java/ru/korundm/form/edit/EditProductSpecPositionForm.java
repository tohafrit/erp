package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

/**
 * Форма добавления позиции ко вхождению в спецификацию изделия
 * @author mazur_ea
 * Date:   16.06.2020
 */
@Getter
@Setter
@ToString
public class EditProductSpecPositionForm implements Validatable {

    private Long id;
    private long lockVersion; // версия блокировки
    private String firmware; // прошивка

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isNotBlank(firmware) && firmware.length() > 20) {
            errors.putError("firmware", ValidatorMsg.RANGE_LENGTH, 0, 20);
        }
    }
}