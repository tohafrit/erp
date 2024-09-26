package eco.repository;

import eco.entity.EcoContractSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoContractSectionRepository extends JpaRepository<EcoContractSection, Long> {
}
