package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EditOperationMaterialForm implements Validatable {

    private Long id; // идентификатор
    private String name; // название
    private List<Long> workTypeList = new ArrayList<>(); // список операций

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(name)) {
            errors.putError("name", ValidatorMsg.REQUIRED);
        }
        if (getWorkTypeList().isEmpty()) {
            errors.putError("workTypeList", ValidatorMsg.REQUIRED);
        }
    }
}