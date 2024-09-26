package eco.repository;

import eco.entity.EcoProductionLotSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoProductionLotSpecRepository extends JpaRepository<EcoProductionLotSpec, Long> {
}