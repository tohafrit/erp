package ru.korundm.controller.action.prod;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.dao.ComponentPurposeService;
import ru.korundm.entity.ComponentPurpose;
import ru.korundm.form.edit.EditComponentPurposeForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;

import java.util.List;

@ActionController(RequestPath.Action.Prod.COMPONENT_PURPOSE)
public class ComponentPurposeActionProdController {

    private final ComponentPurposeService componentPurposeService;

    public ComponentPurposeActionProdController(
        ComponentPurposeService componentPurposeService
    ) {
        this.componentPurposeService = componentPurposeService;
    }

    // Загрузка списка назначений
    @GetMapping("/list/load")
    public List<ComponentPurpose> list_load() {
        return componentPurposeService.getAll();
    }

    // Сохранение элемента в списке назначений
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditComponentPurposeForm form) {
        ComponentPurpose componentPurpose = form.getId() != null ? componentPurposeService.read(form.getId()) : new ComponentPurpose();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            componentPurpose.setName(form.getName().trim());
            componentPurpose.setDescription(form.getDescription());
            componentPurposeService.save(componentPurpose);
        }
        return response;
    }

    // Удаление элемента из списка назначений
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        componentPurposeService.deleteById(id);
    }
}