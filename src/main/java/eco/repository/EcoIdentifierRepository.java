package eco.repository;

import eco.entity.EcoIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoIdentifierRepository extends JpaRepository<EcoIdentifier, Long> {
}
