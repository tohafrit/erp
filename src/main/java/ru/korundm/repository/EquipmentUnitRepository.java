package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;

import java.util.List;

public interface EquipmentUnitRepository extends JpaRepository<EquipmentUnit, Long> {

    List<EquipmentUnit> findAllByEquipment(Equipment equipment);

    List<EquipmentUnit> findAllByEquipmentId(Long equipmentId);

    List<EquipmentUnit> findAllByIdIsNotInAndEquipment(List<Long> idList, Equipment equipment);

    @Transactional
    void deleteAllByEquipment(Equipment equipment);
}