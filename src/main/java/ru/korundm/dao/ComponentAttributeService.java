package ru.korundm.dao;

import ru.korundm.entity.ComponentAttribute;

import java.util.List;

public interface ComponentAttributeService extends CommonService<ComponentAttribute> {

    List<ComponentAttribute> getAllByCategoryId(Long categoryId);

    boolean existsByIdNotAndCategoryIdAndName(Long id, Long categoryId, String name);

    boolean existsByCategoryIdAndName(Long categoryId, String name);
}