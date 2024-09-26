package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitProductionArea;

import java.util.List;

public interface EquipmentUnitProductionAreaRepository extends JpaRepository<EquipmentUnitProductionArea, Long> {

    @Transactional
    void deleteAllByEquipmentUnit_Equipment(Equipment equipment);

    @Transactional
    void deleteAllByEquipmentUnit(EquipmentUnit equipmentUnit);

    @Transactional
    void deleteAllByEquipmentUnitIn(List<EquipmentUnit> equipmentUnitList);
}