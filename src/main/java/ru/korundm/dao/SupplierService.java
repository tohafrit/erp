package ru.korundm.dao;

import ru.korundm.entity.Supplier;
import ru.korundm.form.search.SupplierFilterForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;

public interface SupplierService extends CommonService<Supplier> {

    TabrResultQuery<Supplier> queryDataByFilterForm(TabrIn tableDataIn, SupplierFilterForm form);

    long getCount();
}