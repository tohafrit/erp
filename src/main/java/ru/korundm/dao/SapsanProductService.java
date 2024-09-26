package ru.korundm.dao;

import ru.korundm.entity.SapsanProduct;

public interface SapsanProductService extends CommonService<SapsanProduct> {

    SapsanProduct getByPrefix(String prefix);
}