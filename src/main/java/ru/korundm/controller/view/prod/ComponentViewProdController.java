package ru.korundm.controller.view.prod;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import ru.korundm.annotation.ViewController;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.*;
import ru.korundm.dto.DropdownOption;
import ru.korundm.dto.component.ComponentAttributeItem;
import ru.korundm.entity.*;
import ru.korundm.enumeration.CompanyTypeEnum;
import ru.korundm.enumeration.ComponentLifecycle;
import ru.korundm.exception.AlertUIException;
import ru.korundm.form.edit.EditComponentForm;
import ru.korundm.form.search.ComponentListFilterForm;
import ru.korundm.form.search.ComponentListOccurrenceFilterForm;
import ru.korundm.util.KtCommonUtil;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@ViewController(RequestPath.View.Prod.COMPONENT)
@SessionAttributes(
    names = {
        "componentListFilterForm",
        "componentListOccurrenceFilterForm"
    },
    types = {
        ComponentListFilterForm.class,
        ComponentListOccurrenceFilterForm.class
    }
)
public class ComponentViewProdController {

    private static final String COMPONENT_LIST_FILTER_FORM_ATTR = "componentListFilterForm";
    private static final String COMPONENT_LIST_OCCURRENCE_FILTER_FORM_ATTR = "componentListOccurrenceFilterForm";

    private final ComponentService componentService;
    private final ComponentCategoryService componentCategoryService;
    private final CompanyService companyService;
    private final ComponentPurposeService purposeService;
    private final OkeiService okeiService;
    private final ComponentInstallationTypeService installationService;
    private final ComponentKindService componentKindService;
    private final LaunchService launchService;
    private final UserService userService;
    private final ComponentCommentService componentCommentService;

    public ComponentViewProdController(
        ComponentService componentService,
        ComponentCategoryService componentCategoryService,
        CompanyService companyService,
        ComponentPurposeService purposeService,
        OkeiService okeiService,
        ComponentInstallationTypeService installationService,
        ComponentKindService componentKindService,
        LaunchService launchService,
        UserService userService,
        ComponentCommentService componentCommentService
    ) {
        this.componentService = componentService;
        this.componentCategoryService = componentCategoryService;
        this.companyService = companyService;
        this.purposeService = purposeService;
        this.okeiService = okeiService;
        this.installationService = installationService;
        this.componentKindService = componentKindService;
        this.launchService = launchService;
        this.userService = userService;
        this.componentCommentService = componentCommentService;
    }

    @ModelAttribute(COMPONENT_LIST_FILTER_FORM_ATTR)
    public ComponentListFilterForm componentListFilterFormAttr() {
        return new ComponentListFilterForm();
    }

    @ModelAttribute(COMPONENT_LIST_OCCURRENCE_FILTER_FORM_ATTR)
    public ComponentListOccurrenceFilterForm componentListOccurrenceFilterFormAttr() {
        return new ComponentListOccurrenceFilterForm();
    }

    @GetMapping("/list")
    public String list(
        ModelMap model,
        Long lifecycleId
    ) {
        model.addAttribute("lifecycle", ComponentLifecycle.getById(lifecycleId));
        model.addAttribute("isNew", Objects.equals(ComponentLifecycle.NEW.getId(), lifecycleId));
        model.addAttribute("isDesign", Objects.equals(ComponentLifecycle.DESIGN.getId(), lifecycleId));
        model.addAttribute("isIndustrial", Objects.equals(ComponentLifecycle.INDUSTRIAL.getId(), lifecycleId));
        return "prod/include/component/list";
    }

