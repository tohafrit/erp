package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.PrinterService;
import ru.korundm.dao.UserService;
import ru.korundm.entity.Printer;
import ru.korundm.form.edit.EditPrinterForm;
import ru.korundm.helper.ValidatorResponse;

import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.PRINTER)
public class PrinterActionProdController {

    private final PrinterService printerService;
    private final UserService userService;

    public PrinterActionProdController(
        PrinterService printerService,
        UserService userService
    ) {
        this.printerService = printerService;
        this.userService = userService;
    }

    // Загрузка списка
    @GetMapping("/list/load")
    public List<?> list_load() {
        @Getter
        class ResponseOut {
            long id; // идентификатор
            String name; // наименование
            String ip; // ip-адрес
            Integer port; // порт
            String description; // описание
            int users; // количество пользователей
        }
        return printerService.getAll().stream()
            .map(item -> {
                ResponseOut itemOut = new ResponseOut();
                itemOut.id = item.getId();
                itemOut.name = item.getName();
                itemOut.ip = item.getIp();
                itemOut.port = item.getPort();
                itemOut.description = item.getDescription();
                itemOut.users = item.getUserList().size();
                return itemOut;
            }).collect(Collectors.toList());
    }

    // Загрузка списка пользователей
    @GetMapping("/list/users/load")
    public List<?> list_users_load(long id) {
        @Getter
        class ResponseOut {
            long id; // идентификатор
            String firstName; // имя
            String middleName; // отчество
            String lastName; // фамилия
        }
        return printerService.read(id).getUserList().stream()
            .map(item -> {
                ResponseOut itemOut = new ResponseOut();
                itemOut.id = item.getId();
                itemOut.firstName = item.getFirstName();
                itemOut.middleName = item.getMiddleName();
                itemOut.lastName = item.getLastName();
                return itemOut;
            }).collect(Collectors.toList());
    }

    // Сохранение элемента в списке
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditPrinterForm form) {
        Long formId = form.getId();
        Printer printer = formId != null ? printerService.read(formId) : new Printer();
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            printer.setName(form.getName());
            printer.setIp(form.getIp());
            printer.setPort(form.getPort());
            printer.setDescription(form.getDescription());
            printer.setUserList(userService.getAllById(form.getUserIdList()));
            printerService.save(printer);
            if (formId == null) {
                response.putAttribute("addedPrinterId", printer.getId());
            }
        }
        return response;
    }

    // Удаление элемента из списка
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        printerService.deleteById(id);
    }
}