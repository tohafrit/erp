package ru.korundm.dao;

import ru.korundm.entity.ProductKind;

public interface ProductKindService extends CommonService<ProductKind> {

    Long getMaxId();
}