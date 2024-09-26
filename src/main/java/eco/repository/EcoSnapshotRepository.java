package eco.repository;

import eco.entity.EcoSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoSnapshotRepository extends JpaRepository<EcoSnapshot, Long> {
}