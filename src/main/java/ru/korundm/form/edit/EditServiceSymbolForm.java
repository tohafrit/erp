package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

@Getter
@Setter
public class EditServiceSymbolForm implements Validatable {

    private Long id; // идентификатор
    private String name; // наименование
    private String code; // кодовое представление обозначения
    private boolean technologicalProcess; // использование в техпроцессах
    private boolean operationCard; // использование в операционных картах
    private boolean routeMap; // использование в маршрутных картах
    private String description; // описание

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name) || name.length() > 16) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 16);
        }
        if (StringUtils.isBlank(code) || code.length() > 16) {
            errors.putError("code", ValidatorMsg.RANGE_LENGTH, 1, 16);
        }
        if (StringUtils.isBlank(description) || description.length() > 512) {
            errors.putError("description", ValidatorMsg.RANGE_LENGTH, 1, 512);
        }
    }
}