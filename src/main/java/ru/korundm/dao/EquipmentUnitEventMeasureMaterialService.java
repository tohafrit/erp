package ru.korundm.dao;

import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitEventMeasureMaterial;

import java.util.List;

public interface EquipmentUnitEventMeasureMaterialService extends CommonService<EquipmentUnitEventMeasureMaterial> {

    void deleteAllByEquipment(Equipment equipment);

    void deleteAllByEquipmentUnitIn(List<EquipmentUnit> equipmentUnitList);

    void deleteAllByEquipmentUnitEventId(Long id);
}