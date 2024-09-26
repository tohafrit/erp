package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ComponentKindService;
import ru.korundm.entity.ComponentKind;
import ru.korundm.form.edit.EditComponentKindForm;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.COMPONENT_KIND)
public class ComponentKindViewProdController {

    private final ComponentKindService componentKindService;

    public ComponentKindViewProdController(ComponentKindService componentKindService) {
        this.componentKindService = componentKindService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/component-kind/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditComponentKindForm form = new EditComponentKindForm();
        if (id != null) {
            ComponentKind componentKind = componentKindService.read(id);
            form.setId(id);
            form.setName(componentKind.getName());
        }
        model.addAttribute("form", form);
        return "prod/include/component-kind/list/edit";
    }
}