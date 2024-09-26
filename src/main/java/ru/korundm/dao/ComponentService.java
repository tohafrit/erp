package ru.korundm.dao;

import ru.korundm.entity.Component;
import ru.korundm.form.search.ComponentListFilterForm;
import ru.korundm.form.search.ComponentListReplaceFilterForm;
import ru.korundm.form.search.ProductSpecComponentFilterForm;
import ru.korundm.helper.DynamicObject;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;

import java.time.LocalDate;

public interface ComponentService extends CommonService<Component> {

    TabrResultQuery<Component> queryDataByFilterForm(TabrIn input, ProductSpecComponentFilterForm form);

    <T> TabrResultQuery<T> queryDataByFilterForm(TabrIn input, ComponentListReplaceFilterForm form, Class<T> cl);

    TabrResultQuery<Component> queryDataByFilterForm(TabrIn input, ComponentListFilterForm form, Long selectedId);

    TabrResultQuery<Component> findTableCompReplacementData(TabrIn input, DynamicObject form);

    TabrResultQuery<Component> findTableSpecCompReplacementData(TabrIn input, DynamicObject form);

    Component getByPosition(Integer position);

    void setSubstituteComponent(Long oldId, Long newId);

    int setAnalogReplacement(Long oldId, Long newId, LocalDate replacementDate);
}