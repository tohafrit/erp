package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Форма для поиска компонентов для замены
 * @author mazur_ea
 * Date:   07.08.2020
 */
@Getter
@Setter
@ToString
public final class ComponentReplacementListFilterForm implements Serializable {

    private String name; // наименование
    private Integer position; // позиция
    private boolean showReplaceable; // показывать заменяемые
    private String description; // описание
    private List<Long> categoryIdList; // список идентификаторов категорий
    private List<Long> producerIdList; // список идентификаторов производителей
}