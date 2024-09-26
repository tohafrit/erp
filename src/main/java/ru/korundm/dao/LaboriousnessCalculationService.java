package ru.korundm.dao;

import ru.korundm.entity.LaboriousnessCalculation;

import java.util.List;

public interface LaboriousnessCalculationService extends CommonService<LaboriousnessCalculation> {

    LaboriousnessCalculation getByProductTechnicalProcessId(long id);

    List<LaboriousnessCalculation> getAllByIdIsNotIn(List<Long> idList);
}