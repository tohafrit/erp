package eco.repository;

import eco.entity.EcoProductionShipmentLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EcoProductionShipmentLetterRepository extends JpaRepository<EcoProductionShipmentLetter, Long> {

    EcoProductionShipmentLetter findFirstByCreationDateGreaterThanEqualAndCreationDateLessThanEqualOrderByLetterNumberDesc(LocalDateTime dateFrom, LocalDateTime dateTo);
}
