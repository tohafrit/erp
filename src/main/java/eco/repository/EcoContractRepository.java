package eco.repository;

import eco.entity.EcoContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoContractRepository extends JpaRepository<EcoContract, Long> {
}