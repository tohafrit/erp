package eco.repository;

import eco.entity.EcoSnapshotCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoSnapshotCalculationRepository extends JpaRepository<EcoSnapshotCalculation, Long> {
}