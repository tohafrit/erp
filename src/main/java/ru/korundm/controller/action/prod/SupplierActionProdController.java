package ru.korundm.controller.action.prod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.SupplierService;
import ru.korundm.entity.Supplier;
import ru.korundm.form.search.SupplierFilterForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrOut;
import ru.korundm.helper.TabrResultQuery;
import ru.korundm.util.HibernateUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.SUPPLIER)
@SessionAttributes(names = "supplierFilter", types = SupplierFilterForm.class)
public class SupplierActionProdController {

    private static final String SUPPLIER_FILTER_FORM_ATTR = "supplierFilter";

    private final ObjectMapper jsonMapper;
    private final SupplierService supplierService;

    public SupplierActionProdController(
        ObjectMapper jsonMapper,
        SupplierService supplierService
    ) {
        this.jsonMapper = jsonMapper;
        this.supplierService = supplierService;
    }

    @GetMapping("/list/load")
    public TabrOut<?> list_load(
        HttpServletRequest request,
        ModelMap model,
        String filterForm,
        @RequestParam(required = false) Boolean initLoad
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String name; // наименование
            Long inn; // ИНН
            Long kpp; // КПП
        }
        TabrIn input = new TabrIn(request);
        if (BooleanUtils.toBoolean(initLoad)) {
            TabrOut<TableItemOut> output = new TabrOut<>();
            output.setCurrentPage(input.getPage());
            output.setLastPage(input.getSize(), supplierService.getCount());
            return output;
        }
        SupplierFilterForm form = jsonMapper.readValue(filterForm, SupplierFilterForm.class);
        model.addAttribute(SUPPLIER_FILTER_FORM_ATTR, form);
        TabrResultQuery<Supplier> dataResultQuery = supplierService.queryDataByFilterForm(input, form);
        List<TableItemOut> itemOutList = dataResultQuery.getData().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            itemOut.inn = item.getInn();
            itemOut.kpp = item.getKpp();
            return itemOut;
        }).collect(Collectors.toList());
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), dataResultQuery.getCount());
        output.setData(itemOutList);
        return output;
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(Supplier supplier) {
        ValidatorResponse response = new ValidatorResponse();
        response.fill(HibernateUtil.entityValidate(supplier));
        if (response.isValid()) {
            Long formId = supplier.getId();
            supplierService.save(supplier);
            if (formId == null) {
                response.putAttribute("addedSupplierId", supplier.getId());
            }
        }
        return response;
    }

    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        supplierService.deleteById(id);
    }
}