package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {}