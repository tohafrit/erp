package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.ComponentCategory;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class EditComponentCategoryForm implements Validatable {

    private Long id; // идентификатор
    private String name; // наименование
    private String description; // описание
    private ComponentCategory parent; // категория-родитель

    private List<Long> notParentAllowedList = Collections.emptyList(); // список недоступных идентификаторов для родителя

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name) || name.length() > 64) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 64);
        }
        if (description != null && description.length() > 512) {
            errors.putError("description", ValidatorMsg.RANGE_LENGTH, 0, 512);
        }
        if (notParentAllowedList.stream().anyMatch(id -> parent != null && Objects.equals(id, parent.getId()))) {
            errors.putError("parent", "validator.form.parentNotAllowed");
        }
    }
}