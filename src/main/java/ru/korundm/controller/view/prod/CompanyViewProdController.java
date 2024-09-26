package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.CompanyService;
import ru.korundm.entity.Company;
import ru.korundm.entity.CompanyType;
import ru.korundm.enumeration.CompanyTypeEnum;
import ru.korundm.form.edit.EditCompanyForm;
import ru.korundm.form.CompanyListFilterForm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ViewController(RequestPath.View.Prod.COMPANY)
@SessionAttributes(
    names = "companyListFilterForm",
    types = CompanyListFilterForm.class
)
public class CompanyViewProdController {

    private static final String COMPANY_LIST_FILTER_FORM_ATTR = "companyListFilterForm";

    private final CompanyService companyService;

    /** Список типов, которые нельзя использовать в привязке  */
    private static final List<CompanyTypeEnum> EXCLUDE_EDIT_LIST = List.of(
        CompanyTypeEnum.KORUND_M,
        CompanyTypeEnum.NIISI,
        CompanyTypeEnum.SAPSAN,
        CompanyTypeEnum.OAO_KORUND_M
    );

    public CompanyViewProdController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @ModelAttribute(COMPANY_LIST_FILTER_FORM_ATTR)
    public CompanyListFilterForm companyListFilterFormAttr() {
        return new CompanyListFilterForm();
    }

    @GetMapping("/list")
    public String list(
        ModelMap model,
        Long typeId,
        @RequestParam(required = false) Long selectedCompanyId
    ) {
        Integer lastPage = null;
        if (selectedCompanyId != null) {
            // На данный момент переход осуществляется на последнюю страницу
            // но при необходимости можно сменить на конкретную страницу для переданного изделия
            CompanyListFilterForm form = new CompanyListFilterForm();
            form.setTypeId(typeId);
            long totalSize = companyService.getCountByForm(form);
            lastPage = (int) totalSize / 50;
            lastPage = totalSize % 50 == .0 ? lastPage : lastPage + 1;
        }
        model.addAttribute("initialPage", lastPage);
        model.addAttribute("selectedCompanyId", selectedCompanyId);
        model.addAttribute("companyType", CompanyTypeEnum.Companion.getById(typeId));
        return "prod/include/company/list";
    }

    @GetMapping("/list/filter")
    public String list_filter() {
        return "prod/include/company/list/filter";
    }

    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id, Long typeId) {
        EditCompanyForm form = new EditCompanyForm();
        if (id != null) {
            Company company = companyService.read(id);
            form.setId(company.getId());
            form.setName(company.getName());
            form.setShortName(company.getShortName());
            form.setFullName(company.getFullName());
            form.setChiefName(company.getChiefName());
            form.setChiefPosition(company.getChiefPosition());
            form.setPhoneNumber(company.getPhoneNumber());
            form.setContactPerson(company.getContactPerson());
            form.setInn(company.getInn());
            form.setKpp(company.getKpp());
            form.setOgrn(company.getOgrn());
            form.setInspectorName(company.getInspectorName());
            form.setInspectorHead(company.getInspectorHead());
            form.setLocation(company.getLocation());
            form.setNote(company.getNote());
            form.setFactualAddress(company.getFactualAddress());
            form.setJuridicalAddress(company.getJuridicalAddress());
            form.setMailAddress(company.getMailAddress());
            form.setCompanyTypeList(company.getCompanyTypeList()
                .stream()
                .map(CompanyType::getType)
                .collect(Collectors.toList()));
        }
        List<CompanyTypeEnum> companyTypeEnumList = new ArrayList<>();
        CompanyTypeEnum editCompanyTypeEnum = CompanyTypeEnum.Companion.getById(typeId);
        if (EXCLUDE_EDIT_LIST.contains(editCompanyTypeEnum)) {
            companyTypeEnumList.add(editCompanyTypeEnum);
        } else {
            companyTypeEnumList = CompanyTypeEnum.Companion.getAllowed();
        }
        model.addAttribute("companyTypeList", companyTypeEnumList);
        model.addAttribute("form", form);
        return "prod/include/company/list/edit";
    }
}