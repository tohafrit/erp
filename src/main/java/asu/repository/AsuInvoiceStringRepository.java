package asu.repository;

import asu.entity.AsuInvoiceString;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsuInvoiceStringRepository extends JpaRepository<AsuInvoiceString, Long> {

    AsuInvoiceString findFirstById(Long id);
}