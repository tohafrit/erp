package eco.repository;

import eco.entity.EcoBomItemComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcoBomItemComponentRepository extends JpaRepository<EcoBomItemComponent, Long> {

    EcoBomItemComponent findFirstById(long id);

    List<EcoBomItemComponent> findByBomItem_BomId(long bomId);

    List<EcoBomItemComponent> findByBomItem_BomIdAndKd(long bomId, boolean kd);

    List<EcoBomItemComponent> findByBomItemIdIn(List<Long> bomItemIdList);
}