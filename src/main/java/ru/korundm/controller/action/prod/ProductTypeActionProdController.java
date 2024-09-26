package ru.korundm.controller.action.prod;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.dao.ProductTypeService;
import ru.korundm.entity.ProductType;
import ru.korundm.form.edit.EditProductTypeForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;

import java.util.List;

@ActionController(RequestPath.Action.Prod.PRODUCT_TYPE)
public class ProductTypeActionProdController {

    private final ProductTypeService productTypeService;

    public ProductTypeActionProdController(
        ProductTypeService productTypeService
    ) {
        this.productTypeService = productTypeService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<ProductType> list_load() {
        return productTypeService.getAll();
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditProductTypeForm form) {
        ProductType productType = form.getId() != null ? productTypeService.read(form.getId()) : new ProductType();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            productType.setName(form.getName().trim());
            productType.setDescription(form.getDescription());
            productTypeService.save(productType);
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        productTypeService.deleteById(id);
    }
}