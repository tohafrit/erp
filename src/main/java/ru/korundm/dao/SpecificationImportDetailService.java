package ru.korundm.dao;

import ru.korundm.entity.SpecificationImportDetail;

import java.util.List;

public interface SpecificationImportDetailService extends CommonService<SpecificationImportDetail> {

    List<SpecificationImportDetail> getAllByBomId(Long bomId);

    boolean existsByBomId(Long bomId);

    void deleteAllByBomId(Long bomId);
}