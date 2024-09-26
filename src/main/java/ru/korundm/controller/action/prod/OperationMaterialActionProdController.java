package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.OperationMaterialService;
import ru.korundm.dao.WorkTypeService;
import ru.korundm.entity.OperationMaterial;
import ru.korundm.entity.WorkType;
import ru.korundm.form.edit.EditOperationMaterialForm;
import ru.korundm.helper.ValidatorResponse;

import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.OPERATION_MATERIAL)
public class OperationMaterialActionProdController {

    private final OperationMaterialService operationMaterialService;
    private final WorkTypeService workTypeService;

    public OperationMaterialActionProdController(
        OperationMaterialService operationMaterialService,
        WorkTypeService workTypeService
    ) {
        this.operationMaterialService = operationMaterialService;
        this.workTypeService = workTypeService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<?> list_load() {
        @Getter
        class ResponseOut {
            long id; // идентификатор
            String name; // наименование
            String operation; // операции
        }
        return operationMaterialService.getAll().stream()
            .map(operationMaterial -> {
                ResponseOut responseOut = new ResponseOut();
                responseOut.id = operationMaterial.getId();
                responseOut.name = operationMaterial.getName();
                responseOut.operation = operationMaterial.getWorkTypeList().stream()
                    .map(WorkType::getName).collect(Collectors.joining(", "));
                return responseOut;
            }).collect(Collectors.toList());
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditOperationMaterialForm form) {
        Long formId = form.getId();
        OperationMaterial operationMaterial = formId != null ? operationMaterialService.read(formId) : new OperationMaterial();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            operationMaterial.setName(form.getName().trim());
            operationMaterial.setWorkTypeList(workTypeService.getAllById(form.getWorkTypeList()));
            operationMaterialService.save(operationMaterial);
            if (formId == null) {
                response.putAttribute("addedMaterialId", operationMaterial.getId());
            }
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        operationMaterialService.deleteById(id);
    }
}