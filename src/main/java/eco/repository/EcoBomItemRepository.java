package eco.repository;

import eco.entity.EcoBomItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoBomItemRepository extends JpaRepository<EcoBomItem, Long> {

    EcoBomItem findFirstById(long id);

    List<EcoBomItem> findDistinctByBomIdAndEcoBomItemComponentList_Kd(Long bomId, boolean kd);
}