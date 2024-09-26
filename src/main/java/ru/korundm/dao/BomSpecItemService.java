package ru.korundm.dao;

import ru.korundm.entity.BomSpecItem;

import java.util.List;

public interface BomSpecItemService extends CommonService<BomSpecItem> {

    List<BomSpecItem> getAllByBomId(Long bomId);

    boolean existsByBomIdAndProductId(Long bomId, Long productId);
}