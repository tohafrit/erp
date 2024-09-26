package ru.korundm.dao;

import ru.korundm.entity.MessageTemplate;
import ru.korundm.form.search.MessageTemplateListFilterForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;

public interface MessageTemplateService extends CommonService<MessageTemplate> {

    MessageTemplate getByCode(String code);

    TabrResultQuery<MessageTemplate> queryDataByFilterForm(TabrIn tableDataIn, MessageTemplateListFilterForm form);
}