package ru.korundm.controller.action.prod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.dao.*;
import ru.korundm.entity.*;
import ru.korundm.form.edit.EditEquipmentForm;
import ru.korundm.form.search.EquipmentListFilterForm;
import ru.korundm.helper.ValidatorResponse;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.TabrOut;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.korundm.util.FormValidatorUtil.assertFormId;

@ActionController(RequestPath.Action.Prod.EQUIPMENT)
@SessionAttributes(
    names = "equipmentListFilterForm",
    types = EquipmentListFilterForm.class
)
public class EquipmentActionProdController {

    private static final String EQUIPMENT_LIST_FILTER_FORM_ATTR = "equipmentListFilterForm";

    private final ObjectMapper jsonMapper;
    private final EquipmentService equipmentService;
    private final ProductionAreaService productionAreaService;
    private final EquipmentUnitService equipmentUnitService;
    private final EquipmentUnitProductionAreaService equipmentUnitProductionAreaService;
    private final EquipmentTypeService equipmentTypeService;

    public EquipmentActionProdController(
        ObjectMapper jsonMapper,
        EquipmentService equipmentService,
        ProductionAreaService productionAreaService,
        EquipmentUnitService equipmentUnitService,
        EquipmentUnitProductionAreaService equipmentUnitProductionAreaService,
        EquipmentTypeService equipmentTypeService
    ) {
        this.jsonMapper = jsonMapper;
        this.equipmentService = equipmentService;
        this.productionAreaService = productionAreaService;
        this.equipmentUnitService = equipmentUnitService;
        this.equipmentUnitProductionAreaService = equipmentUnitProductionAreaService;
        this.equipmentTypeService = equipmentTypeService;
    }

