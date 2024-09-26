package eco.repository;

import eco.entity.EcoAllotment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoAllotmentRepository extends JpaRepository<EcoAllotment, Long> {
}