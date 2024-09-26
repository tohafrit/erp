package eco.repository;

import eco.entity.EcoMatValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoMatValueRepository extends JpaRepository<EcoMatValue, Long> {

    EcoMatValue findBySerialNumber(String serialNumber);
}
