package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ProductionAreaService;
import ru.korundm.entity.ProductionArea;
import ru.korundm.form.edit.EditProductionAreaForm;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.PRODUCTION_AREA)
public class ProductionAreaViewProdController {

    private final ProductionAreaService productionAreaService;

    public ProductionAreaViewProdController(
        ProductionAreaService productionAreaService
    ) {
        this.productionAreaService = productionAreaService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/production-area/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditProductionAreaForm form = new EditProductionAreaForm();
        if (id != null) {
            ProductionArea productionArea = productionAreaService.read(id);
            form.setId(id);
            form.setName(productionArea.getName());
            form.setCode(productionArea.getCode());
            form.setTechnological(productionArea.isTechnological());
        }
        model.addAttribute("form", form);
        return "prod/include/production-area/list/edit";
    }

    @GetMapping("/list/info")
    public String list_info(
        ModelMap model,
        @RequestParam Long id,
        @RequestParam String name
    ) {
        model.addAttribute("productionArea", productionAreaService.read(id));
        return String.format("prod/include/production-area/list/info/%s", name);
    }
}