package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.util.CommonUtil;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Форма для поиска расчетов закупочной ведомости
 * @author pakhunov_an
 * Date:   20.08.2019
 */
@Getter
@Setter
public class SearchPurchaseForm implements Serializable, Validator {

    private String name; // наименование
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate planDateFrom; // плановая дата с
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate planDateTo; // плановая дата по
    private String note; // комментарий
    private Long createdBy; // создал
    private Long launchId; // запуск

    @Override
    public boolean supports(Class<?> type) {
        return this.getClass().equals(type);
    }

    @Override
    public void validate(Object o, Errors errors) {
        if (CommonUtil.dateFromMoreThenTo(getPlanDateFrom(), getPlanDateTo())) {
            errors.rejectValue("planDateTo", ValidatorMsg.DATE_MUST_BE_MORE);
        }
    }
}