package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.BomSpecItem;

import java.util.List;

public interface BomSpecItemRepository extends JpaRepository<BomSpecItem, Long> {

    List<BomSpecItem> findAllByBomId(long bomId);

    boolean existsByBomIdAndProductId(Long bomId, Long productId);
}