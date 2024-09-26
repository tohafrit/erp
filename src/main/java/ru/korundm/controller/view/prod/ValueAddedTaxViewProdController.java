package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.ValueAddedTaxService;
import ru.korundm.entity.ValueAddedTax;
import ru.korundm.form.edit.EditValueAddedTaxForm;

@ViewController(RequestPath.View.Prod.VALUE_ADDED_TAX)
public class ValueAddedTaxViewProdController {

    private final ValueAddedTaxService valueAddedTaxService;

    public ValueAddedTaxViewProdController(
        ValueAddedTaxService valueAddedTaxService
    ) {
        this.valueAddedTaxService = valueAddedTaxService;
    }

    @GetMapping("/edit")
    public String edit(ModelMap model, Long id) {
        EditValueAddedTaxForm form = new EditValueAddedTaxForm();
        if (id != null) {
            ValueAddedTax valueAddedTax = valueAddedTaxService.read(id);
            form.setId(valueAddedTax.getId());
            form.setPeriodName(valueAddedTax.getName());
            form.setDateFrom(valueAddedTax.getDateFrom());
            form.setAmount(valueAddedTax.getValue());
            //form.setFileStorage(valueAddedTax.getFile());
        }
        model.addAttribute("form", form);
        return "prod/include/value-added-tax/edit";
    }
}