package eco.repository;

import eco.entity.EcoBomComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoBomComponentRepository extends JpaRepository<EcoBomComponent, Long> {

    EcoBomComponent findFirstById(long id);
}