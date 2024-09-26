package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.enumeration.TripType;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
@Setter
public class EditTripForm implements Validatable {

    private Long id; // идентификатор элемента
    private String name; // название
    private Long employeeId; // идентификатор сотрудника
    private Long chiefId; // идентификатор начальника
    private TripType type; // тип командировки
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate date; // дата командировки
    private Boolean period; // указан период
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate dateTo; // дата окончания командировки
    @DateTimeFormat(pattern = BaseConstant.TIME_PATTERN)
    private LocalTime timeFrom; // время с
    @DateTimeFormat(pattern = BaseConstant.TIME_PATTERN)
    private LocalTime timeTo; // время по

    @Override
    public void validate(ValidatorErrors errors) {
        if (StringUtils.isBlank(getName())) {
            errors.putError("name", ValidatorMsg.REQUIRED);
        }
        if (date == null) {
            errors.putError("date", ValidatorMsg.REQUIRED);
        } else if (DAYS.between(LocalDate.now(), date) < 0) {
            errors.putError("date", "validator.form.backdated");
        } else if (period != null && getPeriod() && getDateTo() != null && DAYS.between(getDate(), getDateTo()) <= 0) {
            errors.putError("dateTo", ValidatorMsg.DATE_MUST_BE_MORE);
        }
        if (timeFrom == null) {
            errors.putError("timeFrom", ValidatorMsg.REQUIRED);
        }
        if (timeTo == null) {
            errors.putError("timeTo", ValidatorMsg.REQUIRED);
        } else if (timeTo.isBefore(timeFrom)) {
            errors.putError("timeTo", "validator.form.time");
        }
    }
}