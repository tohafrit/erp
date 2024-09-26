package ru.korundm.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.dao.JustificationService;
import ru.korundm.dao.LaboriousnessCalculationService;
import ru.korundm.dao.ProductTechnicalProcessService;
import ru.korundm.entity.Justification;
import ru.korundm.entity.JustificationTechnicalProcess;
import ru.korundm.entity.LaboriousnessCalculation;
import ru.korundm.enumeration.JustificationType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LaboriousnessCalculationController {

    private final ProductTechnicalProcessService productTechnicalProcessService;
    private final LaboriousnessCalculationService laboriousnessCalculationService;
    private final JustificationService justificationService;

    public LaboriousnessCalculationController(
        ProductTechnicalProcessService productTechnicalProcessService,
        LaboriousnessCalculationService laboriousnessCalculationService,
        JustificationService justificationService
    ) {
        this.productTechnicalProcessService = productTechnicalProcessService;
        this.laboriousnessCalculationService = laboriousnessCalculationService;
        this.justificationService = justificationService;
    }

    @GetMapping("/laboriousnessCalculation")
    public String getCalculationPage(
        ModelMap model,
        @RequestParam(value = "typeValue", required = false) Long typeValue
    ) {
        List<Justification> justificationList = justificationService.getAllByEntityType(JustificationTechnicalProcess.class);
        StringBuilder url = new StringBuilder("laboriousnessCalculation");
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

    @PostMapping("/laboriousnessCalculation")
    public String getMenuPage(
        ModelMap model,
        @RequestParam("id") Long productTechnicalProcessId
    ) {
        LaboriousnessCalculation laboriousnessCalculation = laboriousnessCalculationService
            .getByProductTechnicalProcessId(productTechnicalProcessId);
        if (laboriousnessCalculation == null) {
            laboriousnessCalculation = new LaboriousnessCalculation();
            laboriousnessCalculation.setProductTechnicalProcess(productTechnicalProcessService.read(productTechnicalProcessId));
            laboriousnessCalculation.setWithPackage(Boolean.FALSE);
            laboriousnessCalculationService.save(laboriousnessCalculation);
        }
        model.addAttribute("laboriousnessCalculation", laboriousnessCalculation);
        return "prod/include/laboriousness/calculation";
    }

    @RequestMapping("/calculationTreeList")
    public String getCalculationTreeListPage(
        ModelMap model,
        @RequestParam(value = "entityId", required = false) Long entityId, // идентификатор сортируемого элемента
        @RequestParam(value = "parentEntityId", required = false) Long parentEntityId, // идентификатор родительского элемента
        @RequestParam(value = "mainEntityId", required = false) Long mainEntityId // идентификатор элемента, от которого строится вся иерархия
    ) {
        if (entityId != null) {
            LaboriousnessCalculation laboriousnessCalculation = laboriousnessCalculationService.read(entityId),
                parentLaboriousnessCalculation = laboriousnessCalculationService.read(parentEntityId != null ? parentEntityId : mainEntityId);
            laboriousnessCalculation.setParent(parentLaboriousnessCalculation);
            laboriousnessCalculationService.save(laboriousnessCalculation);
        }
        model.addAttribute("laboriousnessCalculationList", laboriousnessCalculationService.read(mainEntityId).getChildList());
        model.addAttribute("mainEntityId", mainEntityId);
        return "prod/include/laboriousness/calculationTreeList";
    }

    @GetMapping("/selectProductTechnicalProcess")
    public String selectLaboriousness(
        ModelMap model,
        @RequestParam("laboriousnessCalculationId") Long laboriousnessCalculationId,
        @RequestParam("justificationId") Long justificationId
    ) {
        LaboriousnessCalculation laboriousnessCalculation = laboriousnessCalculationService.read(laboriousnessCalculationId);
        model.addAttribute("productTechnicalProcessList",
            productTechnicalProcessService.getAllByParams(justificationService.read(justificationId), laboriousnessCalculation.getProductTechnicalProcess().getId()));
        model.addAttribute("laboriousnessCalculation", laboriousnessCalculation);
        return "prod/include/product/selectProductTechnicalProcess";
    }

    @PostMapping("/addLaboriousnessCalculation")
    @ResponseBody
    public void addLaboriousnessCalculation(
        ModelMap model,
        @RequestParam("processes") String processes,
        @RequestParam("laboriousnessCalculationId") Long laboriousnessCalculationId
    ) {
        LaboriousnessCalculation laboriousnessCalculation = laboriousnessCalculationService.read(laboriousnessCalculationId);
        List<Long> processList = Arrays.stream(processes.split(",")).map(Long::valueOf).collect(Collectors.toList());
        List<LaboriousnessCalculation> laboriousnessCalculationList = new ArrayList<>();
        for (Long processId : processList) {
            LaboriousnessCalculation newLaboriousnessCalculation = new LaboriousnessCalculation();
            newLaboriousnessCalculation.setCount(1);
            newLaboriousnessCalculation.setProductTechnicalProcess(productTechnicalProcessService.read(processId));
            newLaboriousnessCalculation.setParent(laboriousnessCalculation);
            laboriousnessCalculationList.add(newLaboriousnessCalculation);
        }
        laboriousnessCalculationService.saveAll(laboriousnessCalculationList);
        model.addAttribute("laboriousnessCalculationList", laboriousnessCalculationList);
    }

    @PostMapping("/deleteLaboriousnessCalculation")
    @ResponseBody
    public void deleteLaboriousnessCalculation(@RequestParam("entityId") Long entityId) {
        laboriousnessCalculationService.deleteById(entityId);
    }

    @PostMapping("/calculationWithPackage")
    @ResponseBody
    public void calculationWithPackage(@RequestParam("laboriousnessCalculationId") Long laboriousnessCalculationId) {
        LaboriousnessCalculation laboriousnessCalculation = laboriousnessCalculationService.read(laboriousnessCalculationId);
        laboriousnessCalculation.setWithPackage(!laboriousnessCalculation.getWithPackage());
        laboriousnessCalculationService.save(laboriousnessCalculation);
    }
}