package ru.korundm.dao;

import ru.korundm.entity.ColumnDisplaySetting;
import ru.korundm.entity.User;

import java.util.List;

public interface ColumnDisplaySettingService extends CommonService<ColumnDisplaySetting> {

    List<ColumnDisplaySetting> getColumnSettingList(String tableId, User user);

    ColumnDisplaySetting getSettingByName(String tableId, String name, User user);

    void resetSettings(User user, String tableId);
}