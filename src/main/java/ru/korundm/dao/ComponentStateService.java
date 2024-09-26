package ru.korundm.dao;

import ru.korundm.entity.ComponentState;

public interface ComponentStateService extends CommonService<ComponentState>{

    ComponentState getByCode(String type);
}