package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {}