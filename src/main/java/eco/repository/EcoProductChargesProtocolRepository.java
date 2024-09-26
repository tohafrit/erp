package eco.repository;

import eco.entity.EcoProductChargesProtocol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoProductChargesProtocolRepository extends JpaRepository<EcoProductChargesProtocol, Long> {
}
