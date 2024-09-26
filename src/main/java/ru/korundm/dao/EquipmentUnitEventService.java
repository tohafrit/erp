package ru.korundm.dao;

import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitEvent;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;

import java.util.List;

public interface EquipmentUnitEventService extends CommonService<EquipmentUnitEvent> {

    void deleteAllByEquipmentUnit(EquipmentUnit equipmentUnit);

    void deleteAllByEquipmentUnitIn(List<EquipmentUnit> equipmentUnitList);

    void deleteAllByEquipment(Equipment equipment);

    TabrResultQuery<EquipmentUnitEvent> getByTableDataIn(TabrIn input);

    long getCount();

    int getPageById(long id);
}