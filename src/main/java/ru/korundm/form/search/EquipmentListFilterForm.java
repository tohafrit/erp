package ru.korundm.form.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public final class EquipmentListFilterForm implements Serializable {

    private String name; // наименование
    private Boolean archive; // архивность
    private Long equipmentTypeId; // идентификатор типа оборудования
    private List<Long> productionAreaIdList = new ArrayList<>(); // список идентификаторов участков

    private List<Long> excludeEquipmentIdList = new ArrayList<>(); // список оборудования для исключения из поиска
}