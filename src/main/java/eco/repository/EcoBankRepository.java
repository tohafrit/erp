package eco.repository;

import eco.entity.EcoBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoBankRepository extends JpaRepository<EcoBank, Long> {
}
