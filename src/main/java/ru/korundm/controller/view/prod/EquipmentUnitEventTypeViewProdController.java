package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.EquipmentUnitEventTypeService;
import ru.korundm.entity.EquipmentUnitEventType;

@ViewController(RequestPath.View.Prod.EQUIPMENT_UNIT_EVENT_TYPE)
public class EquipmentUnitEventTypeViewProdController {

    private final EquipmentUnitEventTypeService equipmentUnitEventTypeService;

    public EquipmentUnitEventTypeViewProdController(EquipmentUnitEventTypeService equipmentUnitEventTypeService) {
        this.equipmentUnitEventTypeService = equipmentUnitEventTypeService;
    }

    @GetMapping("/edit")
    public String edit(ModelMap model, Long id) {
        EquipmentUnitEventType equipmentUnitEventType = id != null ? equipmentUnitEventTypeService.read(id) : new EquipmentUnitEventType();
        model.addAttribute("equipmentUnitEventType", equipmentUnitEventType);
        return "prod/include/equipment-unit-event-type/edit";
    }
}