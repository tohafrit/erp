package ru.korundm.controller.action.corp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.CorporateDocumentCategoryService;
import ru.korundm.dto.corporateDocumentCategory.CorporateDocumentCategoryTreeItem;
import ru.korundm.entity.CorporateDocumentCategory;
import ru.korundm.form.edit.EditCorporateDocumentCategoryForm;
import ru.korundm.helper.ValidatorResponse;

import java.util.ArrayList;
import java.util.List;

import static ru.korundm.util.FormValidatorUtil.assertFormId;

@ActionController(RequestPath.Action.Corp.CORPORATE_DOCUMENT_CATEGORY)
public class CorporateDocumentCategoryActionCorpController {

    private final CorporateDocumentCategoryService corporateDocumentCategoryService;

    public CorporateDocumentCategoryActionCorpController(CorporateDocumentCategoryService corporateDocumentCategoryService) {
        this.corporateDocumentCategoryService = corporateDocumentCategoryService;
    }

    // Загрузка списка корпоративных категорий документов
    @GetMapping("/list/load")
    public List<CorporateDocumentCategoryTreeItem> list_load() {
        return recursiveComponentCategory(corporateDocumentCategoryService.getAllByParentIsNull());
    }

    /**
     * Метод для формирования списка компонентов корпоративных категорий документов с вложенными пунктами
     * @param corporateDocumentCategoryList родительский список компонентов корпоративных категорий документов
     * @return полный список компонентов корпоративных категорий документов с вложениями
     */
    private List<CorporateDocumentCategoryTreeItem> recursiveComponentCategory(List<CorporateDocumentCategory> corporateDocumentCategoryList) {
        List<CorporateDocumentCategoryTreeItem> childrenList = new ArrayList<>();
        for (var componentCategory : corporateDocumentCategoryList) {
            CorporateDocumentCategoryTreeItem children = new CorporateDocumentCategoryTreeItem();
            children.setId(componentCategory.getId());
            children.setName(componentCategory.getName());
            children.setDescription(componentCategory.getDescription());
            children.setSort(componentCategory.getSort());
            if (!componentCategory.getChildList().isEmpty()) {
                children.setChildrenList(recursiveComponentCategory(componentCategory.getChildList()));
            }
            childrenList.add(children);
        }
        return childrenList;
    }

    // Сохранение элемента в списке корпоративных категорий документов
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(
        EditCorporateDocumentCategoryForm form
    ) {
        CorporateDocumentCategory category = form.getId() != null ? corporateDocumentCategoryService.read(form.getId()) : new CorporateDocumentCategory();
        form.setNotParentAllowedList(corporateDocumentCategoryService.getAllSiblingsIdByParentId(category.getId()));
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            category.setName(form.getName().trim());
            category.setParent(assertFormId(form.getParent()));
            category.setSort(form.getSort());
            category.setDescription(StringUtils.defaultIfBlank(form.getDescription(), null));
            corporateDocumentCategoryService.save(category);
        }
        return response;
    }

    // Удаление элемента из списка корпортивных категорий документов
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        corporateDocumentCategoryService.deleteById(id);
    }
}