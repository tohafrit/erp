package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.Documentation;
import ru.korundm.entity.MenuItem;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EditDocumentationForm implements Validatable {

    private Long id; // идентификатор
    private String name; // наименование
    private String content; // описание
    private MenuItem menuItem; // пункт меню
    private Documentation parent; // категория-родитель

    private List<Long> notParentAllowedList = new ArrayList<>(); // список недоступных идентификаторов для родителя
    private List<MenuItem> notMenuItemAllowedList = new ArrayList<>(); // список использованных пунктов меню
    private List<Long> seeAlsoIdList = new ArrayList<>(); // список связанных документов

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name)) {
            errors.putError("name", ValidatorMsg.REQUIRED);
        }
        if (notParentAllowedList.stream().anyMatch(id -> getParent() != null && id.equals(getParent().getId()))) {
            errors.putError("parent", "validator.form.parentNotAllowed");
        }
        if (notMenuItemAllowedList.stream().anyMatch(menuItem -> getMenuItem() != null && menuItem.getId().equals(getMenuItem().getId()))) {
            errors.putError("menuItem", "validator.form.itemAlreadyUsed");
        }
        if (StringUtils.isBlank(content)) {
            errors.putError("content", ValidatorMsg.REQUIRED);
        }
    }
}