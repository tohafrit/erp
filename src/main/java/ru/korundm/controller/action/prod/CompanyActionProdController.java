package ru.korundm.controller.action.prod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.CompanyService;
import ru.korundm.entity.Company;
import ru.korundm.entity.CompanyType;
import ru.korundm.form.CompanyListFilterForm;
import ru.korundm.form.edit.EditCompanyForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrOut;
import ru.korundm.helper.ValidatorResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.COMPANY)
@SessionAttributes(
    names = "companyListFilterForm",
    types = CompanyListFilterForm.class
)
public class CompanyActionProdController {

    private static final String COMPANY_LIST_FILTER_FORM_ATTR = "companyListFilterForm";

    private final ObjectMapper jsonMapper;
    private final CompanyService companyService;

    public CompanyActionProdController(
        ObjectMapper jsonMapper,
        CompanyService companyService
    ) {
        this.jsonMapper = jsonMapper;
        this.companyService = companyService;
    }

    @GetMapping("/list/load")
    public TabrOut<?> list_load(
        HttpServletRequest request,
        ModelMap model,
        String filterForm,
        Long typeId,
        @RequestParam(required = false) Boolean initLoad
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String name; // наименование
            String fullName; // полное наименование
            String chiefName; // ФИО руководителя
            String chiefPosition; // должность руководителя
            String phoneNumber; // телефон, факс
            String contactPerson; // контактные лица
            String location; // местонахождение
            String inn; // инн
            String kpp; // кпп
            String ogrn; // огрн
            String inspectorName; // название ПЗ
            String inspectorHead; // руководитель ПЗ
            String factualAddress; // фактический адрес
            String juridicalAddress; // юридический адрес
            String mailAddress; // почтовый адрес
            String note; // комментарий
        }
        TabrIn input = new TabrIn(request);
        if (BooleanUtils.toBoolean(initLoad)) {
            TabrOut<TableItemOut> output = new TabrOut<>();
            output.setCurrentPage(input.getPage());
            CompanyListFilterForm form = new CompanyListFilterForm();
            form.setTypeId(typeId);
            output.setLastPage(input.getSize(), companyService.getCountByForm(form));
            return output;
        }
        CompanyListFilterForm form = jsonMapper.readValue(filterForm, CompanyListFilterForm.class);
        model.addAttribute(COMPANY_LIST_FILTER_FORM_ATTR, form);
        form.setTypeId(typeId);
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), companyService.getCountByForm(form));
        List<TableItemOut> itemOutList = companyService.getByTableDataIn(input, form).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            itemOut.fullName = item.getFullName();
            itemOut.chiefName = item.getChiefName();
            itemOut.chiefPosition = item.getChiefPosition();
            itemOut.phoneNumber = item.getPhoneNumber();
            itemOut.contactPerson = item.getContactPerson();
            itemOut.location = item.getLocation();
            itemOut.inn = item.getInn();
            itemOut.kpp = item.getKpp();
            itemOut.ogrn = item.getOgrn();
            itemOut.inspectorName = item.getInspectorName();
            itemOut.inspectorHead = item.getInspectorHead();
            itemOut.factualAddress = item.getFactualAddress();
            itemOut.juridicalAddress = item.getJuridicalAddress();
            itemOut.mailAddress = item.getMailAddress();
            itemOut.note = item.getNote();
            return itemOut;
        }).collect(Collectors.toList());
        output.setData(itemOutList);
        return output;
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditCompanyForm form) {
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            Long formCompanyId = form.getId();
            Company company = formCompanyId == null ? new Company() : companyService.read(formCompanyId);
            company.setName(form.getName().strip());
            company.setFullName(form.getFullName());
            company.setChiefName(form.getChiefName());
            company.setChiefPosition(form.getChiefPosition());
            company.setPhoneNumber(form.getPhoneNumber());
            company.setContactPerson(form.getContactPerson());
            company.setInn(form.getInn());
            company.setKpp(form.getKpp());
            company.setOgrn(form.getOgrn());
            company.setInspectorName(form.getInspectorName());
            company.setInspectorHead(form.getInspectorHead());
            company.setLocation(form.getLocation());
            company.setNote(form.getNote());
            company.setFactualAddress(StringUtils.defaultIfBlank(form.getFactualAddress(), null));
            company.setJuridicalAddress(StringUtils.defaultIfBlank(form.getJuridicalAddress(), null));
            company.setMailAddress(StringUtils.defaultIfBlank(form.getMailAddress(), null));
            // Сохраняем типы компаний
            List<CompanyType> companyTypeList = company.getCompanyTypeList();
            List<CompanyType> formCompanyTypeList = form.getCompanyTypeList().stream().map(en -> {
                CompanyType companyType = new CompanyType();
                companyType.setCompany(company);
                companyType.setType(en);
                companyTypeList.add(companyType);
                return companyType;
            }).collect(Collectors.toList());
            companyTypeList.forEach(companyType ->
                formCompanyTypeList.forEach(formCompanyType -> {
                    if (Objects.equals(companyType.getType(), formCompanyType.getType())) {
                        formCompanyType.setId(companyType.getId());
                    }
                })
            );
            companyTypeList.clear();
            companyTypeList.addAll(formCompanyTypeList);
            companyService.save(company);
            if (formCompanyId == null) {
                response.putAttribute("addedCompanyId", company.getId());
            }
        }
        return response;
    }

    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        companyService.deleteById(id);
    }
}