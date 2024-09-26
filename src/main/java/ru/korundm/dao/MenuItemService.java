package ru.korundm.dao;

import ru.korundm.entity.MenuItem;
import ru.korundm.enumeration.MenuItemType;

import java.util.List;

public interface MenuItemService extends CommonService<MenuItem> {

    List<MenuItem> getAllAlreadyUsedMenuItem(MenuItem currentMenuItem);

    List<MenuItem> getAllByParentNullAndType(MenuItemType type);
}