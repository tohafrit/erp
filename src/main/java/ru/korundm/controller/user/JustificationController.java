package ru.korundm.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.korundm.dao.CompanyService;
import ru.korundm.dao.FileStorageService;
import ru.korundm.dao.JustificationService;
import ru.korundm.entity.*;
import ru.korundm.enumeration.CompanyTypeEnum;
import ru.korundm.enumeration.JustificationType;
import ru.korundm.form.edit.EditJustificationForm;

@Controller
public class JustificationController {

    private final JustificationService justificationService;
    private final FileStorageService fileStorageService;
    private final CompanyService companyService;

    public JustificationController(
        JustificationService justificationService,
        FileStorageService fileStorageService,
        CompanyService companyService
    ) {
        this.justificationService = justificationService;
        this.fileStorageService = fileStorageService;
        this.companyService = companyService;
    }

    @GetMapping("/pageHead")
    public String getPageHead(
        ModelMap model,
        @RequestParam("type") String type
    ) {
        model.addAttribute("justificationType", JustificationType.getByType(type));
        return "prod/include/justification/pageHead";
    }

    @GetMapping("/justification")
    public String getJustificationPage(
        ModelMap model,
        @RequestParam("type") String type
    ) {
        model.addAttribute("justificationList",
            justificationService.getAllByEntityType(getJustificationByType(type).getClass()));
        model.addAttribute("type", type);
        model.addAttribute("isCompany", JustificationType.SPECIAL_CHECK.getType().equals(type) || JustificationType.SPECIAL_RESEARCH.getType().equals(type));
        return "prod/include/justification/justification";
    }

    @GetMapping("/editJustification")
    public String editJustification(
        ModelMap model,
        @RequestParam("docType") String docType,
        @RequestParam(value = "entityId", required = false) Long entityId,
        @RequestParam("type") String type
    ) {
        EditJustificationForm form = new EditJustificationForm();
        form.setDocType(docType);
        form.setTypeId(type);
        if ("edit".equals(docType)) {
            Justification justification = justificationService.read(entityId);
            form.setId(entityId);
            form.setName(justification.getName());
            form.setDate(justification.getDate());
            form.setCompany(justification.getCompany());
            form.setNote(justification.getNote());
            //form.setFileStorage(justification.getFile());
        }
        model.addAttribute("isCompany", JustificationType.SPECIAL_CHECK.getType().equals(type) || JustificationType.SPECIAL_RESEARCH.getType().equals(type));
        model.addAttribute("form", form);
        return "prod/include/justification/editJustification";
    }

    /*@PostMapping(
        value = "/editJustification",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public ValidatorJSONResponse saveJustification(
        EditJustificationForm form,
        BindingResult result,
        HttpServletRequest request
    ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ValidatorJSONResponse response = new ValidatorJSONResponse();
        Justification justification = "edit".equals(form.getDocType()) ? justificationService.read(form.getId()) :
            getJustificationByType(form.getTypeId());
        justification.setDate(form.getDate());
        justification.setName(form.getName());
        justification.setNote(form.getNote());
        justification.setCompany(assertFormId(form.getCompany()));
        if (
            (JustificationType.SPECIAL_CHECK.getType().equals(form.getTypeId()) || JustificationType.SPECIAL_RESEARCH.getType().equals(form.getTypeId())) &&
                formIdNotValid(form.getCompany())
        ) {
            response.putErrorMessage(request, "company", ValidatorMsg.REQUIRED, new Object[]{});
        }

        response.fullValidateFill(form, justification, result, request);
        if (response.isStatus()) {
            justification.setFile(new FileStorage(justification, form.getFile()));
            justificationService.save(justification);
            response.setRedirectHref(JustificationType.getByType(form.getTypeId()).getUrl());
        }
        return response;
    }*/

    /*@PostMapping("/deleteJustification")
    @ResponseBody
    public ValidatorJSONResponse deleteJustification(
        @RequestParam("entityId") Long entityId,
        @RequestParam("type") String type
    ) {
        justificationService.deleteById(entityId);
        ValidatorJSONResponse response = new ValidatorJSONResponse();
        response.setRedirectHref(JustificationType.getByType(type).getUrl());
        return response;
    }*/

    @GetMapping("/justification/searchCompany")
    public String searchCompany(ModelMap model) {
        model.addAttribute("companyList", companyService.getAllByType(CompanyTypeEnum.CUSTOMERS));
        return "prod/include/justification/searchCompany";
    }

    /**
     * Получить объект обоснования по его типу
     * @param type тип обоснования
     * @return объект
     */
    private Justification getJustificationByType(String type) {
        Justification justification;
        switch (type) {
            case JustificationType.Types.INDEX:
                justification = new JustificationIndex();
                break;
            case JustificationType.Types.SPECIAL_CHECK:
                justification = new JustificationSpecialCheck();
                break;
            case JustificationType.Types.WORK:
                justification = new JustificationWork();
                break;
            case JustificationType.Types.TECHNICAL_PROCESS:
                justification = new JustificationTechnicalProcess();
                break;
            default:
                justification = new JustificationSpecialResearch();
                break;
        }
        return justification;
    }
}