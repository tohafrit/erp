package ru.korundm.dao;

import ru.korundm.entity.EquipmentType;

import java.util.List;

public interface EquipmentTypeService extends CommonService<EquipmentType> {

    List<EquipmentType> getAllByType(List<String> equipmentNameList);

    EquipmentType readByName(String name);
}