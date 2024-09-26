package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.ValidatorMsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Форма для описания полей редактирования трудоемкости для техпроцесса
 * @author pakhunov_an
 * Date:   15.10.2019
 */
@Getter @Setter
public class EditTechnologicalProcessLaboriousnessForm implements Serializable, Validator {

    private List<LaboriousnessForm> laboriousnessFormList = new ArrayList<>(); // список трудоемкостей

    @Getter @Setter
    // Форма для единицы TechnologicalProcessLaboriousness
    public static class LaboriousnessForm {
        private Long id; // идентификатор
        private String laboriousnessValue; // значение трудоемкости
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        for (var laboriousnessForm : laboriousnessFormList) {
            if (StringUtils.isBlank(laboriousnessForm.getLaboriousnessValue())) {
                errors.rejectValue("laboriousnessFormList", ValidatorMsg.REQUIRED);
            } else if (laboriousnessForm.getLaboriousnessValue().indexOf(BaseConstant.UNDERSCORE) > 0) {
                errors.rejectValue("laboriousnessFormList", ValidatorMsg.REQUIRED);
            }
        }
    }
}