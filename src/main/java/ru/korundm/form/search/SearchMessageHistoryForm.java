package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.korundm.constant.BaseConstant;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Форма для поиска сообщений
 * @author zhestkov_an
 * Date:   11.01.2019
 */
@Getter
@Setter
public class SearchMessageHistoryForm implements Serializable {

    private Long type; // тип сообщения
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate dateDeparture; // дата отправки
    private Long user; // пользователь
}
