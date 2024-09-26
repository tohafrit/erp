package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Форма поиска данных для спецификации изделий
 * @author mazur_ea
 * Date:   13.06.2020
 */
@Getter
@Setter
@ToString
public final class ProductSpecComponentFilterForm implements Serializable {

    private boolean newComponent; // отображать только новые
    private String name; // наименование
    private Integer position; // позиция
    private boolean showReplaceable; // показывать заменяемые
    private String description; // описание
    private List<Long> categoryIdList; // список идентификаторов категорий
    private List<Long> producerIdList; // список идентификаторов производителей
}