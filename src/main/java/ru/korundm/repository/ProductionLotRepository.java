package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ProductionLot;

public interface ProductionLotRepository extends JpaRepository<ProductionLot, Long> {}