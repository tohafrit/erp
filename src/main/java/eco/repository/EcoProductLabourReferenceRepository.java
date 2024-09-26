package eco.repository;

import eco.entity.EcoProductLabourId;
import eco.entity.EcoProductLabourReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoProductLabourReferenceRepository extends JpaRepository<EcoProductLabourReference, EcoProductLabourId> {
}