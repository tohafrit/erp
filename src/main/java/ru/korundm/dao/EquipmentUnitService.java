package ru.korundm.dao;

import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;

import java.util.List;

public interface EquipmentUnitService extends CommonService<EquipmentUnit> {

    List<EquipmentUnit> getAllByEquipment(Equipment equipment);

    List<EquipmentUnit> getAllByEquipmentId(Long equipmentId);

    List<EquipmentUnit> getAllByIdIsNotInAndEquipment(List<Long> idList, Equipment equipment);

    void deleteAllByEquipment(Equipment equipment);

    void deleteAll(List<EquipmentUnit> equipmentUnitList);

    EquipmentUnit getByAreaIdAndEquipmentTypeId(Long areaId, Long equipmentTypeId);

    TabrResultQuery<EquipmentUnit> getByTableDataIn(TabrIn input);
}