package eco.repository;

import eco.entity.EcoPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoPaymentRepository extends JpaRepository<EcoPayment, Long> {
}
