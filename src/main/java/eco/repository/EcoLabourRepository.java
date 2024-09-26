package eco.repository;

import eco.entity.EcoLabour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoLabourRepository extends JpaRepository<EcoLabour, Long> {
}