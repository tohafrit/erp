package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ProductUnit;

public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {}