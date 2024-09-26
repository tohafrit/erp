package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import java.util.ArrayList;
import java.util.List;

/**
 * Форма для редактирования принтера
 * @author berezin_mm
 * Date:   24.09.2019
 */
@Getter
@Setter
public class EditPrinterForm implements Validatable {

    private Long id; // идентификатор принтера
    private String name; // название принтера
    private String ip; // ip-адрес
    private Integer port; // порт
    private String description; // описание
    private List<Long> userIdList = new ArrayList<>(); // список пользователей

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name)) {
            errors.putError("name", ValidatorMsg.REQUIRED);
        }
    }
}
