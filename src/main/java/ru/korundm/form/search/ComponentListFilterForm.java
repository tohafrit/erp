package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Форма для поиска компонентов
 * @author mazur_ea
 * Date:   09.07.2020
 */
@Getter
@Setter
@ToString
public final class ComponentListFilterForm implements Serializable {

    private String name; // наименование по ТС
    private Integer position; // позиция
    private String description; // описание
    private Long lifecycleId; // жизненный цикл
    private List<Long> categoryIdList = new ArrayList<>(); // список идентификаторов категорий
    private List<Long> producerIdList = new ArrayList<>(); // список идентификаторов производителей

    // Для новых компонентов
    private Long launchId; // запуск
    private String product; // изделие
}