package ru.korundm.dao;

import ru.korundm.entity.CorporateDocumentCategory;

import java.util.List;

public interface CorporateDocumentCategoryService extends CommonService<CorporateDocumentCategory> {

    List<CorporateDocumentCategory> getAllByParentIsNull();

    List<Long> getAllSiblingsIdByParentId(Long parentId);
}