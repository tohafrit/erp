package ru.korundm.dao;

import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitProductionArea;

import java.util.List;

public interface EquipmentUnitProductionAreaService extends CommonService<EquipmentUnitProductionArea> {

    void deleteAllByEquipmentUnit(EquipmentUnit equipmentUnit);

    void deleteAllByEquipment(Equipment equipment);

    void deleteAllByEquipmentUnitIn(List<EquipmentUnit> equipmentUnitList);
}