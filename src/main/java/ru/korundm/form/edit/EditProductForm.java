package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.korundm.constant.ObjAttr;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.*;
import ru.korundm.helper.Validatable;
import ru.korundm.helper.ValidatorErrors;

import static ru.korundm.util.FormValidatorUtil.formIdNotValid;

/**
 * Форма добавления/редактирования изделия {@link Product}
 */
@Getter
@Setter
public class EditProductForm implements Validatable {

    private Long id; // идентификатор
    private long lockVersion; // Версия блокировки

    private String conditionalName; // условное наименование
    private String techSpecName; // наименование по ТС
    private String decimalNumber; // ТУ изделия
    private Integer position; // позиция
    private String comment; // комментарий
    private boolean archive; // устаревший
    private boolean serial; // серийное

    private ProductType type; // краткая техническая характеристика
    private ProductLetter letter; // литера
    private User lead; // ведущий
    private ClassificationGroup classificationGroup; // классификационная группа

    @Override
    public void validate(ValidatorErrors errors) {
        if (conditionalName.length() > 128) errors.putError(ObjAttr.CONDITIONAL_NAME, ValidatorMsg.RANGE_LENGTH, 0, 128);
        if (StringUtils.isBlank(techSpecName) || techSpecName.length() > 256) errors.putError(ObjAttr.TECH_SPEC_NAME, ValidatorMsg.RANGE_LENGTH, 1, 256);
        if (decimalNumber.length() > 128) errors.putError(ObjAttr.DECIMAL_NUMBER, ValidatorMsg.RANGE_LENGTH, 0, 128);
        if (position != null && position.toString().length() > 6) errors.putError(ObjAttr.POSITION, "validator.form.fixRange", 0, 999999);
        if (comment.length() > 1024) errors.putError(ObjAttr.COMMENT, ValidatorMsg.RANGE_LENGTH, 0, 1024);
        if (formIdNotValid(type)) errors.putError(ObjAttr.TYPE, ValidatorMsg.REQUIRED);
    }
}