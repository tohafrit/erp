package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.BankService;
import ru.korundm.entity.Bank;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.exception.AlertUIException;
import ru.korundm.util.HibernateUtil;

import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.BANK)
public class BankActionProdController {

    private final BankService bankService;

    public BankActionProdController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/list/load")
    public List<?> list_load() {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String name; // наименование
            String location; // местонахождение
            String bik; // БИК
            String correspondentAccount; // корр. счет
            String address; // адрес
            String phone; // телефон
        }
        return bankService.getAll().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            itemOut.location = item.getLocation();
            itemOut.bik = item.getBik();
            itemOut.correspondentAccount = item.getCorrespondentAccount();
            itemOut.address = item.getAddress();
            itemOut.phone = item.getPhone();
            return itemOut;
        }).collect(Collectors.toList());
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(Bank bank) {
        Long formId = bank.getId();
        ValidatorResponse response = new ValidatorResponse();
        response.fill(HibernateUtil.entityValidate(bank));
        if (response.isValid()) {
            bankService.save(bank);
            if (formId == null) {
                response.putAttribute("addedBankId", bank.getId());
            }
        }
        return response;
    }

    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable long id) {
        Bank bank = bankService.read(id);
        if (CollectionUtils.isNotEmpty(bank.getAccountList())) {
            throw new AlertUIException("Невозможно удалить банк, к которому привязаны расчетные счета");
        }
        bankService.deleteById(id);
    }
}