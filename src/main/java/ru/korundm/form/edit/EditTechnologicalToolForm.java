package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.entity.User;
import ru.korundm.enumeration.TechnologicalToolType;
import ru.korundm.helper.Validatable;
import ru.korundm.helper.ValidatorErrors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EditTechnologicalToolForm implements Validatable {

    private Long id; // id
    private String sign; // обозначение
    private String name; // наименование
    private String appointment; // назначение
    private String link; // ссылка на файл
    private String state; // состояние
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate issueDate; // дата выпуска
    private User user; // кем выпущен
    private TechnologicalToolType type; // тип
    private List<Long> productionAreaIdList = new ArrayList<>(); // список идентификаторов участков

    @Override
    public void validate(@NotNull ValidatorErrors errors) {
        if (sign.length() > 128) {
            errors.putError("sign", ValidatorMsg.RANGE_LENGTH, 0, 128);
        }
        if (StringUtils.isBlank(name) || name.length() > 512) {
            errors.putError("name", ValidatorMsg.RANGE_LENGTH, 1, 512);
        }
        if (appointment.length() > 512) {
            errors.putError("appointment", ValidatorMsg.RANGE_LENGTH, 0, 512);
        }
        if (link.length() > 512) {
            errors.putError("link", ValidatorMsg.RANGE_LENGTH, 0, 512);
        }
        if (state.length() > 512) {
            errors.putError("state", ValidatorMsg.RANGE_LENGTH, 0, 512);
        }
        if (type == null) {
            errors.putError("type", ValidatorMsg.REQUIRED);
        }
    }
}