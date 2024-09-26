package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ProductionArea;

import java.util.List;

public interface ProductionAreaRepository extends JpaRepository<ProductionArea, Long> {

    List<ProductionArea> findAllByTechnologicalOrderByCodeAsc(boolean technological);
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByCode(String code);
}