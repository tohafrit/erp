package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ComponentMarkTypeService;
import ru.korundm.entity.ComponentMarkType;
import ru.korundm.form.edit.EditComponentMarkTypeForm;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.COMPONENT_MARK_TYPE)
public class ComponentMarkTypeViewProdController {

    private final ComponentMarkTypeService componentMarkTypeService;

    public ComponentMarkTypeViewProdController(ComponentMarkTypeService componentMarkTypeService) {
        this.componentMarkTypeService = componentMarkTypeService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/component-mark-type/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditComponentMarkTypeForm form = new EditComponentMarkTypeForm();
        if (id != null) {
            ComponentMarkType componentMarkType = componentMarkTypeService.read(id);
            form.setId(id);
            form.setMark(componentMarkType.getMark());
        }
        model.addAttribute("form", form);
        return "prod/include/component-mark-type/list/edit";
    }
}