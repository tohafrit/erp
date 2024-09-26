package eco.repository;

import eco.entity.EcoBomSpecItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EcoBomSpecItemRepository extends JpaRepository<EcoBomSpecItem, Long> {

    boolean existsByIdAndProductIdAndBomId(Long id, Long productId, Long versionId);

    EcoBomSpecItem getFirstByIdAndProductIdAndBomId(Long id, Long productId, Long versionId);
}