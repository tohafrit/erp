package ru.korundm.form.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.constant.BaseConstant;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Форма поиска данных по пользовательским комментариям изделия
 * @author zhestksov_an
 * Date:   06.10.2020
 */
@Getter
@Setter
@ToString
public class ProductCommentFilterForm implements Serializable {

    private Long createdBy; // создал
    @JsonFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate createDateFrom; // дата создания с
    @JsonFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate createDateTo; // дата создания по
    private String comment; // комментарий
}