    // Фильтр компонентов
    @GetMapping("/list/filter")
    public String list_filter(ModelMap model, Long lifecycleId) {
        model.addAttribute("categoryList", componentCategoryService.getAll());
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.COMPONENT_PRODUCER));
        model.addAttribute("launchList", launchService.findAllSortedYearNumberDesc(null));
        model.addAttribute("isNew", Objects.equals(ComponentLifecycle.NEW.getId(), lifecycleId));
        return "prod/include/component/list/filter";
    }

    // Редактирование компонента
    @GetMapping("/list/edit")
    public String list_edit(
        ModelMap model,
        Long id,
        boolean addAsDesign,
        boolean copy
    ) {
        EditComponentForm form = new EditComponentForm();
        if (id != null) {
            Component component = componentService.read(id);
            form.setName(component.getName());
            form.setCategory(component.getCategory());
            form.setDescription(component.getDescription());
            form.setPurchaseComponent(component.getPurchaseComponent());
            form.setPurchaseComponentData(component.getPurchaseComponentDate() == null ? "" : component.getPurchaseComponentDate().format(BaseConstant.INSTANCE.getDATE_FORMATTER()));
            form.setSubstituteComponent(component.getSubstituteComponent());
            form.setPurpose(component.getPurpose());
            form.setProcessed(component.isProcessed());
            form.setPrice(component.getPrice());
            form.setDeliveryTime(component.getDeliveryTime());
            form.setInstallation(component.getInstallation());
            form.setOkei(component.getOkei());
            form.setProducer(component.getProducer());
            form.setKind(component.getKind());
            if (!copy) {
                form.setId(id);
                form.setLockVersion(component.getLockVersion());
                form.setModifiedDatetime(component.getModifiedDatetime());
                form.setPosition(component.getPosition());
            }
        }
        form.setAddAsDesign(addAsDesign);
        model.addAttribute("form", form);
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.COMPONENT_PRODUCER));
        model.addAttribute("purposeList", purposeService.getAll());
        model.addAttribute("okeiList", okeiService.getAll());
        model.addAttribute("installationList", installationService.getAll());
        model.addAttribute("categoryList", componentCategoryService.getAll());
        model.addAttribute("kindList", componentKindService.getAll());
        model.addAttribute("componentId", id);
        return "prod/include/component/list/edit";
    }

    // Атрибуты компонента
    @GetMapping("/list/edit/attribute")
    public String list_edit_attribute(ModelMap model, Long componentId, Long categoryId) {
        Component component = componentId != null ? componentService.read(componentId) : null;
        ComponentCategory componentCategory = componentCategoryService.read(categoryId);
        List<ComponentCategory> categoryList = new ArrayList<>();
        while (componentCategory != null) {
            categoryList.add(componentCategory);
            componentCategory = componentCategory.getParent();
        }
        categoryList.sort(Comparator.comparingInt(ComponentCategory::level));
        // Формируем список атрибутов
        Map<ComponentCategory, List<ComponentAttributeItem>> categoryAttributeMap = new HashMap<>();
        categoryList.forEach(category -> {
            List<ComponentAttributeItem> attributeList = new ArrayList<>();
            // Определяем категорию в мапу, если есть атрибуты
            List<ComponentAttribute> componentAttributeList = category.getComponentAttributeList();
            if (!componentAttributeList.isEmpty()) {
                categoryAttributeMap.put(category, attributeList);
            }
            componentAttributeList.forEach(componentAttribute -> {
                ComponentAttributeItem attribute = new ComponentAttributeItem();
                attributeList.add(attribute);
                attribute.setId(componentAttribute.getId());
                attribute.setType(componentAttribute.getType());
                attribute.setName(componentAttribute.getName());
                // Получаем настройки атрибутов
                List<DropdownOption> selectOptionList = new ArrayList<>();
                List<ComponentAttributePreference> preferenceList = componentAttribute.getComponentAttributePreferenceList();
                preferenceList.sort(Comparator.comparingInt(ComponentAttributePreference::getSortIndex));
                preferenceList.forEach(preference -> {
                    switch (preference.getType()) {
                        case REQUIRED:
                            attribute.setRequired(BooleanUtils.toBooleanDefaultIfNull(preference.getBoolValue(), Boolean.FALSE));
                            break;
                        case DISABLED:
                            attribute.setDisabled(BooleanUtils.toBooleanDefaultIfNull(preference.getBoolValue(), Boolean.FALSE));
                            break;
                        case MULTIPLE:
                            attribute.setMultiple(BooleanUtils.toBooleanDefaultIfNull(preference.getBoolValue(), Boolean.FALSE));
                            break;
                        case SELECT_POSTFIX:
                            attribute.setSelectPostfix(preference.getStringValue());
                            break;
                        case SELECT_OPTION:
                            selectOptionList.add(new DropdownOption(preference.getId(), preference.getStringValue(), false));
                            break;
                    }
                });
                selectOptionList.forEach(option ->
                    option.setValue(StringUtils.defaultIfBlank(option.getValue(), StringUtils.EMPTY)
                        .concat(StringUtils.defaultIfBlank(attribute.getSelectPostfix(), StringUtils.EMPTY)))
                );
                attribute.setSelectOptionList(selectOptionList);
                if (component != null) {
                    List<ComponentAttributeValue> valueList = component.getComponentAttributeValueList();
                    valueList.stream().filter(value -> Objects.equals(value.getAttribute(), componentAttribute)).forEach(value -> {
                        switch (value.getAttribute().getType()) {
                            case SELECT:
                                Long selectedId = value.getLongValue();
                                if (selectedId != null) {
                                    if (attribute.isMultiple()) {
                                        attribute.getSelectOptionIdList().add(selectedId);
                                    } else {
                                        attribute.setSelectOptionId(selectedId);
                                    }
                                }
                                break;
                            case CHECKBOX:
                                Boolean boolValue = value.getBoolValue();
                                attribute.setBoolValue(boolValue == null ? Boolean.FALSE : boolValue);
                                break;
                            case INPUT:
                                attribute.setStringValue(value.getStringValue());
                                break;
                        }
                    });
                }
            });
        });
        model.addAttribute("categoryAttributeMap", categoryAttributeMap);
        return "prod/include/component/list/edit/attribute";
    }

    // Список компонентов для установки замен
    @GetMapping("/list/set-component-replacement")
    public String list_setComponentReplacement(ModelMap model, Long componentId, int mode) {
        var component = componentService.read(componentId);
        if (component == null) throw new AlertUIException("Компонент не найден");
        model.addAttribute("componentId", componentId);
        model.addAttribute("componentName", component.getName());
        model.addAttribute("mode", mode);
        model.addAttribute("replacementDate", LocalDate.now().minusYears(1).format(BaseConstant.INSTANCE.getDATE_FORMATTER()));
        model.addAttribute("categoryId", component.getCategory().getId());
        return "prod/include/component/list/setComponentReplacement";
    }

    // Фильтр замен по компонентам
    @GetMapping("/list/set-component-replacement/filter")
    public String list_setComponentReplacement_filter(ModelMap model) {
        model.addAttribute("categoryList", componentCategoryService.getAll().stream().map(it -> new DropdownOption(it.getId(), it.getName(), false)).collect(Collectors.toList()));
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.COMPONENT_PRODUCER).stream().map(it -> new DropdownOption(it.getId(), it.getName(), false)).collect(Collectors.toList()));
        return "prod/include/component/list/set-component-replacement/filter";
    }

    // Загрузка вхождений компонента
    @GetMapping("/list/occurrence")
    public String list_occurrence(ModelMap model, Long id) {
        model.addAttribute("componentId", id);
        return "prod/include/component/list/occurrence";
    }

    // Фильтр вхождений компонентов
    @GetMapping("/list/occurrence/filter")
    public String list_occurrence_filter() {
        return "prod/include/component/list/occurrence/filter";
    }

    // Загрузка окна со списком компонентов
    @GetMapping("/list/replace")
    public String list_replace(ModelMap model, Long componentId) {
        var comp = componentService.read(componentId);
        model.addAttribute("componentId", comp.getId());
        model.addAttribute("componentName", comp.getName());
        model.addAttribute("categoryId", comp.getCategory().getId());
        return "prod/include/component/list/replace";
    }

    // Фильтр замен по компонентам
    @GetMapping("/list/replace/filter")
    public String list_replace_filter(ModelMap model) {
        model.addAttribute("categoryList", componentCategoryService.getAll().stream().map(it -> new DropdownOption(it.getId(), it.getName(), false)).collect(Collectors.toList()));
        model.addAttribute("producerList", companyService.getAllByType(CompanyTypeEnum.COMPONENT_PRODUCER).stream().map(it -> new DropdownOption(it.getId(), it.getName(), false)).collect(Collectors.toList()));
        return "prod/include/component/list/replace/filter";
    }

    // Загрузка окна комментариев
    @GetMapping("/list/comment")
    public String list_comment(ModelMap model, Long id) {
        model.addAttribute("componentId", id);
        model.addAttribute("userList", userService.getAll());
        return "prod/include/component/list/comment";
    }

    // Загрузка добавления/редакторования комментария
    @GetMapping("/list/comment/edit")
    public String list_comment_edit(
        HttpSession session,
        ModelMap model,
        Long id,
        long componentId
    ) {
        var compComment = id == null ? null : componentCommentService.read(id);
        if (id != null && compComment == null) throw new AlertUIException("Комментарий не найден");
        model.addAttribute("id", id);
        model.addAttribute("componentId", componentId);
        model.addAttribute("createdBy", (compComment == null ? KtCommonUtil.INSTANCE.getUser(session) : compComment.getUser()).getUserOfficialName());
        model.addAttribute("createdDate", (compComment == null ? LocalDateTime.now() : compComment.getCreateDatetime()).format(BaseConstant.INSTANCE.getDATE_TIME_FORMATTER()));
        model.addAttribute("comment", compComment == null ? "" : compComment.getComment());
        return "prod/include/component/list/comment/edit";
    }
}