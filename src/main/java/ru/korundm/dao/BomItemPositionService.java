package ru.korundm.dao;

import ru.korundm.entity.BomItemPosition;

import java.util.List;

public interface BomItemPositionService extends CommonService<BomItemPosition> {

    List<BomItemPosition> getAllByBomItemId(Long bomItemId);

    long getCountAllByBomItemId(Long bomItemId);
}