package eco.repository;

import eco.entity.EcoProductKind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoProductKindRepository extends JpaRepository<EcoProductKind, Long> {
}