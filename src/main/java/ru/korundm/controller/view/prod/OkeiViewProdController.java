package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.OkeiService;
import ru.korundm.entity.Okei;
import ru.korundm.constant.RequestPath;

@ViewController(RequestPath.View.Prod.OKEI)
public class OkeiViewProdController {

    private final OkeiService okeiService;

    public OkeiViewProdController(
        OkeiService okeiService
    ) {
        this.okeiService = okeiService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/okei/list";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        model.addAttribute("okei", id != null ? okeiService.read(id) : new Okei());
        return "prod/include/okei/list/edit";
    }
}