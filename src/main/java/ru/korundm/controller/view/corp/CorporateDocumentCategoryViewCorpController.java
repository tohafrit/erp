package ru.korundm.controller.view.corp;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.CorporateDocumentCategoryService;
import ru.korundm.entity.CorporateDocumentCategory;
import ru.korundm.form.edit.EditCorporateDocumentCategoryForm;

@ViewController(RequestPath.View.Corp.CORPORATE_DOCUMENT_CATEGORY)
public class CorporateDocumentCategoryViewCorpController {

    private final CorporateDocumentCategoryService corporateDocumentCategoryService;

    public CorporateDocumentCategoryViewCorpController(CorporateDocumentCategoryService corporateDocumentCategoryService) {
        this.corporateDocumentCategoryService = corporateDocumentCategoryService;
    }

    @GetMapping("/list")
    public String list() {
        return "corp/include/corporate-document-category/list";
    }

    // Редактирование элемента в списке категорий документов
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditCorporateDocumentCategoryForm form = new EditCorporateDocumentCategoryForm();
        if (id != null) {
            CorporateDocumentCategory corporateDocumentCategory = corporateDocumentCategoryService.read(id);
            form.setId(id);
            form.setName(corporateDocumentCategory.getName());
            form.setParent(corporateDocumentCategory.getParent());
            form.setDescription(corporateDocumentCategory.getDescription());
            form.setSort(corporateDocumentCategory.getSort());
        }
        model.addAttribute("form", form);
        model.addAttribute("corporateDocumentCategoryList", corporateDocumentCategoryService.getAllByParentIsNull());
        return "corp/include/corporate-document-category/list/edit";
    }
}