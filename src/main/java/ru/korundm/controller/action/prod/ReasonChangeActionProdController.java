package ru.korundm.controller.action.prod;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.dao.ReasonChangeService;
import ru.korundm.entity.ReasonChange;
import ru.korundm.form.edit.EditReasonChangeForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.constant.RequestPath;

import java.util.List;

@ActionController(RequestPath.Action.Prod.REASON_CHANGE)
public class ReasonChangeActionProdController {

    private final ReasonChangeService reasonChangeService;

    public ReasonChangeActionProdController(ReasonChangeService reasonChangeService) {
        this.reasonChangeService = reasonChangeService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<ReasonChange> list_load() {
        return reasonChangeService.getAll();
    }


    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditReasonChangeForm form) {
        ReasonChange reasonChange = form.getId() != null ? reasonChangeService.read(form.getId()) : new ReasonChange();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            reasonChange.setCode(form.getCode());
            reasonChange.setReason(form.getReason());
            reasonChangeService.save(reasonChange);
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        reasonChangeService.deleteById(id);
    }
}