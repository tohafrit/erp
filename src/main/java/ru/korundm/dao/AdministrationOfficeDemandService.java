package ru.korundm.dao;

import ru.korundm.entity.AdministrationOfficeDemand;
import ru.korundm.entity.User;
import ru.korundm.form.search.AdministrationOfficeDemandListFilterForm;
import ru.korundm.helper.TabrIn;

import java.util.List;

public interface AdministrationOfficeDemandService extends CommonService<AdministrationOfficeDemand> {

    List<AdministrationOfficeDemand> getAllByUser(User user);

    List<AdministrationOfficeDemand> getByTableDataIn(TabrIn tableDataIn, AdministrationOfficeDemandListFilterForm form);

    long getCountByForm(AdministrationOfficeDemandListFilterForm form);
}