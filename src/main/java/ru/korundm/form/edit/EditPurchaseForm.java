package ru.korundm.form.edit;

import eco.entity.EcoLaunch;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.korundm.constant.BaseConstant;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Форма для редактирования расчетов закупочной ведомости
 * @author pakhunov_an
 * Date:   21.08.2019
 */
@Getter @Setter
public class EditPurchaseForm implements Serializable {

    private Long id; // идентификатор
    private String name; // наименование
    @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN)
    private LocalDate planDate; // плановая дата
    private EcoLaunch launch; // запуск
    private List<Long> launchList; // предыдущие запуски
    private Long type; // тип версии ЗС изделия
    private String note; // комментарий
}