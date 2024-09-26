package ru.korundm.dao;

import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitEventMeasure;

import java.util.List;

public interface EquipmentUnitEventMeasureService extends CommonService<EquipmentUnitEventMeasure> {

    void deleteAllByEquipment(Equipment equipment);

    void deleteAllByEquipmentUnitIn(List<EquipmentUnit> equipmentUnitList);

    void deleteAllByEquipmentUnitEventId(Long id);
}