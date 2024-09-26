package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ProductionLotSpec;

public interface ProductionLotSpecRepository extends JpaRepository<ProductionLotSpec, Long> {}