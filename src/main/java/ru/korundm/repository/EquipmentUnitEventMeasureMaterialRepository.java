package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitEventMeasureMaterial;

import java.util.List;

public interface EquipmentUnitEventMeasureMaterialRepository extends JpaRepository<EquipmentUnitEventMeasureMaterial, Long> {

    @Transactional
    void deleteAllByEquipmentUnitEventMeasure_EquipmentUnitEvent_EquipmentUnit_Equipment(Equipment equipment);

    @Transactional
    void deleteAllByEquipmentUnitEventMeasure_EquipmentUnitEvent_EquipmentUnitIn(List<EquipmentUnit> equipmentUnitList);

    @Transactional
    void deleteAllByEquipmentUnitEventMeasure_EquipmentUnitEvent_Id(Long id);
}