package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.ProductionDefect;

public interface ProductionDefectRepository extends JpaRepository<ProductionDefect, Long> {}