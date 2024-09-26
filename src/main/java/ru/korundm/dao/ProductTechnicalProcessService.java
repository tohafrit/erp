package ru.korundm.dao;

import ru.korundm.entity.Justification;
import ru.korundm.entity.ProductTechnicalProcess;

import java.util.List;

public interface ProductTechnicalProcessService extends CommonService<ProductTechnicalProcess> {

    List<ProductTechnicalProcess> getAllByJustification(Justification justification);

    void setApprovedById(Boolean approved, List<Long> ids);

    List<ProductTechnicalProcess> getAllByParams(Justification justification, Long id);
}