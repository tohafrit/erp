package ru.korundm.controller.view.prod;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.EquipmentService;
import ru.korundm.dao.EquipmentUnitEventService;
import ru.korundm.dao.EquipmentUnitEventTypeService;
import ru.korundm.entity.EquipmentUnitEvent;
import ru.korundm.form.edit.EditEquipmentUnitEventForm;

@ViewController(RequestPath.View.Prod.EQUIPMENT_UNIT_EVENT)
public class EquipmentUnitEventViewProdController {

    private final EquipmentUnitEventService equipmentUnitEventService;
    private final EquipmentUnitEventTypeService equipmentUnitEventTypeService;
    private final EquipmentService equipmentService;

    public EquipmentUnitEventViewProdController(
        EquipmentUnitEventService equipmentUnitEventService,
        EquipmentUnitEventTypeService equipmentUnitEventTypeService,
        EquipmentService equipmentService
    ) {
        this.equipmentUnitEventService = equipmentUnitEventService;
        this.equipmentUnitEventTypeService = equipmentUnitEventTypeService;
        this.equipmentService = equipmentService;
    }

    @GetMapping("/list")
    public String list(
        ModelMap model,
        @RequestParam(required = false) Long selectedEventId
    ) {
        Integer lastPage = null;
        if (selectedEventId != null) {
            lastPage = equipmentUnitEventService.getPageById(selectedEventId);
        }
        model.addAttribute("initialPage", lastPage);
        model.addAttribute("selectedEventId", selectedEventId);
        return "prod/include/equipment-unit-event/list";
    }

    @GetMapping("/list/info")
    public String list_info(ModelMap model, long id) {
        model.addAttribute("equipment", equipmentService.read(id));
        return "prod/include/equipment-unit-event/list/info";
    }

    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditEquipmentUnitEventForm form = new EditEquipmentUnitEventForm();
        if (id != null) {
            EquipmentUnitEvent equipmentUnitEvent = equipmentUnitEventService.read(id);
            form.setId(equipmentUnitEvent.getId());
            form.setEquipmentUnit(equipmentUnitEvent.getEquipmentUnit());
            form.setEquipmentUnitEventType(equipmentUnitEvent.getEquipmentUnitEventType());
            form.setName(equipmentUnitEvent.getName());
            form.setCommentary(equipmentUnitEvent.getCommentary());
            form.setDateEventOn(equipmentUnitEvent.getEventOn().toLocalDate());
        }
        model.addAttribute("form", form);
        model.addAttribute("equipmentUnitEventTypeList", equipmentUnitEventTypeService.getAll());
        return "prod/include/equipment-unit-event/list/edit";
    }

    @GetMapping("/list/edit/equipment-unit")
    public String list_edit_equipmentUnit() {
        return "prod/include/equipment-unit-event/list/edit/equipmentUnit";
    }
}