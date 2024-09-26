package eco.repository;

import eco.entity.EcoLotGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoLotGroupRepository extends JpaRepository<EcoLotGroup, Long> {

    List<EcoLotGroup> findDistinctByLotList_AllotmentList_ProductionShipmentLetterId(Long letterId);
}
