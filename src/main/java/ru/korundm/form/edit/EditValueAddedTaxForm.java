package ru.korundm.form.edit;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;

import java.time.LocalDate;

@Getter @Setter
public class EditValueAddedTaxForm implements Validatable {

    private Long id; // идентификатор
    private String periodName; // наименование периода действия ставки
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate dateFrom; // дата начала периода действия ставки
    private Double amount; // величина ставки в %
    //private FileStorage fileStorage; // файл
    private MultipartFile file; // файл

    @Override
    public void validate(@NotNull ValidatorErrors errors) {
        /* TODO подключить валидатор файлов */
        // FormValidatorUtil.validateMultipartFile(errors, getFile(), "file", Boolean.FALSE);
        if (StringUtils.isBlank(getPeriodName())) {
            errors.putError("periodName", ValidatorMsg.REQUIRED);
        }
        if (dateFrom == null) {
            errors.putError("dateFrom", ValidatorMsg.REQUIRED);
        }
        if (amount == null || amount.isNaN()) {
            errors.putError("amount", ValidatorMsg.REQUIRED);
        }
    }
}