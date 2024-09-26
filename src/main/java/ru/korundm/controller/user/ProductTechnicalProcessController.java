package ru.korundm.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.korundm.dao.*;
import ru.korundm.entity.Justification;
import ru.korundm.entity.JustificationTechnicalProcess;
import ru.korundm.entity.Laboriousness;
import ru.korundm.entity.ProductTechnicalProcess;
import ru.korundm.enumeration.JustificationType;
import ru.korundm.form.edit.EditProductTechnicalProcessForm;
import ru.korundm.form.search.ProductListFilterForm;
import ru.korundm.util.HibernateUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductTechnicalProcessController {

    private final ObjectMapper jsonMapper;
    private final ProductTechnicalProcessService productTechnicalProcessService;
    private final JustificationService justificationService;
    private final WorkTypeService workTypeService;
    private final LaboriousnessService laboriousnessService;
    private final ProductService productService;
    private final ProductTypeService productTypeService;

    public ProductTechnicalProcessController(
        ObjectMapper jsonMapper,
        ProductTechnicalProcessService productTechnicalProcessService,
        JustificationService justificationService,
        WorkTypeService workTypeService,
        LaboriousnessService laboriousnessService,
        ProductService productService,
        ProductTypeService productTypeService
    ) {
        this.jsonMapper = jsonMapper;
        this.productTechnicalProcessService = productTechnicalProcessService;
        this.justificationService = justificationService;
        this.workTypeService = workTypeService;
        this.laboriousnessService = laboriousnessService;
        this.productService = productService;
        this.productTypeService = productTypeService;
    }

    @GetMapping("/productTechnicalProcess")
    public String getMenuPage(
        ModelMap model,
        @RequestParam(value = "typeValue", required = false) Long typeValue
    ) {
        List<Justification> justificationList = justificationService.getAllByEntityType(JustificationTechnicalProcess.class);
        StringBuilder url = new StringBuilder("productTechnicalProcess");
        if (!justificationList.isEmpty()) {
            if (typeValue != null && typeValue > 0) {
                url.insert(0, "section/");
                model.addAttribute("justificationType", JustificationType.TECHNICAL_PROCESS);
                Justification justification = justificationService.read(typeValue);
                model.addAttribute("justification", justification);
                model.addAttribute("justificationList", justificationList);
                model.addAttribute("productTechnicalProcessList",
                    productTechnicalProcessService.getAllByJustification(justification));
            } else {
                url.insert(0, "redirect:/").append("?typeValue=")
                    .append(justificationList.get(0).getId());
            }
        } else {
            url.insert(0, "section/");
        }
        return url.toString();
    }

    @GetMapping("/editProductTechnicalProcess")
    public String getEditProductTechnicalProcessPage(
        ModelMap model,
        @RequestParam("docType") String docType,
        @RequestParam(value = "entityId", required = false) Long entityId
    ) {
        EditProductTechnicalProcessForm editProductTechnicalProcessForm = new EditProductTechnicalProcessForm();
        if ("edit".equals(docType)) {
            ProductTechnicalProcess productTechnicalProcess = productTechnicalProcessService.read(entityId);
            Justification justification = HibernateUtil.initializeAndUnproxy(productTechnicalProcess.getJustification());
            editProductTechnicalProcessForm.setJustification((JustificationTechnicalProcess) justification);
            editProductTechnicalProcessForm.setProduct(productTechnicalProcess.getProduct());
            editProductTechnicalProcessForm.setName(productTechnicalProcess.getName());
            editProductTechnicalProcessForm.setSource(productTechnicalProcess.getSource());
            editProductTechnicalProcessForm.setId(entityId);
            editProductTechnicalProcessForm.setApproved(productTechnicalProcess.getApproved());
            List<EditProductTechnicalProcessForm.LaboriousnessForm> formList = productTechnicalProcess
                .getLaboriousnessList().stream().map(laboriousness -> {
                    EditProductTechnicalProcessForm.LaboriousnessForm ff =
                        new EditProductTechnicalProcessForm.LaboriousnessForm();
                    ff.setId(laboriousness.getId());
                    ff.setValue(laboriousness.getValue());
                    ff.setNumber(laboriousness.getNumber());
                    ff.setWithPackage(laboriousness.getWithPackage());
                    ff.setWorkTypeId(laboriousness.getWorkType().getId());
                    return ff;
                }).collect(Collectors.toList());
            editProductTechnicalProcessForm.setLaboriousnessFormList(formList);
        }
        model.addAttribute("workTypeList", workTypeService.getAll());
        model.addAttribute("editProductTechnicalProcessForm", editProductTechnicalProcessForm);
        model.addAttribute("docType", docType);
        return "edit/editProductTechnicalProcess";
    }

    /*@PostMapping(
        value = "/editProductTechnicalProcess",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public ValidatorJSONResponse saveProductTechnicalProcess(
        EditProductTechnicalProcessForm editProductTechnicalProcessForm,
        BindingResult result,
        HttpServletRequest request
    ) {
        ValidatorJSONResponse response = new ValidatorJSONResponse();
        editProductTechnicalProcessForm.validate(editProductTechnicalProcessForm, result);
        if (result.hasErrors()) {
            response.fill(result, request);
        } else {
            ProductTechnicalProcess productTechnicalProcess = editProductTechnicalProcessForm.getId() != null ?
                productTechnicalProcessService.read(editProductTechnicalProcessForm.getId()) :
                new ProductTechnicalProcess();
            editProductTechnicalProcessForm.getDeleteLaboriousnessList().forEach(laboriousnessService::deleteById);
            productTechnicalProcess.setJustification(editProductTechnicalProcessForm.getJustification());
            productTechnicalProcess.setProduct(editProductTechnicalProcessForm.getProduct());
            productTechnicalProcess.setName(editProductTechnicalProcessForm.getName());
            productTechnicalProcess.setSource(editProductTechnicalProcessForm.getSource());
            productTechnicalProcess.setApproved(editProductTechnicalProcessForm.getApproved());
            productTechnicalProcessService.save(productTechnicalProcess);
            List<Laboriousness> laboriousnessList = new ArrayList<>();
            int index = 1;
            for (EditProductTechnicalProcessForm.LaboriousnessForm ff : editProductTechnicalProcessForm.getLaboriousnessFormList()) {
                Laboriousness laboriousness = ff.getId() != null ? laboriousnessService.read(ff.getId()) : new Laboriousness();
                laboriousness.setProductTechnicalProcess(productTechnicalProcess);
                laboriousness.setValue(!ff.getValue().trim().isEmpty() ? ff.getValue() : "0.000");
                laboriousness.setNumber(ff.getNumber());
                laboriousness.setWithPackage(ff.getWithPackage() != null);
                laboriousness.setWorkType(workTypeService.read(ff.getWorkTypeId()));
                laboriousness.setSort(index);
                laboriousnessList.add(laboriousness);
                index++;
            }
            laboriousnessService.saveAll(laboriousnessList);
        }
        return response;
    }*/

    @GetMapping("/product/selectedProduct")
    public String selectedProduct(ModelMap model) {
        model.addAttribute("productTypeList", productTypeService.getAll());
        return "prod/include/product/selectedProduct";
    }

    /*@PostMapping("/ajaxSelectedProduct")
    @ResponseBody
    public DataTablesOutput<Product> ajaxSelectedProduct(
        HttpServletRequest request,
        @RequestParam(value = "searchFormData", required = false) String searchFormData
    ) throws IOException {
        ProductListFilterForm form = jsonMapper.readValue(searchFormData, new TypeReference<>() {});
        DataTablesInput dataTablesInput = new DataTablesInput(request);
        DataTablesOutput<Product> dataTablesOutput = new DataTablesOutput<>();
        dataTablesOutput.setDraw(dataTablesInput.getDraw());
        List<Product> productList = productService.getByForm(form, dataTablesInput);
        dataTablesOutput.setData(productList);
        long count = productService.getCountByForm(form);
        dataTablesOutput.setRecordsFiltered(count);
        dataTablesOutput.setRecordsTotal(count);
        return dataTablesOutput;
    }*/

    /*@PostMapping("/approvedProcess")
    @ResponseBody
    public ValidatorJSONResponse approvedProcess(
        HttpServletRequest request,
        @RequestParam(value = "productTechnicalProcessId") Long productTechnicalProcessId
    ) {
        ValidatorJSONResponse response = new ValidatorJSONResponse();
        ProductTechnicalProcess productTechnicalProcess = productTechnicalProcessService.read(productTechnicalProcessId);
        if (productTechnicalProcess.getLaboriousnessList().stream().anyMatch(laboriousness -> "0.000".equals(laboriousness.getValue()))) {
            response.putErrorMessage(request, "laboriousnessValue", "validator.form.productTechnicalProcess.valueRequired", new Object[]{});
        } else {
            productTechnicalProcess.setApproved(!productTechnicalProcess.getApproved());
            productTechnicalProcessService.save(productTechnicalProcess);
        }
        return response;
    }*/

    /*@PostMapping("/commonActionProductTechnicalProcess")
    @ResponseBody
    public ValidatorJSONResponse approvedAllProductTechnicalProcess(
        HttpServletRequest request,
        @RequestParam("justificationId") Long justificationId
    ) {
        ValidatorJSONResponse response = new ValidatorJSONResponse();
        productTechnicalProcessService.getAllByJustification(justificationService.read(justificationId))
            .forEach(productTechnicalProcess -> {
                if (productTechnicalProcess.getLaboriousnessList().stream().anyMatch(laboriousness -> "0.000".equals(laboriousness.getValue()))) {
                    response.putErrorMessage(request, "laboriousnessValue", "validator.form.productTechnicalProcess.valueRequired", new Object[]{});
                } else {
                    productTechnicalProcess.setApproved(true);
                    productTechnicalProcessService.save(productTechnicalProcess);
                }
            });
        return response;
    }*/

    @GetMapping("/cloneProductTechnicalProcess")
    public String cloneProductTechnicalProcess(
        ModelMap model,
        @RequestParam("justificationId") Long justificationId
    ) {
        model.addAttribute("justificationList",
            justificationService.getAllByEntityTypeAndIdNot(JustificationTechnicalProcess.class, justificationId));
        return "prod/include/product/cloneProductTechnicalProcess";
    }

    @PostMapping("/cloneProductTechnicalProcess")
    @ResponseBody
    public void cloneProductTechnicalProcessSave(
        @RequestParam("justificationId") Long justificationId,
        @RequestParam("processes") String processes
    ) {
        Justification justification = justificationService.read(justificationId);
        List<Long> processList = Arrays.stream(processes.split(",")).map(Long::valueOf).collect(Collectors.toList());
        processList.forEach(id -> {
            ProductTechnicalProcess productTechnicalProcess = productTechnicalProcessService.read(id);
            ProductTechnicalProcess newProductTechnicalProcess = new ProductTechnicalProcess();
            newProductTechnicalProcess.setApproved(false);
            newProductTechnicalProcess.setJustification(justification);
            newProductTechnicalProcess.setName("(Копия) " + productTechnicalProcess.getName());
            newProductTechnicalProcess.setSource(productTechnicalProcess.getSource());
            newProductTechnicalProcess.setProduct(productTechnicalProcess.getProduct());
            productTechnicalProcessService.save(newProductTechnicalProcess);

            List<Laboriousness> laboriousnessList = productTechnicalProcess.getLaboriousnessList().stream()
                .map(laboriousness -> {
                    Laboriousness lab = new Laboriousness();
                    lab.setProductTechnicalProcess(newProductTechnicalProcess);
                    lab.setSort(laboriousness.getSort());
                    lab.setValue(laboriousness.getValue());
                    lab.setNumber(laboriousness.getNumber());
                    lab.setWithPackage(laboriousness.getWithPackage());
                    lab.setWorkType(laboriousness.getWorkType());
                    return lab;
                }).collect(Collectors.toList());
            laboriousnessService.saveAll(laboriousnessList);
        });
    }

    /*@PostMapping(
        value = "/ajaxProductTechnicalProcess",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public DataTablesOutput<ProductTechnicalProcess> ajaxProductTechnicalProcess(HttpServletRequest request) {
        DataTablesInput dataTablesInput = new DataTablesInput(request);
        DataTablesOutput<ProductTechnicalProcess> dataTablesOutput = new DataTablesOutput<>();
        dataTablesOutput.setDraw(dataTablesInput.getDraw());
        List<ProductTechnicalProcess> productTechnicalProcessList =
            productTechnicalProcessService.getAllByJustification(
                justificationService.read(Long.valueOf(request.getParameter("justificationId"))));
        dataTablesOutput.setData(productTechnicalProcessList);
        long count = productTechnicalProcessList.size();
        dataTablesOutput.setRecordsFiltered(count);
        dataTablesOutput.setRecordsTotal(count);
        return dataTablesOutput;
    }*/

    @PostMapping("/deleteProductTechnicalProcess")
    @ResponseBody
    public void deleteProductTechnicalProcess(@RequestParam("entityId") Long entityId) {
        productTechnicalProcessService.deleteById(entityId);
    }

    @GetMapping("/productTechnicalProcess/productSearch")
    public String getProductSearch(ModelMap model) {
        return "search/searchProduct";
    }
}