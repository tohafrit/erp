package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ProductTypeService;
import ru.korundm.entity.ProductType;
import ru.korundm.form.edit.EditProductTypeForm;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.PRODUCT_TYPE)
public class ProductTypeViewProdController {

    private final ProductTypeService productTypeService;

    public ProductTypeViewProdController(
        ProductTypeService productTypeService
    ) {
        this.productTypeService = productTypeService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/product-type/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditProductTypeForm form = new EditProductTypeForm();
        if (id != null) {
            ProductType productType = productTypeService.read(id);
            form.setId(id);
            form.setName(productType.getName());
            form.setDescription(productType.getDescription());
        }
        model.addAttribute("form", form);
        return "prod/include/product-type/list/edit";
    }
}