package ru.korundm.controller.user;

import eco.dao.EcoLabourPriceService;
import eco.dao.EcoLabourProtocolService;
import eco.entity.EcoLabourProtocol;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.korundm.constant.BaseConstant;

import java.util.List;

/**
 * TODO после ухода от ECO удалить данный класс
 */
@Controller
public class LabourProtocolController {

    private final EcoLabourProtocolService ecoLabourProtocolService;
    private final EcoLabourPriceService ecoLabourPriceService;

    public LabourProtocolController(
        EcoLabourProtocolService ecoLabourProtocolService,
        EcoLabourPriceService ecoLabourPriceService
    ) {
        this.ecoLabourProtocolService = ecoLabourProtocolService;
        this.ecoLabourPriceService = ecoLabourPriceService;
    }

    @GetMapping("/labourProtocol")
    public String labourProtocol(
        ModelMap model,
        @RequestParam(value = "typeValue", required = false) Long typeValue
    ) {
        List<EcoLabourProtocol> labourProtocolList = ecoLabourProtocolService.getAllByCompanyId(BaseConstant.ECO_MAIN_PLANT_ID);
        if (typeValue != null) {
            model.addAttribute("labourProtocolList", labourProtocolList);
            model.addAttribute("labourProtocol", ecoLabourProtocolService.read(typeValue));
            model.addAttribute("labourPriceList", ecoLabourPriceService.getAllByProtocolId(typeValue));
            return "section/labourProtocol";
        }
        return "redirect:/labourProtocol?typeValue=" + labourProtocolList.get(0).getId();
    }
}