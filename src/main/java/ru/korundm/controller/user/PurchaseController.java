package ru.korundm.controller.user;

import eco.dao.EcoLaunchService;
import eco.dao.EcoPurchaseService;
import eco.entity.EcoLaunch;
import eco.entity.EcoPurchase;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.korundm.dao.UserService;
import ru.korundm.enumeration.BomType;
import ru.korundm.form.edit.EditPurchaseForm;
import ru.korundm.form.search.SearchPurchaseForm;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@SessionAttributes(names = "searchPurchaseForm", types = SearchPurchaseForm.class)
public class PurchaseController {

    private final EcoPurchaseService purchaseService;
    private final UserService userService;
    private final EcoLaunchService launchService;

    public PurchaseController(
        EcoPurchaseService purchaseService,
        UserService userService,
        EcoLaunchService launchService
    ) {
        this.purchaseService = purchaseService;
        this.userService = userService;
        this.launchService = launchService;
    }

    @ModelAttribute
    public void setUpModelAttribute(ModelMap model) {
        if (!model.containsAttribute("searchPurchaseForm")) {
            model.addAttribute("searchPurchaseForm", new SearchPurchaseForm());
        }
        List<String> lastNameList = purchaseService.getDistinctCreatedBy();
        model.addAttribute("userList", userService.getByLastNameIn(lastNameList));
        model.addAttribute("launchList", launchService.getAll());
    }

    @GetMapping("/purchase")
    public String purchasing(ModelMap model) {
        model.addAttribute("purchaseList", purchaseService.getAll());
        return "section/purchase";
    }

    @PostMapping("/purchase")
    public String searchPurchase(
        SearchPurchaseForm form,
        BindingResult result
    ) {
        form.validate(form, result);
        return "section/purchase";
    }

    /*@PostMapping(
        value = "/ajaxLoadPurchase",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public DataTablesOutput<EcoPurchase> ajaxLoadPurchase(
        HttpServletRequest request,
        ModelMap model
    ) {
        SearchPurchaseForm form = (SearchPurchaseForm) model.get("searchPurchaseForm");
        DataTablesOutput<EcoPurchase> dataTablesOutput = new DataTablesOutput<>();
        DataTablesInput dataTablesInput = new DataTablesInput(request);
        dataTablesOutput.setDraw(dataTablesInput.getDraw());
        List<EcoPurchase> purchaseList = purchaseService.getAllBySearchForm(form, dataTablesInput);
        dataTablesOutput.setData(purchaseList);
        long count = purchaseService.getCountBySearchForm(form);
        dataTablesOutput.setRecordsFiltered(count);
        dataTablesOutput.setRecordsTotal(count);
        return dataTablesOutput;
    }*/

    @GetMapping("/editPurchase")
    public String editPurchase(
        ModelMap model,
        @RequestParam("docType") String docType,
        @RequestParam(value = "entityId", required = false) Long entityId
    ) {
        EditPurchaseForm form = new EditPurchaseForm();
        form.setPlanDate(LocalDate.now());
        if ("edit".equals(docType)) {
            EcoPurchase purchase = purchaseService.read(entityId);
            EcoLaunch launch = purchase.getLaunch();
            form.setId(purchase.getId());
            form.setName(purchase.getName());
            form.setNote(purchase.getNote());
            form.setLaunch(launch);
            form.setPlanDate(purchase.getPlanDate());
            form.setType(purchase.getType());
            form.setLaunchList(purchase.getPurchaseLaunchList().stream().map(EcoLaunch::getId).collect(Collectors.toList()));
            List<EcoLaunch> launchList = launchService.getAllByYearAndNumberInYear(launch.getYear(), launch.getId());
            model.addAttribute("previousLaunchList",
                launchList.stream().filter(l -> !(l.getYear().equals(launch.getYear()) && l.getNumberInYear() > launch.getNumberInYear())).collect(Collectors.toList()));
        }
        model.addAttribute("typeList", BomType.values());
        model.addAttribute("form", form);
        model.addAttribute("docType", docType);
        return "edit/editPurchase";
    }

    /*@PostMapping(
        value = "/editPurchase",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public ValidatorJSONResponse savePurchase(EditPurchaseForm form) {
        ValidatorJSONResponse response = new ValidatorJSONResponse();
        EcoPurchase purchase = formIdValid(form.getId()) ? purchaseService.read(form.getId()) : new EcoPurchase();
        purchase.setLaunch(form.getLaunch());
        purchase.setName(form.getName());
        purchase.setNote(form.getNote());
        purchase.setPlanDate(form.getPlanDate());
        purchase.setType(form.getType());
        purchase.setPurchaseLaunchList(launchService.getAllById(form.getLaunchList()));
        response.fill(HibernateUtil.entityValidate(purchase));
        if (response.isStatus()) {
//            purchaseService.save(purchase);
        }
        return response;
    }*/

    @GetMapping(
        value = "/getPreviousLaunchList",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public List<EcoLaunch> getPreviousLaunchList(
        @RequestParam("launchId") Long launchId
    ) {
        List<EcoLaunch> launchList = Collections.emptyList();
        if (launchId != null) {
            EcoLaunch launch = launchService.read(launchId);
            launchList = launchService.getAllByYearAndNumberInYear(launch.getYear(), launchId).stream()
                .filter(l -> !(l.getYear().equals(launch.getYear()) && l.getNumberInYear() > launch.getNumberInYear())).collect(Collectors.toList());
        }
        return launchList;
    }

    @PostMapping("/deletePurchase")
    @ResponseBody
    public void deletePurchase(
        @RequestParam(value = "entityId", required = false) Long entityId
    ) {
//        purchaseService.deleteById(entityId);
    }
}