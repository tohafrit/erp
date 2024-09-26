package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ProductionWarehouse;

public interface ProductionWarehouseRepository extends JpaRepository<ProductionWarehouse, Long> {

    boolean existsByCodeAndIdNot(Integer code, Long id);
    boolean existsByCode(Integer code);
}