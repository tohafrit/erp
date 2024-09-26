package ru.korundm.dao;

import ru.korundm.entity.AdministrationOfficeStep;

public interface AdministrationOfficeStepService extends CommonService<AdministrationOfficeStep> {

    AdministrationOfficeStep getLastByDemand(Long demandId);
}
