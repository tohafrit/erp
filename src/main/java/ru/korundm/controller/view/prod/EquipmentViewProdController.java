package ru.korundm.controller.view.prod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.*;
import ru.korundm.entity.Equipment;
import ru.korundm.entity.EquipmentType;
import ru.korundm.entity.EquipmentUnitProductionArea;
import ru.korundm.enumeration.WaterType;
import ru.korundm.form.edit.EditEquipmentForm;
import ru.korundm.form.search.EquipmentListFilterForm;

import java.util.List;
import java.util.stream.Collectors;

@ViewController(RequestPath.View.Prod.EQUIPMENT)
@SessionAttributes(
    names = "equipmentListFilterForm",
    types = EquipmentListFilterForm.class
)
public class EquipmentViewProdController {

    private static final String EQUIPMENT_LIST_FILTER_FORM_ATTR = "equipmentListFilterForm";

    private final ObjectMapper jsonMapper;
    private final EquipmentService equipmentService;
    private final ProductionAreaService productionAreaService;
    private final ProducerService producerService;
    private final EquipmentTypeService equipmentTypeService;
    private final UserService userService;

    public EquipmentViewProdController(
        ObjectMapper jsonMapper,
        EquipmentService equipmentService,
        ProductionAreaService productionAreaService,
        ProducerService producerService,
        EquipmentTypeService equipmentTypeService,
        UserService userService
    ) {
        this.jsonMapper = jsonMapper;
        this.equipmentService = equipmentService;
        this.productionAreaService = productionAreaService;
        this.producerService = producerService;
        this.equipmentTypeService = equipmentTypeService;
        this.userService = userService;
    }

    @ModelAttribute(EQUIPMENT_LIST_FILTER_FORM_ATTR)
    public EquipmentListFilterForm equipmentListFilterFormAttr() {
        EquipmentListFilterForm form = new EquipmentListFilterForm();
        List<EquipmentType> equipmentTypeList = equipmentTypeService.getAll();
        if (!equipmentTypeList.isEmpty()) {
            form.setEquipmentTypeId(equipmentTypeList.get(0).getId());
        }
        return form;
    }

    @GetMapping("/list")
    public String list(
        ModelMap model,
        String filterForm,
        @RequestParam(required = false) Long selectedEquipmentId
    ) throws JsonProcessingException {
        Integer lastPage = null;
        EquipmentListFilterForm form = (EquipmentListFilterForm) model.get(EQUIPMENT_LIST_FILTER_FORM_ATTR);
        long typeId;
        if (filterForm != null) {
            // если запрос пришёл от кнопки поиска и filterForm не null, берём typeId оттуда
            form = jsonMapper.readValue(filterForm, EquipmentListFilterForm.class);
            model.addAttribute(EQUIPMENT_LIST_FILTER_FORM_ATTR, form);
            typeId = form.getEquipmentTypeId();
        } else if (form != null) {
            // если filterForm не заполнена, но в модели хранится форма, берём typeId из неё
            typeId = form.getEquipmentTypeId();
        } else {
            // по умолчанию выставляем единицу
            typeId = 1;
        }
        if (selectedEquipmentId != null) {
            // На данный момент переход осуществляется на последнюю страницу
            // но при необходимости можно сменить на конкретную страницу для переданного изделия
            if (form == null) {
                form = new EquipmentListFilterForm();
                form.setEquipmentTypeId(1L);
            }
            long totalSize = equipmentService.getCountByForm(form);
            lastPage = (int) totalSize / 30;
            lastPage = totalSize % 30 == .0 ? lastPage : lastPage + 1;
        }
        model.addAttribute("initialPage", lastPage);
        model.addAttribute("selectedEquipmentId", selectedEquipmentId);
        model.addAttribute("equipmentType", equipmentTypeService.read(typeId));
        return "prod/include/equipment/list";
    }

