package ru.korundm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.korundm.entity.MenuItem;
import ru.korundm.enumeration.MenuItemType;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> getAllByParentIsNullAndType(MenuItemType type);
}