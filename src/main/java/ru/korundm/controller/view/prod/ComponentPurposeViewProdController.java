package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ComponentPurposeService;
import ru.korundm.entity.ComponentPurpose;
import ru.korundm.form.edit.EditComponentPurposeForm;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.COMPONENT_PURPOSE)
public class ComponentPurposeViewProdController {

    private final ComponentPurposeService componentPurposeService;

    public ComponentPurposeViewProdController(
        ComponentPurposeService componentPurposeService
    ) {
        this.componentPurposeService = componentPurposeService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/component-purpose/list";
    }

    // Редактирование элемента в списке назначений
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditComponentPurposeForm form = new EditComponentPurposeForm();
        if (id != null) {
            ComponentPurpose componentPurpose = componentPurposeService.read(id);
            form.setId(id);
            form.setName(componentPurpose.getName());
            form.setDescription(componentPurpose.getDescription());
        }
        model.addAttribute("form", form);
        return "prod/include/component-purpose/list/edit";
    }
}