    @GetMapping("/list/filter")
    public String list_filter(ModelMap model) {
        model.addAttribute("userList", userService.getAll());
        model.addAttribute("equipmentTypeList", equipmentTypeService.getAll());
        model.addAttribute("productionAreaList", productionAreaService.getAllByTechnological(Boolean.TRUE));
        return "prod/include/equipment/list/filter";
    }

    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditEquipmentForm form = new EditEquipmentForm();
        List<EquipmentType> equipmentTypeList = equipmentTypeService.getAll();
        if (id != null) {
            Equipment equipment = equipmentService.read(id);
            form.setId(equipment.getId());
            form.setEquipmentType(equipment.getEquipmentType());
        } else {
            if (!equipmentTypeList.isEmpty()) {
                form.setEquipmentType(equipmentTypeList.get(0));
            }
        }
        model.addAttribute("form", form);
        model.addAttribute("equipmentTypeList", equipmentTypeList);
        model.addAttribute("productionAreaList", productionAreaService.getAllByTechnological(Boolean.TRUE));
        return "prod/include/equipment/list/edit";
    }

    @GetMapping("/list/edit/type")
    public String list_edit_type(ModelMap model, long typeId, Long equipmentId) {
        EquipmentType equipmentType = equipmentTypeService.read(typeId);
        String page;
        switch (equipmentType.getName()) {
            case "machine_park":
                model.addAttribute("waterTypeList", WaterType.values());
                model.addAttribute("producerList", producerService.getAll());
                page = "machinePark";
                break;
            case "workplace":
                model.addAttribute("userList", userService.getAll());
                page = "workplace";
                break;
            default:
                model.addAttribute("waterTypeList", WaterType.values());
                model.addAttribute("producerList", producerService.getAll());
                page = "forTP";
                break;
        }
        EditEquipmentForm form = new EditEquipmentForm();
        if (equipmentId != null) {
            Equipment equipment = equipmentService.read(equipmentId);
            form.setArchive(equipment.isArchive());
            form.setName(equipment.getName());
            form.setEmployee(equipment.getUser());
            form.setShift(equipment.getShift());
            form.setProducer(equipment.getProducer());
            form.setModel(equipment.getModel());
            form.setWeight(equipment.getWeight());
            form.setVoltage(equipment.getVoltage());
            form.setPower(equipment.getPower());
            String dimensions = equipment.getDimensions();
            if (StringUtils.isNotBlank(dimensions)) {
                String[] dimensionsArray = dimensions.split("x");
                if (dimensionsArray.length == 3) {
                    form.setDimensionsLength(dimensionsArray[0]);
                    form.setDimensionsDepth(dimensionsArray[1]);
                    form.setDimensionsWidth(dimensionsArray[2]);
                }
            }

            form.setCompressedAirPressure(equipment.getCompressedAirPressure());
            form.setCompressedAirConsumption(equipment.getCompressedAirConsumption());
            form.setNitrogenPressure(equipment.getNitrogenPressure());
            form.setWater(equipment.getWater());
            form.setSewerage(equipment.isSewerage());
            form.setExtractorVolume(equipment.getExtractorVolume());
            form.setExtractorDiameter(equipment.getExtractorDiameter());
            form.setLink(equipment.getLink());
            if (!equipment.getEquipmentUnitList().isEmpty()) {
                List<EditEquipmentForm.EditEquipmentUnitForm> editEquipmentUnitFormList = equipment.getEquipmentUnitList().stream()
                    .map(equipmentUnit -> {
                        EditEquipmentForm.EditEquipmentUnitForm editEquipmentUnitForm = new EditEquipmentForm.EditEquipmentUnitForm();
                        EquipmentUnitProductionArea equipmentUnitProductionArea = equipmentUnit.getLastEquipmentUnitProductionArea();
                        if (equipmentUnitProductionArea != null) {
                            editEquipmentUnitForm.setAreaId(equipmentUnitProductionArea.getProductionArea().getId());
                        }
                        editEquipmentUnitForm.setCode(equipmentUnit.getUnitCode());
                        editEquipmentUnitForm.setId(equipmentUnit.getId());
                        editEquipmentUnitForm.setInventoryNumber(equipmentUnit.getInventoryNumber());
                        editEquipmentUnitForm.setSerialNumber(equipmentUnit.getSerialNumber());
                        return editEquipmentUnitForm;
                    }).collect(Collectors.toList());
                form.setEquipmentUnitList(editEquipmentUnitFormList);
            }
        } else {
            form.setShift(1);
        }
        model.addAttribute("productionAreaList", productionAreaService.getAllByTechnological(Boolean.TRUE));
        model.addAttribute("form", form);
        return "prod/include/equipment/list/type/" + page;
    }
}