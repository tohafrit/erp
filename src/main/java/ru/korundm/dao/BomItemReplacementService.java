package ru.korundm.dao;

import ru.korundm.entity.BomItemReplacement;

import java.util.List;

public interface BomItemReplacementService extends CommonService<BomItemReplacement> {

    List<BomItemReplacement> getAllByBomItemId(Long bomItemId);

    List<BomItemReplacement> getAllByBomId(Long bomId);

    boolean existsByComponentIdAndBomId(Long componentId, Long bomId);

    List<BomItemReplacement> getAllByComponentId(Long componentId);
}