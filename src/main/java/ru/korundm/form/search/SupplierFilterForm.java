package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Форма для поиска поставщиков
 * @author berezin_mm
 * Date:   29.09.2020
 */
@Getter
@Setter
@ToString
public class SupplierFilterForm implements Serializable {

    private String name; // наименование
    private String inn; // ИНН
    private String kpp; // КПП
}
