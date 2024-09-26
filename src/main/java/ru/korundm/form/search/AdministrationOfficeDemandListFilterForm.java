package ru.korundm.form.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import ru.korundm.constant.BaseConstant;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class AdministrationOfficeDemandListFilterForm {

    private List<Long> userIdList; // пользователь
    private String roomNumber; // номер комнаты
    private String reason; // причина

    @DateTimeFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    @JsonFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    private LocalDateTime requestDateFrom; // дата с

    @DateTimeFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    @JsonFormat(pattern = BaseConstant.DATE_TIME_PATTERN)
    private LocalDateTime requestDateTo; // дата по
}