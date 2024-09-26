package ru.korundm.dao;

import ru.korundm.entity.OperationMaterial;

import java.util.List;

public interface OperationMaterialService extends CommonService<OperationMaterial> {

    List<OperationMaterial> findTableData(List<Long> materialIdList, Long workTypeId);
}