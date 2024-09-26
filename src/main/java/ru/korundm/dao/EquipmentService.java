package ru.korundm.dao;

import ru.korundm.entity.Equipment;
import ru.korundm.form.search.EquipmentListFilterForm;
import ru.korundm.helper.TabrIn;

import java.util.List;

public interface EquipmentService extends CommonService<Equipment> {

    List<Equipment> getByTableDataIn(TabrIn tableDataIn, EquipmentListFilterForm form);

    long getCountByForm(EquipmentListFilterForm form);

    List<Equipment> findTableData(List<Long> equipmentIdList);
}