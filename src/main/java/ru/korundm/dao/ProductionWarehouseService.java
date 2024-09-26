package ru.korundm.dao;

import ru.korundm.entity.ProductionWarehouse;

public interface ProductionWarehouseService extends CommonService<ProductionWarehouse> {

    boolean existsByCodeAndIdNot(Integer code, Long id);
}