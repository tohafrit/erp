package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ServiceSymbolService;
import ru.korundm.entity.ServiceSymbol;
import ru.korundm.form.edit.EditServiceSymbolForm;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.SERVICE_SYMBOL)
public class ServiceSymbolViewProdController {

    private final ServiceSymbolService serviceSymbolService;

    public ServiceSymbolViewProdController(ServiceSymbolService serviceSymbolService) {
        this.serviceSymbolService = serviceSymbolService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/service-symbol/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditServiceSymbolForm form = new EditServiceSymbolForm();
        if (id != null) {
            ServiceSymbol serviceSymbol = serviceSymbolService.read(id);
            form.setId(id);
            form.setName(serviceSymbol.getName());
            form.setCode(serviceSymbol.getCode());
            form.setOperationCard(serviceSymbol.isOperationCard());
            form.setRouteMap(serviceSymbol.isRouteMap());
            form.setTechnologicalProcess(serviceSymbol.isTechnologicalProcess());
            form.setDescription(serviceSymbol.getDescription());
        }
        model.addAttribute("form", form);
        return "prod/include/service-symbol/list/edit";
    }
}