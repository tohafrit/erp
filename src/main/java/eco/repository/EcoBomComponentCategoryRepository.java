package eco.repository;

import eco.entity.EcoBomComponentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoBomComponentCategoryRepository extends JpaRepository<EcoBomComponentCategory, Long> {

    EcoBomComponentCategory findFirstById(long id);
}