package eco.repository;

import eco.entity.EcoProductionLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoProductionLotRepository extends JpaRepository<EcoProductionLot, Long> {
}