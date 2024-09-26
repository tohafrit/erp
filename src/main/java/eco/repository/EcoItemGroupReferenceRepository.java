package eco.repository;

import eco.entity.EcoItemGroupReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoItemGroupReferenceRepository extends JpaRepository<EcoItemGroupReference, Long> {
}