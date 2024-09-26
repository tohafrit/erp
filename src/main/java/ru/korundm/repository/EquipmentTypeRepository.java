package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.EquipmentType;

import java.util.List;

public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {

    EquipmentType findEquipmentTypeByName(String name);

    List<EquipmentType> findAllByNameInOrderByIdAsc(List<String> equipmentNameList);
}