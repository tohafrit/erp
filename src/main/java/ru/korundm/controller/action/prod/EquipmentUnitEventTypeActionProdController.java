package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.EquipmentUnitEventTypeService;
import ru.korundm.entity.EquipmentUnitEventType;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.util.HibernateUtil;

import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.EQUIPMENT_UNIT_EVENT_TYPE)
public class EquipmentUnitEventTypeActionProdController {

    private final EquipmentUnitEventTypeService equipmentUnitEventTypeService;

    public EquipmentUnitEventTypeActionProdController(EquipmentUnitEventTypeService equipmentUnitEventTypeService) {
        this.equipmentUnitEventTypeService = equipmentUnitEventTypeService;
    }

    @GetMapping("/load")
    public List<?> load() {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String name; // наименование
        }
        return equipmentUnitEventTypeService.getAll().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            return itemOut;
        }).collect(Collectors.toList());
    }

    @PostMapping("/edit/save")
    public ValidatorResponse edit_save(EquipmentUnitEventType equipmentUnitEventType) {
        ValidatorResponse response = new ValidatorResponse();
        response.fill(HibernateUtil.entityValidate(equipmentUnitEventType));
        if (response.isValid()) {
            equipmentUnitEventTypeService.save(equipmentUnitEventType);
        }
        return response;
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        equipmentUnitEventTypeService.deleteById(id);
    }
}