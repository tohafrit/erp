package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.BomItem;

import java.util.List;

public interface BomItemRepository extends JpaRepository<BomItem, Long> {

    List<BomItem> findAllByBomId(Long bomId);

    boolean existsByBomIdAndComponentId(Long bomId, Long componentId);

    void deleteAllByBomId(Long bomId);

    boolean existsByBomId(Long bomId);

    List<BomItem> findAllByComponentId(Long componentId);
}