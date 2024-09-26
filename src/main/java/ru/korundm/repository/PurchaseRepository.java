package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {}