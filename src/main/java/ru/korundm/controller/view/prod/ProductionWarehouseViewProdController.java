package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ProductionWarehouseService;
import ru.korundm.entity.ProductionWarehouse;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.PRODUCTION_WAREHOUSE)
public class ProductionWarehouseViewProdController {

    private final ProductionWarehouseService productionWarehouseService;

    public ProductionWarehouseViewProdController(ProductionWarehouseService productionWarehouseService) {
        this.productionWarehouseService = productionWarehouseService;
    }

    @GetMapping("/edit")
    public String edit(ModelMap model, Long id) {
        ProductionWarehouse productionWarehouse = id != null ?
            productionWarehouseService.read(id) :
            new ProductionWarehouse();
        model.addAttribute("productionWarehouse", productionWarehouse);
        return "prod/include/production-warehouse/edit";
    }
}