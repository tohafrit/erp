package ru.korundm.controller.action.prod;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.dao.ComponentMarkTypeService;
import ru.korundm.entity.ComponentMarkType;
import ru.korundm.form.edit.EditComponentMarkTypeForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;

import java.util.List;

@ActionController(RequestPath.Action.Prod.COMPONENT_MARK_TYPE)
public class ComponentMarkTypeActionProdController {

    private final ComponentMarkTypeService componentMarkTypeService;

    public ComponentMarkTypeActionProdController(ComponentMarkTypeService componentMarkTypeService) {
        this.componentMarkTypeService = componentMarkTypeService;
    }
    // Загрузка списка
    @GetMapping("/list/load")
    public List<ComponentMarkType> list_load() {
        return componentMarkTypeService.getAll();
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditComponentMarkTypeForm form) {
        ComponentMarkType componentMarkType = form.getId() != null ? componentMarkTypeService.read(form.getId()) : new ComponentMarkType();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            componentMarkType.setMark(form.getMark());
            componentMarkTypeService.save(componentMarkType);
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        componentMarkTypeService.deleteById(id);
    }
}