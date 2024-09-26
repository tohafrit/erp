package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.SupplierService;
import ru.korundm.entity.Supplier;
import ru.korundm.form.search.SupplierFilterForm;

@ViewController(RequestPath.View.Prod.SUPPLIER)
@SessionAttributes(names = "supplierFilter", types = SupplierFilterForm.class)
public class SupplierViewProdController {

    private static final String SUPPLIER_FILTER_FORM_ATTR = "supplierFilter";

    private final SupplierService supplierService;

    public SupplierViewProdController(
        SupplierService supplierService
    ) {
        this.supplierService = supplierService;
    }

    @ModelAttribute(SUPPLIER_FILTER_FORM_ATTR)
    public SupplierFilterForm supplierFilterFormAttr() {
        return new SupplierFilterForm();
    }

    @GetMapping("/list")
    public String list(
        ModelMap model,
        @RequestParam(required = false) Long selectedSupplierId
    ) {
        Integer lastPage = null;
        if (selectedSupplierId != null) {
            // На данный момент переход осуществляется на последнюю страницу
            // но при необходимости можно сменить на конкретную страницу для переданного производителя
            long totalSize = supplierService.getCount();
            lastPage = (int) totalSize / 50;
            lastPage = totalSize % 50 == .0 ? lastPage : lastPage + 1;
        }
        model.addAttribute("initialPage", lastPage);
        model.addAttribute("selectedSupplierId", selectedSupplierId);
        return "prod/include/supplier/list";
    }

    @GetMapping("/list/filter")
    public String list_filter() {
        return "prod/include/supplier/list/filter";
    }

    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        Supplier supplier = id != null ? supplierService.read(id) : new Supplier();
        model.addAttribute("supplier", supplier);
        return "prod/include/supplier/list/edit";
    }
}