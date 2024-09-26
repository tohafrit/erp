package ru.korundm.dao;

import ru.korundm.entity.BomItem;
import ru.korundm.entity.Component;
import ru.korundm.form.search.ComponentListOccurrenceFilterForm;

import java.util.List;

public interface BomItemService extends CommonService<BomItem> {

    List<BomItem> getAllByBomId(Long bomId);

    boolean existsByBomIdAndComponentId(Long bomId, Long componentId);

    void deleteAllByBomId(Long bomId);

    boolean existsByBomId(Long bomId);

    List<BomItem> getAllByComponentId(Long componentId);

    void deleteAll(List<BomItem> objectList);

    List<BomItem> queryDataByFilterForm(ComponentListOccurrenceFilterForm form, List<Component> componentReplacementList);
}