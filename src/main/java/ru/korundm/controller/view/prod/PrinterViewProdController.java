package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.PrinterService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.Printer;
import ru.korundm.entity.User;
import ru.korundm.form.edit.EditPrinterForm;

import java.util.stream.Collectors;

@ViewController(RequestPath.View.Prod.PRINTER)
public class PrinterViewProdController {

    private final PrinterService printerService;
    private final UserService userService;

    public PrinterViewProdController(
        PrinterService printerService,
        UserService userService
    ) {
        this.printerService = printerService;
        this.userService = userService;
    }

    // Страница списка
    @GetMapping("/list")
    public String list() {
        return "prod/include/printer/list";
    }

    // Страница пользователей
    @GetMapping("/list/users")
    public String list_users(ModelMap model, Long entityId) {
        model.addAttribute("entityId", entityId);
        return "prod/include/printer/list/users";
    }

    // Редактирование элемента в списке
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditPrinterForm form = new EditPrinterForm();
        if (id != null) {
            Printer printer = printerService.read(id);
            form.setId(id);
            form.setName(printer.getName());
            form.setIp(printer.getIp());
            form.setPort(printer.getPort());
            form.setDescription(printer.getDescription());
            form.setUserIdList(printer.getUserList().stream()
                .map(User::getId).collect(Collectors.toList()));
        }
        model.addAttribute("form", form);
        model.addAttribute("userList", userService.getAll());
        return "prod/include/printer/list/edit";
    }
}