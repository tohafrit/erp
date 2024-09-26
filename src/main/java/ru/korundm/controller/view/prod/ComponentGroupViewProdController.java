package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ComponentGroupService;
import ru.korundm.entity.ComponentGroup;
import ru.korundm.form.edit.EditComponentGroupForm;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.COMPONENT_GROUP)
public class ComponentGroupViewProdController {

    private final ComponentGroupService componentGroupService;

    public ComponentGroupViewProdController(ComponentGroupService componentGroupService) {
        this.componentGroupService = componentGroupService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/component-group/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditComponentGroupForm form = new EditComponentGroupForm();
        if (id != null) {
            ComponentGroup componentGroup = componentGroupService.read(id);
            form.setId(id);
            form.setNumber(componentGroup.getNumber());
            form.setName(componentGroup.getName());
        }
        model.addAttribute("form", form);
        return "prod/include/component-group/list/edit";
    }
}