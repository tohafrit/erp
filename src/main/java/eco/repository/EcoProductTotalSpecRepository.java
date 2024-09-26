package eco.repository;

import eco.entity.EcoProductTotalSpec;
import eco.entity.EcoProductTotalSpecId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoProductTotalSpecRepository extends JpaRepository<EcoProductTotalSpec, EcoProductTotalSpecId> {
}