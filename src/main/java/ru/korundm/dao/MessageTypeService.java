package ru.korundm.dao;

import ru.korundm.entity.MessageType;
import ru.korundm.form.search.MessageTypeListFilterForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrResultQuery;

public interface MessageTypeService extends CommonService<MessageType> {

    TabrResultQuery<MessageType> queryDataByFilterForm(TabrIn tableDataIn, MessageTypeListFilterForm form);

    boolean isUniqueCode(Long id, String code);
}