package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.dao.ProductionWarehouseService;
import ru.korundm.entity.ProductionWarehouse;
import ru.korundm.exception.AlertUIException;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;
import ru.korundm.util.HibernateUtil;

import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.PRODUCTION_WAREHOUSE)
public class ProductionWarehouseActionProdController {

    private final ProductionWarehouseService productionWarehouseService;

    public ProductionWarehouseActionProdController(ProductionWarehouseService productionWarehouseService) {
        this.productionWarehouseService = productionWarehouseService;
    }

    @GetMapping("/load")
    public List<?> load() {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String code; // код
            String name; // наименование
        }
        return productionWarehouseService.getAll().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.code = item.getFormatCode();
            itemOut.name = item.getName();
            return itemOut;
        }).collect(Collectors.toList());
    }

    @PostMapping("/edit/save")
    public ValidatorResponse edit_save(ProductionWarehouse productionWarehouse) {
        ValidatorResponse response = new ValidatorResponse();
        response.fill(HibernateUtil.entityValidate(productionWarehouse));
        if (response.isValid()) {
            if (productionWarehouseService.existsByCodeAndIdNot(productionWarehouse.getCode(), productionWarehouse.getId())) {
                throw new AlertUIException("Склад под указанным кодом уже существует");
            }
            productionWarehouseService.save(productionWarehouse);
        }
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        productionWarehouseService.deleteById(id);
    }
}