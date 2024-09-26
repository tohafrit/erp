package asu.repository;

import asu.entity.AsuSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuSupplierRepository extends JpaRepository<AsuSupplier, Long> {
}