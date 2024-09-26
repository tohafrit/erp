package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.ObjAttr;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.ValueAddedTaxService;
import ru.korundm.entity.ValueAddedTax;
import ru.korundm.form.edit.EditValueAddedTaxForm;
import ru.korundm.helper.ValidatorResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.VALUE_ADDED_TAX)
public class ValueAddedTaxActionProdController {

    private final ValueAddedTaxService valueAddedTaxService;

    public ValueAddedTaxActionProdController(
        ValueAddedTaxService valueAddedTaxService
    ) {
        this.valueAddedTaxService = valueAddedTaxService;
    }

    @GetMapping("/load")
    public List<?> load() {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String periodName; // наименование периода действия ставки
            LocalDate dateFrom; // дата начала периода действия ставки
            LocalDate dateTo; // дата окончания периода действия ставки
            Double amount; // величина ставки в %
            String fileHref; // адрес файла
            boolean isLast; // последний элемент
        }
        return valueAddedTaxService.getAll().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.periodName = item.getName();
            itemOut.dateFrom = item.getDateFrom();
            itemOut.dateTo = item.getDateTo();
            itemOut.amount = item.getValue();
            //itemOut.fileHref = item.getFile() == null ? null : "/download-file/" + item.getFile().getUrlHash();
            List<ValueAddedTax> lastValueAddedTaxesList = valueAddedTaxService.findLastTwoElementsByDate();
            if (!lastValueAddedTaxesList.isEmpty()) {
                itemOut.isLast = item.equals(lastValueAddedTaxesList.get(0));
            }
            return itemOut;
        }).collect(Collectors.toList());
    }

    @PostMapping("/edit/save")
    public ValidatorResponse edit_save(EditValueAddedTaxForm form) {
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            ValueAddedTax valueAddedTax;
            ValueAddedTax previous = null;
            List<ValueAddedTax> lastValueAddedTaxesList = valueAddedTaxService.findLastTwoElementsByDate();
            if (form.getId() != null) {
                valueAddedTax = valueAddedTaxService.read(form.getId());
                if (lastValueAddedTaxesList.size() > 1) {
                    previous = lastValueAddedTaxesList.get(1);
                }
            } else {
                valueAddedTax = new ValueAddedTax();
                if (!lastValueAddedTaxesList.isEmpty()) {
                    previous = lastValueAddedTaxesList.get(0);
                }
            }
            LocalDate dateFrom = form.getDateFrom();
            valueAddedTax.setName(form.getPeriodName());
            valueAddedTax.setDateFrom(dateFrom);
            valueAddedTax.setDateTo(null);
            valueAddedTax.setValue(form.getAmount());
            if (dateFrom != null) {
                if (previous != null) {
                    if (previous.getDateFrom().isAfter(form.getDateFrom()) || previous.getDateFrom().isEqual(form.getDateFrom())) {
                        response.putError(ObjAttr.DATE_FROM, "common.hibernate.dateAfter");
                        return response;
                    } else {
                        previous.setDateTo(form.getDateFrom().minusDays(1));
                        valueAddedTaxService.save(previous);
                    }
                }
            }
            //if (form.getFileStorage() != null) {
                //valueAddedTax.setFile(new FileStorage(valueAddedTax, form.getFile()));
            //}
            valueAddedTaxService.save(valueAddedTax);
        }
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        valueAddedTaxService.deleteById(id);
        List<ValueAddedTax> lastValueAddedTaxesList = valueAddedTaxService.findLastTwoElementsByDate();
        if (!lastValueAddedTaxesList.isEmpty()) {
            ValueAddedTax lastValueAddedTax = lastValueAddedTaxesList.get(0);
            lastValueAddedTax.setDateTo(null);
            valueAddedTaxService.save(lastValueAddedTax);
        }
    }
}