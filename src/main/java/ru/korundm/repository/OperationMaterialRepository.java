package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.OperationMaterial;

public interface OperationMaterialRepository extends JpaRepository<OperationMaterial, Long> {}