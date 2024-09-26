package eco.repository;

import eco.entity.EcoTBicStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoTBicStatusRepository extends JpaRepository<EcoTBicStatus, Long> {
}