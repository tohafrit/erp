package ru.korundm.controller.action.prod;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.dao.ComponentInstallationTypeService;
import ru.korundm.entity.ComponentInstallationType;
import ru.korundm.form.edit.EditComponentInstallationTypeForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;

import java.util.List;

@ActionController(RequestPath.Action.Prod.COMPONENT_INSTALLATION_TYPE)
public class ComponentInstallationTypeActionProdController {

    private final ComponentInstallationTypeService componentInstallationTypeService;

    public ComponentInstallationTypeActionProdController(ComponentInstallationTypeService componentInstallationTypeService) {
        this.componentInstallationTypeService = componentInstallationTypeService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<ComponentInstallationType> list_load() {
        return componentInstallationTypeService.getAll();
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditComponentInstallationTypeForm form) {
        ComponentInstallationType componentInstallationType = form.getId() != null ?
            componentInstallationTypeService.read(form.getId()) : new ComponentInstallationType();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            componentInstallationType.setName(form.getName().trim());
            componentInstallationTypeService.save(componentInstallationType);
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        componentInstallationTypeService.deleteById(id);
    }
}