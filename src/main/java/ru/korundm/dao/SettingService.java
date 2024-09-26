package ru.korundm.dao;

import ru.korundm.entity.Setting;

public interface SettingService extends CommonService<Setting> {

    Setting getSettingByCode(String code);
}