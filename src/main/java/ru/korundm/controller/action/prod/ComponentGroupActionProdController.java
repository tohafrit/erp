package ru.korundm.controller.action.prod;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.dao.ComponentGroupService;
import ru.korundm.entity.ComponentGroup;
import ru.korundm.form.edit.EditComponentGroupForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;

import java.util.List;

@ActionController(RequestPath.Action.Prod.COMPONENT_GROUP)
public class ComponentGroupActionProdController {

    private final ComponentGroupService componentGroupService;

    public ComponentGroupActionProdController(ComponentGroupService componentGroupService) {
        this.componentGroupService = componentGroupService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<ComponentGroup> list_load() {
        return componentGroupService.getAll();
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditComponentGroupForm form) {
        ComponentGroup componentGroup = form.getId() != null ? componentGroupService.read(form.getId()) : new ComponentGroup();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            componentGroup.setName(form.getName().trim());
            componentGroup.setNumber(form.getNumber());
            componentGroupService.save(componentGroup);
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        componentGroupService.deleteById(id);
    }
}
