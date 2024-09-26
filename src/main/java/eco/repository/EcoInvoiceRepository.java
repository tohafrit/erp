package eco.repository;

import eco.entity.EcoInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "ecoInvoiceRepository")
public interface EcoInvoiceRepository extends JpaRepository<EcoInvoice, Long> {
}