    @GetMapping("/list/load")
    public TabrOut<?> list_load(
        HttpServletRequest request,
        ModelMap model,
        String filterForm,
        @RequestParam(required = false) Boolean initLoad
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id; // идентификатор
            String name; // наименование
            String producerName; // производитель
            String model; // модель
            Integer weight; // масса
            String voltage; // напряжение
            Integer power; // мощность
            String dimensions; // габариты
            String compressedAirPressure; // сжатый воздух (давление)
            String compressedAirConsumption; // сжатый воздух (расход)
            String nitrogenPressure; // давление азота
            String water; // вода
            boolean sewerage; // канализация
            String extractorVolume; // вытяжка (объем)
            String extractorDiameter; // вытяжка (диаметр)
            String link; // документация
            String areaName; // участок
            String code; // код
            String user; // сотрудник
            Integer shift; // сменность
            boolean archive; // архивность
        }
        TabrIn input = new TabrIn(request);
        EquipmentListFilterForm form = jsonMapper.readValue(filterForm, EquipmentListFilterForm.class);
        if (BooleanUtils.toBoolean(initLoad)) {
            TabrOut<TableItemOut> output = new TabrOut<>();
            output.setCurrentPage(input.getPage());
            output.setLastPage(input.getSize(), equipmentService.getCountByForm(form));
            return output;
        }
        model.addAttribute(EQUIPMENT_LIST_FILTER_FORM_ATTR, form);
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), equipmentService.getCountByForm(form));
        List<TableItemOut> itemOutList = equipmentService.getByTableDataIn(input, form).stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.name = item.getName();
            itemOut.producerName = item.getProducer() != null ? item.getProducer().getName() : "";
            itemOut.model = item.getModel();
            itemOut.weight = item.getWeight();
            itemOut.voltage = item.getVoltage();
            itemOut.power = item.getPower();
            itemOut.dimensions = item.getDimensions();
            itemOut.compressedAirPressure = item.getCompressedAirPressure();
            itemOut.compressedAirConsumption = item.getCompressedAirConsumption();
            itemOut.nitrogenPressure = item.getNitrogenPressure();
            itemOut.water = item.getWater();
            itemOut.sewerage = item.isSewerage();
            itemOut.extractorVolume = item.getExtractorVolume();
            itemOut.extractorDiameter = item.getExtractorDiameter();
            itemOut.link = item.getLink();
            itemOut.archive = item.isArchive();
            List<EquipmentUnit> equipmentUnitList = item.getEquipmentUnitList();
            if (!equipmentUnitList.isEmpty()) {
                EquipmentUnit firstUnit = item.getEquipmentUnitList().get(0);
                EquipmentUnitProductionArea eupa = firstUnit.getLastEquipmentUnitProductionArea();
                if (eupa != null) {
                    itemOut.areaName = eupa.getProductionArea() != null ? eupa.getProductionArea().getName() : "";
                }
                itemOut.code = firstUnit.getUnitCode();
            }
            itemOut.user = item.getUser() != null ? item.getUser().getUserOfficialName() : "";
            itemOut.shift = item.getShift();
            return itemOut;
        }).collect(Collectors.toList());
        output.setData(itemOutList);
        return output;
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditEquipmentForm form) {
        ValidatorResponse response = new ValidatorResponse(form);
        EquipmentType equipmentType = equipmentTypeService.read(form.getEquipmentType().getId());
        switch (equipmentType.getName()) {
            case "machine_park":
                if (form.getEquipmentUnitList().stream().anyMatch(equipmentUnit -> StringUtils.isBlank(equipmentUnit.getInventoryNumber()) && StringUtils.isBlank(equipmentUnit.getSerialNumber()))) {
                    response.putError("equipmentUnitList", "validator.equipment.fieldsRequired");
                }
                break;
            case "for_tp":
                if (form.getEquipmentUnitList().stream().anyMatch(equipmentUnit -> equipmentUnit.getAreaId() == null)) {
                    response.putError("equipmentUnitList", "validator.equipment.productionAreaRequired");
                }
                break;
            case "workplace":
                if (form.getShift() == null) {
                    response.putError("shift", ValidatorMsg.REQUIRED);
                }
                if (form.getEquipmentUnitList().get(0).getAreaId() == null) {
                    response.putError("productionArea", ValidatorMsg.REQUIRED);
                }
                break;
        }
        if (response.isValid()) {
            // Оборудование
            Long formId = form.getId();
            Equipment equipment = formId != null ? equipmentService.read(formId) : new Equipment();
            equipment.setEquipmentType(equipmentType);
            equipment.setArchive(form.isArchive());
            equipment.setName(form.getName());
            equipment.setUser(assertFormId(form.getEmployee()));
            equipment.setShift(form.getShift());
            equipment.setProducer(assertFormId(form.getProducer()));
            equipment.setModel(form.getModel());
            equipment.setWeight(form.getWeight());
            equipment.setVoltage(form.getVoltage());
            equipment.setPower(form.getPower());
            if (StringUtils.isNotBlank(form.getDimensionsLength()) && StringUtils.isNotBlank(form.getDimensionsDepth()) && StringUtils.isNotBlank(form.getDimensionsWidth())) {
                equipment.setDimensions(form.getDimensionsLength() + "x" + form.getDimensionsDepth() + "x" + form.getDimensionsWidth());
            }
            equipment.setCompressedAirPressure(form.getCompressedAirPressure());
            equipment.setCompressedAirConsumption(form.getCompressedAirConsumption());
            equipment.setNitrogenPressure(form.getNitrogenPressure());
            equipment.setWater(form.getWater());
            equipment.setSewerage(form.isSewerage());
            equipment.setExtractorVolume(form.getExtractorVolume());
            equipment.setExtractorDiameter(form.getExtractorDiameter());
            equipment.setLink(form.getLink());

            equipmentService.save(equipment);

            if (formId == null) {
                response.putAttribute("addedEquipmentId", equipment.getId());
                response.putAttribute("addedEquipmentType", equipment.getEquipmentType().getId());
            }

            // Единицы оборудования
            List<EditEquipmentForm.EditEquipmentUnitForm> equipmentUnitFormList = form.getEquipmentUnitList();
            // Список единиц на удаление
            List<EquipmentUnit> equipmentUnitListForDelete = form.getEquipmentUnitList().isEmpty() ?
                equipmentUnitService.getAllByEquipment(equipment) : equipmentUnitService.getAllByIdIsNotInAndEquipment(
                equipmentUnitFormList.stream().map(EditEquipmentForm.EditEquipmentUnitForm::getId).collect(Collectors.toList()),
                equipment
            );
            // Предварительно удаляем все зависимости единиц
            if (!equipmentUnitListForDelete.isEmpty()) {
                equipmentUnitService.deleteAll(equipmentUnitListForDelete);
            }
            // Сохраняем полученные с формы
            equipmentUnitFormList.forEach(equipmentUnitForm -> {
                EquipmentUnit last = equipmentUnitService.getByAreaIdAndEquipmentTypeId(equipmentUnitForm.getAreaId(), form.getEquipmentType().getId());
                EquipmentUnit equipmentUnit = equipmentUnitForm.getId() != null ? equipmentUnitService.read(equipmentUnitForm.getId()) : new EquipmentUnit();
                equipmentUnit.setEquipment(equipment);
                equipmentUnit.setSerialNumber(equipmentUnitForm.getSerialNumber());
                equipmentUnit.setInventoryNumber(equipmentUnitForm.getInventoryNumber());
                if (last != null && !last.getId().equals(equipmentUnitForm.getId())) {
                    equipmentUnit.setCode(last.getCode() + 1);
                }
                equipmentUnitService.save(equipmentUnit);
                // Производственный участок
                ProductionArea productionArea = assertFormId(equipmentUnitForm.getAreaId()) == null ? null : productionAreaService.read(equipmentUnitForm.getAreaId());
                ProductionArea euProductionArea = equipmentUnit.getLastEquipmentUnitProductionArea() == null ? null : equipmentUnit.getLastEquipmentUnitProductionArea().getProductionArea();
                if (!Objects.equals(euProductionArea, productionArea)) {
                    EquipmentUnitProductionArea equipmentUnitProductionArea = new EquipmentUnitProductionArea();
                    equipmentUnitProductionArea.setProductionArea(productionArea);
                    equipmentUnitProductionArea.setEquipmentUnit(equipmentUnit);
                    equipmentUnitProductionArea.setMovedOn(LocalDateTime.now());
                    equipmentUnitProductionAreaService.save(equipmentUnitProductionArea);
                }
            });
        }
        return response;
    }

    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        equipmentService.deleteById(id);
    }
}