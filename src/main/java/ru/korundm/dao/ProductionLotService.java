package ru.korundm.dao;

import ru.korundm.entity.ProductionLot;

public interface ProductionLotService extends CommonService<ProductionLot> {

    int getUsageBalanceById(Long productionLotId);

    void addProduct(ProductionLot productionLot, int count);
}