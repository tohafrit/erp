package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.JustificationTechnicalProcess;
import ru.korundm.entity.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static ru.korundm.util.FormValidatorUtil.assertFormId;


/**
 * Форма для описания полей редактирования трудоемкости для техпроцесса
 * @author pakhunov_an
 * Date:   04.03.2019
 */
@Getter
@Setter
public class EditProductTechnicalProcessForm implements Serializable, Validator {

    private Long id; // идентификатор техпроцесса
    private String name; // наименование техпроцесса
    private String source; // название документа с техпроцессом
    private JustificationTechnicalProcess justification; // обоснование
    private Product product; // изделие
    private Boolean approved; // одобрен
    private List<Long> deleteLaboriousnessList = new ArrayList<>(); // список трудоемкостей на удаление
    private List<LaboriousnessForm> laboriousnessFormList = new ArrayList<>(); // список трудоемкостей

    @Getter @Setter
    // Форма для единицы Laboriousness
    public static class LaboriousnessForm {
        private Long id; // идентификатор
        private Long workTypeId; // идентификатор вида работ
        private String value; // значение трудоемкости
        private String number; // номер по техническому процессу (ТП)
        private Boolean withPackage; // с упаковкой или без
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", ValidatorMsg.REQUIRED);
        if (assertFormId(getProduct()) == null) {
            errors.rejectValue("product", ValidatorMsg.REQUIRED);
        }
        if (laboriousnessFormList.stream()
                .anyMatch(laboriousnessForm -> StringUtils.isBlank(laboriousnessForm.getNumber()))) {
            errors.rejectValue("justification", "validator.form.allFieldsRequired");
        }
        if (approved) {
            if (laboriousnessFormList.stream()
                    .anyMatch(laboriousnessForm -> StringUtils.isBlank(laboriousnessForm.getValue()))) {
                errors.rejectValue("justification", "validator.form.productTechnicalProcess.valueRequired");
            }
        }
    }
}