package ru.korundm.dao;

import ru.korundm.entity.ComponentCategory;

import java.util.List;

public interface ComponentCategoryService extends CommonService<ComponentCategory> {

    List<ComponentCategory> getAllByIdIsNotIn(List<Long> idList);

    List<ComponentCategory> getAllByParentIsNull();

    List<Long> getAllSiblingsIdByParentId(Long parentId);
}