package eco.repository;

import eco.entity.EcoMvInOutDocReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoMvInOutDocReferenceRepository extends JpaRepository<EcoMvInOutDocReference, Long> {
}
