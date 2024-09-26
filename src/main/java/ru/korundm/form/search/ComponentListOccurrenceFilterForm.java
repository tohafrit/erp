package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Форма для поиска вхождений для компонентов
 * @author pakhunov_an
 * Date:   29.08.2020
 */
@Getter
@Setter
@ToString
public final class ComponentListOccurrenceFilterForm implements Serializable {

    private boolean active; // выпускаемые
    private boolean archive; // устаревшие
    private boolean lastApprove; // последний подтвержденный
    private boolean lastAccept; // последний принятый
    private String approveSearchText; // текст поиска по подтвержденным
    private String acceptSearchText; // текст поиска по принятым
}