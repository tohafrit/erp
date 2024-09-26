package eco.repository;

import eco.entity.EcoUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoUnitRepository extends JpaRepository<EcoUnit, Long>{
}