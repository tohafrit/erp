package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ProductionAreaService;
import ru.korundm.dao.TechnologicalToolService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.ProductionArea;
import ru.korundm.entity.TechnologicalTool;
import ru.korundm.enumeration.TechnologicalToolType;
import ru.korundm.form.edit.EditTechnologicalToolForm;
import ru.korundm.form.search.TechnologicalToolFilterForm;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.RequestPath;

import java.util.stream.Collectors;

@ViewController(RequestPath.View.Prod.TECHNOLOGICAL_TOOL)
@SessionAttributes(names = "technologicalToolListFilterForm", types = TechnologicalToolFilterForm.class)
public class TechnologicalToolViewProdController {

    private static final String TECHNOLOGICAL_TOOL_LIST_FILTER_FORM_ATTR = "technologicalToolListFilterForm";

    private final TechnologicalToolService technologicalToolService;
    private final UserService userService;
    private final ProductionAreaService productionAreaService;

    public TechnologicalToolViewProdController(
        TechnologicalToolService technologicalToolService,
        UserService userService,
        ProductionAreaService productionAreaService
    ) {
        this.technologicalToolService = technologicalToolService;
        this.userService = userService;
        this.productionAreaService = productionAreaService;
    }

    @ModelAttribute(TECHNOLOGICAL_TOOL_LIST_FILTER_FORM_ATTR)
    public TechnologicalToolFilterForm technologicalToolFilterFormAttr() {
        return new TechnologicalToolFilterForm();
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/technological-tool/list";
    }

    @GetMapping("/list/filter")
    public String list_filter(ModelMap model) {
        model.addAttribute("technologicalToolTypeList", TechnologicalToolType.values());
        model.addAttribute("productionAreaList", productionAreaService.getAllByTechnological(Boolean.TRUE));
        return "prod/include/technological-tool/list/filter";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditTechnologicalToolForm form = new EditTechnologicalToolForm();
        if (id != null) {
            TechnologicalTool technologicalTool = technologicalToolService.read(id);
            form.setId(technologicalTool.getId());
            form.setSign(technologicalTool.getSign());
            form.setName(technologicalTool.getName());
            form.setAppointment(technologicalTool.getAppointment());
            form.setLink(technologicalTool.getLink());
            form.setState(technologicalTool.getState());
            form.setIssueDate(technologicalTool.getIssueDate());
            form.setUser(technologicalTool.getUser());
            form.setType(technologicalTool.getType());
            form.setProductionAreaIdList(
                technologicalTool.getProductionAreaList().stream()
                    .map(ProductionArea::getId).collect(Collectors.toList())
            );
        }
        model.addAttribute("technologicalToolTypeList", TechnologicalToolType.values());
        model.addAttribute("productionAreaList", productionAreaService.getAllByTechnological(Boolean.TRUE));
        model.addAttribute("userList", userService.getActiveAll());
        model.addAttribute("form", form);
        return "prod/include/technological-tool/list/edit";
    }

    @GetMapping("/list/edit/file")
    public String list_edit_file(ModelMap model) {
        model.addAttribute("link", BaseConstant.OPP_SHARE_FILESERVER);
        return "prod/include/technological-tool/list/edit/file";
    }
}