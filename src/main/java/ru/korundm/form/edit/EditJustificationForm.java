package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.Company;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class EditJustificationForm implements Serializable, Validator {

    private String docType; // тип страницы - создание/редактирование
    private Long id; // идентификатор обоснования
    private String typeId; // тип обоснования
    private String name; // название обоснования
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate date; // дата утверждения обоснования
    private Company company; // организация-соисполнитель
    private String note; // комментарий к обоснованию
    //private FileStorage fileStorage; // файл
    private MultipartFile file; // файл

    @Override
    public boolean supports(Class<?> clazz) {
        return this.getClass().equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // FormValidatorUtil.validateMultipartFile(errors, getFile(), "file", Boolean.FALSE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", ValidatorMsg.REQUIRED);
        if (date == null) {
            errors.rejectValue("date", ValidatorMsg.REQUIRED, new Object[] {}, null);
        }
    }
}