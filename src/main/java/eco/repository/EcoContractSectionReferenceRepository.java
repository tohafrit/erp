package eco.repository;

import eco.entity.EcoContractSectionReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoContractSectionReferenceRepository extends JpaRepository<EcoContractSectionReference, Long> {
}