package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ComponentInstallationTypeService;
import ru.korundm.entity.ComponentInstallationType;
import ru.korundm.form.edit.EditComponentInstallationTypeForm;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.COMPONENT_INSTALLATION_TYPE)
public class ComponentInstallationTypeViewProdController {

    private final ComponentInstallationTypeService componentInstallationTypeService;

    public ComponentInstallationTypeViewProdController(ComponentInstallationTypeService componentInstallationTypeService) {
        this.componentInstallationTypeService = componentInstallationTypeService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/component-installation-type/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditComponentInstallationTypeForm form = new EditComponentInstallationTypeForm();
        if (id != null) {
            ComponentInstallationType componentInstallationType = componentInstallationTypeService.read(id);
            form.setId(id);
            form.setName(componentInstallationType.getName());
        }
        model.addAttribute("form", form);
        return "prod/include/component-installation-type/list/edit";
    }
}