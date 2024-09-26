package ru.korundm.controller.user;

import eco.dao.*;
import eco.entity.EcoPurchase;
import eco.entity.EcoSnapshot;
import eco.entity.EcoSnapshotParameter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.korundm.dto.purchase.Snapshot;
import ru.korundm.enumeration.SnapshotType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SnapshotController {

    private final EcoSnapshotParameterService snapshotParameterService;
    private final EcoProductService productService;
    private final EcoBomService bomService;
    private final EcoBomAttributeService bomAttributeService;
    private final EcoSnapshotService snapshotService;
    private final EcoPurchaseService purchaseService;

    public SnapshotController(
        EcoSnapshotParameterService snapshotParameterService,
        EcoProductService productService,
        EcoBomService bomService,
        EcoBomAttributeService bomAttributeService,
        EcoSnapshotService snapshotService,
        EcoPurchaseService purchaseService
    ) {
        this.snapshotParameterService = snapshotParameterService;
        this.productService = productService;
        this.bomService = bomService;
        this.bomAttributeService = bomAttributeService;
        this.snapshotService = snapshotService;
        this.purchaseService = purchaseService;
    }

    // Меню информации о сливе
    @PostMapping("/purchase/information")
    public String information(
        ModelMap model,
        @RequestParam(value = "id", required = false) Long purchaseId
    ) {
        model.addAttribute("purchaseId", purchaseId);
        return "prod/include/purchase/information/windowContainer";
    }

    // Обязательства по текущему запуску
    @GetMapping("/purchase/information/obligation/currentLaunch")
    public String obligationCurrentLaunch(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        model.addAttribute("purchaseId", purchaseId);
        model.addAttribute("snapshotType", SnapshotType.CURRENT_LAUNCH);
        model.addAttribute("snapshotParameterList",
            snapshotParameterService.getAllByPurchaseAndType(purchaseId, SnapshotType.CURRENT_LAUNCH.getId()));
        return "prod/include/purchase/information/obligation/currentLaunch";
    }

    /*@PostMapping(
        value = "/ajaxLoadSnapshot/obligationLaunch",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public DataTablesOutput<EcoSnapshot> ajaxLoadSnapshot(
        HttpServletRequest request,
        @RequestParam(value = "snapshotParameterId", required = false) Long snapshotParameterId,
        @RequestParam("snapshotTypeId") Long snapshotTypeId
    ) {
        DataTablesOutput<EcoSnapshot> dataTablesOutput = new DataTablesOutput<>();
        DataTablesInput dataTablesInput = new DataTablesInput(request);
        dataTablesOutput.setDraw(dataTablesInput.getDraw());
        List<EcoSnapshot> snapshotList = Collections.emptyList();
        if (snapshotParameterId != null) {
            snapshotList = snapshotService.getAllByParams(snapshotParameterId, dataTablesInput);
            EcoSnapshotParameter snapshotParameter = snapshotParameterService.read(snapshotParameterId);
            if (snapshotTypeId != null && SnapshotType.PREVIOUS_LAUNCH != SnapshotType.getById(snapshotTypeId)) {
                snapshotList = snapshotList.stream().peek(snapshot -> {
                    snapshot.setAccepted(bomAttributeService.isAccepted(snapshot.getBom(), snapshotParameter.getPurchase().getLaunch()));
                    snapshot.setApproved(bomAttributeService.getApproved(snapshot.getBom()));
                }).collect(Collectors.toList());
            }
        }
        dataTablesOutput.setData(snapshotList);
        return dataTablesOutput;
    }*/

    @PostMapping("/refreshSnapshot")
    @ResponseBody
    public List<EcoSnapshotParameter> snapshot(
        @RequestParam("purchaseId") Long purchaseId,
        @RequestParam("snapshotTypeId") Long snapshotTypeId
    ) {
        EcoPurchase purchase = purchaseService.read(purchaseId);

        EcoSnapshotParameter snapshotParameter = new EcoSnapshotParameter();
        snapshotParameter.setGenerateOn(LocalDateTime.now());
        snapshotParameter.setType(snapshotTypeId);
        snapshotParameter.setPurchase(purchase);
//        snapshotParameterService.save(snapshotParameter);

        List<Snapshot> snapshotDTOList =
            snapshotService.getSnapshotList(purchase.getType(), purchase.getLaunch().getId());
        List<EcoSnapshot> snapshotList = snapshotDTOList.stream().map(s -> {
            EcoSnapshot snapshot = new EcoSnapshot();
            snapshot.setSnapshotParameter(snapshotParameter);
//            snapshot.setBom(s.getBom());
//            snapshot.setProduct(s.getProduct());
            snapshot.setAmount(s.getAmount());
            snapshot.setReserveAmount(s.getReserve());
            snapshot.setAmountContract(s.getAmountContract());
            snapshot.setAmountUnpaid(s.getAmountUnpaid());
            snapshot.setAmountUnalloted(s.getUnalloted());
            snapshot.setAmountInternal(s.getAmountInternal());
            return snapshot;
        }).collect(Collectors.toList());
        if (!snapshotList.isEmpty()) {
//            snapshotService.saveAll(snapshotList);
//            snapshotService.deleteProcTmpBom();
//            snapshotService.insertProcTmpBom(purchase.getType());
        }
        return snapshotParameterService.getAllByPurchaseAndType(purchaseId, snapshotTypeId);
    }

    // Обязательства по предыдущим запускам
    @GetMapping("/purchase/information/obligation/previousLaunch")
    public String obligationPreviousLaunch(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        List<EcoSnapshotParameter> snapshotParameterList =
            snapshotParameterService.getAllByPurchaseAndType(purchaseId, SnapshotType.PREVIOUS_LAUNCH.getId());
        model.addAttribute("snapshotType", SnapshotType.PREVIOUS_LAUNCH);
        model.addAttribute("snapshotParameterList", snapshotParameterList);
        return "prod/include/purchase/information/obligation/previousLaunch";
    }

    @GetMapping("/purchase/information/need/currentLaunch")
    public String needCurrentLaunch(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/need/currentLaunch";
    }

    @GetMapping("/purchase/information/need/previousLaunch")
    public String needPreviousLaunch(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/need/previousLaunch";
    }

    @GetMapping("/purchase/information/need/currentLaunchTolling")
    public String needCurrentLaunchTolling(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/need/currentLaunchTolling";
    }

    @GetMapping("/purchase/information/need/previousLaunchTolling")
    public String needPreviousLaunchTolling(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/need/previousLaunchTolling";
    }

    @GetMapping("/purchase/information/income/free")
    public String incomeFree(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/income/free";
    }

    @GetMapping("/purchase/information/income/safe")
    public String incomeSafe(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/income/safe";
    }

    @GetMapping("/purchase/information/income/component")
    public String incomeComponent(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/income/component";
    }

    @GetMapping("/purchase/information/income/way")
    public String incomeWay(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/income/way";
    }

    @GetMapping("/purchase/information/payment/nomenclature")
    public String paymentNomenclature(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/payment/nomenclature";
    }

    @GetMapping("/purchase/information/payment/component")
    public String paymentComponent(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/payment/component";
    }

    @GetMapping("/purchase/information/payment/product")
    public String paymentProduct(
        ModelMap model,
        @RequestParam(value = "purchaseId", required = false) Long purchaseId
    ) {
        return "prod/include/purchase/information/payment/product";
    }
}