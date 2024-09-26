package asu.repository;

import asu.entity.AsuInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuInvoiceRepository extends JpaRepository<AsuInvoice, Long> {
}