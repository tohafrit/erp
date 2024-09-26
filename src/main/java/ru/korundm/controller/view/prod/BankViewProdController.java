package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.BankService;
import ru.korundm.entity.Bank;

@ViewController(RequestPath.View.Prod.BANK)
public class BankViewProdController {

    private final BankService bankService;

    public BankViewProdController(BankService bankService) {
        this.bankService = bankService;
    }

    // Страница списка
    @GetMapping("/list")
    public String list() {
        return "prod/include/bank/list";
    }

    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        Bank bank = id != null ? bankService.read(id) : new Bank();
        model.addAttribute("bank", bank);
        return "prod/include/bank/list/edit";
    }
}