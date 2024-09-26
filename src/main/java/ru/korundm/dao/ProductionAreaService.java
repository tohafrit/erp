package ru.korundm.dao;

import ru.korundm.entity.ProductionArea;

import java.util.List;

public interface ProductionAreaService extends CommonService<ProductionArea> {

    List<ProductionArea> getAllByTechnological(boolean technological);
    boolean existsByCodeAndIdNot(String code, Long id);
    List<ProductionArea> findTableData(List<Long> productionAreaIdList);
}