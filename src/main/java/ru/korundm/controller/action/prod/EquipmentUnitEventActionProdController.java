package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.EquipmentUnitEventService;
import ru.korundm.dao.EquipmentUnitService;
import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentUnit;
import ru.korundm.entity.EquipmentUnitEvent;
import ru.korundm.entity.Producer;
import ru.korundm.form.edit.EditEquipmentUnitEventForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrOut;
import ru.korundm.helper.TabrResultQuery;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ActionController(RequestPath.Action.Prod.EQUIPMENT_UNIT_EVENT)
public class EquipmentUnitEventActionProdController {

    private final EquipmentUnitService equipmentUnitService;
    private final EquipmentUnitEventService equipmentUnitEventService;

    public EquipmentUnitEventActionProdController(
        EquipmentUnitService equipmentUnitService,
        EquipmentUnitEventService equipmentUnitEventService
    ) {
        this.equipmentUnitService = equipmentUnitService;
        this.equipmentUnitEventService = equipmentUnitEventService;
    }

    @GetMapping("/list/load")
    public TabrOut<?> list_load(
        HttpServletRequest request,
        @RequestParam(required = false) Boolean initLoad
    ) {
        @Getter
        class TableItemOut {
            long id;
            String name; // наименование
            String eventType; // наименование типа события
            String equipmentName; // наименование оборудования
            long equipmentId; // идентификатор оборудования
            String serialNumber; // серийный номер единицы оборудования
            String inventoryNumber; // инвентарный номер единицы оборудования
            LocalDateTime eventOn; // дата события
            String commentary; // комментарий
        }
        TabrIn input = new TabrIn(request);
        if (BooleanUtils.toBoolean(initLoad)) {
            TabrOut<TableItemOut> output = new TabrOut<>();
            output.setCurrentPage(input.getPage());
            output.setLastPage(input.getSize(), equipmentUnitEventService.getCount());
            return output;
        }
        TabrResultQuery<EquipmentUnitEvent> dataResultQuery = equipmentUnitEventService.getByTableDataIn(input);
        List<TableItemOut> itemOutList = dataResultQuery.getData().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            itemOut.eventType = item.getEquipmentUnitEventType() != null ? item.getEquipmentUnitEventType().getName() : null;
            EquipmentUnit equipmentUnit = item.getEquipmentUnit();
            if (equipmentUnit != null) {
                Equipment equipment = equipmentUnit.getEquipment();
                if (equipment != null) {
                    itemOut.equipmentName = equipment.getName();
                    itemOut.equipmentId = equipment.getId();
                }
                itemOut.serialNumber = equipmentUnit.getSerialNumber();
                itemOut.inventoryNumber = equipmentUnit.getInventoryNumber();
            }
            itemOut.eventOn = item.getEventOn();
            itemOut.commentary = item.getCommentary();
            return itemOut;
        }).collect(Collectors.toList());
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), dataResultQuery.getCount());
        output.setData(itemOutList);
        return output;
    }

    @GetMapping("/list/edit/equipment-unit/load")
    public TabrOut<?> list_edit_equipmentUnit_load(HttpServletRequest request) {
        @Getter
        class TableItemOut {
            long id;
            String name; // наименование
            String producer; // наименование производителя
            String model; // модель оборудования
            String productionArea; // наименование производственного участка
            String serialNumber; // серийный номер
            String inventoryNumber; // инвентарный номер
        }
        TabrIn input = new TabrIn(request);
        TabrResultQuery<EquipmentUnit> dataResultQuery = equipmentUnitService.getByTableDataIn(input);
        List<TableItemOut> itemOutList = dataResultQuery.getData().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            Equipment equipment = item.getEquipment();
            if (equipment != null) {
                Producer producer = equipment.getProducer();
                if (producer != null) {
                    itemOut.producer = producer.getName();
                }
                itemOut.name = equipment.getName();
                itemOut.model = equipment.getModel();
            }
            if (item.getLastEquipmentUnitProductionArea() != null && item.getLastEquipmentUnitProductionArea().getProductionArea() != null) {
                itemOut.productionArea = item.getLastEquipmentUnitProductionArea().getProductionArea().getName();
            }
            itemOut.serialNumber = item.getSerialNumber();
            itemOut.inventoryNumber = item.getInventoryNumber();
            return itemOut;
        }).collect(Collectors.toList());
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), dataResultQuery.getCount());
        output.setData(itemOutList);
        return output;
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditEquipmentUnitEventForm form) {
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            Long formId = form.getId();
            EquipmentUnitEvent equipmentUnitEvent = formId == null ? new EquipmentUnitEvent() : equipmentUnitEventService.read(formId);
            equipmentUnitEvent.setEquipmentUnit(form.getEquipmentUnit());
            equipmentUnitEvent.setEquipmentUnitEventType(form.getEquipmentUnitEventType());
            equipmentUnitEvent.setName(form.getName());
            equipmentUnitEvent.setCommentary(form.getCommentary());
            equipmentUnitEvent.setEventOn(form.getDateEventOn() == null ? null : form.getDateEventOn().atStartOfDay());
            equipmentUnitEventService.save(equipmentUnitEvent);
            if (formId == null) {
                response.putAttribute("addedEventId", equipmentUnitEvent.getId());
            }
        }
        return response;
    }

    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        equipmentUnitEventService.deleteById(id);
    }
}