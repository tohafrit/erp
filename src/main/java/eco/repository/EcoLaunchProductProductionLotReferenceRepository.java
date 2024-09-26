package eco.repository;

import eco.entity.EcoLaunchProductProductionLotId;
import eco.entity.EcoLaunchProductProductionLotReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoLaunchProductProductionLotReferenceRepository extends JpaRepository<EcoLaunchProductProductionLotReference, EcoLaunchProductProductionLotId> {
}