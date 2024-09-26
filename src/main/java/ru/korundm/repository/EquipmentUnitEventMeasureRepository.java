package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitEventMeasure;

import java.util.List;

public interface EquipmentUnitEventMeasureRepository extends JpaRepository<EquipmentUnitEventMeasure, Long> {

    @Transactional
    void deleteAllByEquipmentUnitEvent_EquipmentUnit_Equipment(Equipment equipment);

    @Transactional
    void deleteAllByEquipmentUnitEvent_EquipmentUnitIn(List<EquipmentUnit> equipmentUnitList);

    @Transactional
    void deleteAllByEquipmentUnitEvent_Id(Long id);
}