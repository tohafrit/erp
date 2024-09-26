package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ReasonChangeService;
import ru.korundm.entity.ReasonChange;
import ru.korundm.form.edit.EditReasonChangeForm;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.REASON_CHANGE)
public class ReasonChangeViewProdController {

    private final ReasonChangeService reasonChangeService;

    public ReasonChangeViewProdController(ReasonChangeService reasonChangeService) {
        this.reasonChangeService = reasonChangeService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/reason-change/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditReasonChangeForm form = new EditReasonChangeForm();
        if (id != null) {
            ReasonChange reasonChange = reasonChangeService.read(id);
            form.setId(id);
            form.setCode(reasonChange.getCode());
            form.setReason(reasonChange.getReason());
        }
        model.addAttribute("form", form);
        return "prod/include/reason-change/list/edit";
    }
}