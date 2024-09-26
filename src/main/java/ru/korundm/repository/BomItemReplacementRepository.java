package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.BomItemReplacement;

import java.util.List;

public interface BomItemReplacementRepository extends JpaRepository<BomItemReplacement, Long> {

    List<BomItemReplacement> findAllByBomItemId(Long bomItemId);

    List<BomItemReplacement> findAllByBomItem_BomId(Long bomId);

    boolean existsByComponentIdAndBomItem_BomId(Long componentId, Long bomId);

    List<BomItemReplacement> findAllByComponentId(Long componentId);
}