package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.OperationMaterialService;
import ru.korundm.dao.WorkTypeService;
import ru.korundm.entity.OperationMaterial;
import ru.korundm.entity.WorkType;
import ru.korundm.form.edit.EditOperationMaterialForm;

import java.util.stream.Collectors;

@ViewController(RequestPath.View.Prod.OPERATION_MATERIAL)
public class OperationMaterialViewProdController {

    private final OperationMaterialService operationMaterialService;
    private final WorkTypeService workTypeService;

    public OperationMaterialViewProdController(
        OperationMaterialService operationMaterialService,
        WorkTypeService workTypeService
    ) {
        this.operationMaterialService = operationMaterialService;
        this.workTypeService = workTypeService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/operation-material/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditOperationMaterialForm form = new EditOperationMaterialForm();
        if (id != null) {
            OperationMaterial operationMaterial = operationMaterialService.read(id);
            form.setId(id);
            form.setName(operationMaterial.getName());
            form.setWorkTypeList(operationMaterial.getWorkTypeList().stream().map(WorkType::getId).collect(Collectors.toList()));
        }
        model.addAttribute("form", form);
        model.addAttribute("workTypeList", workTypeService.getAll());
        return "prod/include/operation-material/list/edit";
    }
}