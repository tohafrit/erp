package eco.repository;

import eco.entity.EcoStoragePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoStoragePlaceRepository extends JpaRepository<EcoStoragePlace, Long> {
}
