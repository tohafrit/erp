package eco.repository;

import eco.entity.EcoStoreCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoStoreCellRepository extends JpaRepository<EcoStoreCell, Long> {
}
