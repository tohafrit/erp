package ru.korundm.controller.action.prod;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.dao.ServiceSymbolService;
import ru.korundm.entity.ServiceSymbol;
import ru.korundm.form.edit.EditServiceSymbolForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;

import java.util.List;

@ActionController(RequestPath.Action.Prod.SERVICE_SYMBOL)
public class ServiceSymbolActionProdController {

    private final ServiceSymbolService serviceSymbolService;

    public ServiceSymbolActionProdController(ServiceSymbolService serviceSymbolService) {
        this.serviceSymbolService = serviceSymbolService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<ServiceSymbol> list_load() {
        return serviceSymbolService.getAll();
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditServiceSymbolForm form) {
        ServiceSymbol serviceSymbol = form.getId() != null ? serviceSymbolService.read(form.getId()) : new ServiceSymbol();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            serviceSymbol.setName(form.getName().trim());
            serviceSymbol.setCode(form.getCode());
            serviceSymbol.setOperationCard(form.isOperationCard());
            serviceSymbol.setRouteMap(form.isRouteMap());
            serviceSymbol.setTechnologicalProcess(form.isTechnologicalProcess());
            serviceSymbol.setDescription(form.getDescription());
            serviceSymbolService.save(serviceSymbol);
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        serviceSymbolService.deleteById(id);
    }
}