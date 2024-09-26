package ru.korundm.dao;

import ru.korundm.entity.ProductionLotSpecProductionLotReference;

import java.util.List;

public interface ProductionLotSpecProductionLotReferenceService extends CommonService<ProductionLotSpecProductionLotReference> {

    void deleteAll(List<ProductionLotSpecProductionLotReference> objectList);
}