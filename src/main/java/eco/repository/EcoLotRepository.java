package eco.repository;

import eco.entity.EcoLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoLotRepository extends JpaRepository<EcoLot, Long> {

    List<EcoLot> findDistinctByAllotmentList_ProductionShipmentLetterId(Long letterId);
}
