package ru.korundm.controller.action.prod;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.dao.ComponentKindService;
import ru.korundm.entity.ComponentKind;
import ru.korundm.form.edit.EditComponentKindForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;

import java.util.List;

@ActionController(RequestPath.Action.Prod.COMPONENT_KIND)
public class ComponentKindActionProdController {

    private final ComponentKindService componentKindService;

    public ComponentKindActionProdController(ComponentKindService componentKindService) {
        this.componentKindService = componentKindService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<ComponentKind> list_load() {
        return componentKindService.getAll();
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditComponentKindForm form) {
        ComponentKind componentKind = form.getId() != null ? componentKindService.read(form.getId()) : new ComponentKind();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            componentKind.setName(form.getName().trim());
            componentKindService.save(componentKind);
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        componentKindService.deleteById(id);
    }
}