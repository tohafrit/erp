package ru.korundm.controller.action.prod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.BaseConstant;
import ru.korundm.constant.ObjAttr;
import ru.korundm.constant.RequestPath;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.dao.*;
import ru.korundm.dto.component.ComponentAttributeItem;
import ru.korundm.entity.*;
import ru.korundm.enumeration.ComponentAttributePreferenceType;
import ru.korundm.enumeration.ComponentAttributeType;
import ru.korundm.enumeration.ComponentLifecycle;
import ru.korundm.enumeration.ComponentType;
import ru.korundm.exception.AlertUIException;
import ru.korundm.form.edit.EditComponentForm;
import ru.korundm.form.search.ComponentListFilterForm;
import ru.korundm.form.search.ComponentListOccurrenceFilterForm;
import ru.korundm.form.search.ComponentListReplaceFilterForm;
import ru.korundm.helper.*;
import ru.korundm.util.CommonUtil;
import ru.korundm.util.KtCommonUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;

@ActionController(RequestPath.Action.Prod.COMPONENT)
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
public class ComponentActionProdController {

    private static final Locale locale = LocaleContextHolder.getLocale();

    @Resource
    private MessageSource messageSource;

    private static final String COMPONENT_LIST_FILTER_FORM_ATTR = "componentListFilterForm";
    private static final String COMPONENT_LIST_OCCURRENCE_FILTER_FORM_ATTR = "componentListOccurrenceFilterForm";

    private static final String COMPONENT_SUBSTITUTE_MARK = "#";
    private static final String COMPONENT_NOT_PROCESSED_MARK = "*";
    private static final String TECH_CHAR_SEPARATOR = "; ";
    private static final String DATA_DELIMITER = ", ";

    private static final int REPLACEMENT_COMPONENT_SUBSTITUTE = 1;
    private static final int REPLACEMENT_COMPONENT_PURCHASE = 2;
    private static final int REPLACEMENT_COMPONENT_DEPUTY = 3;

    private final ObjectMapper jsonMapper;
    private final ComponentService componentService;
    private final ComponentAttributePreferenceService componentAttributePreferenceService;
    private final BomItemService bomItemService;
    private final ComponentCommentService componentCommentService;
    private final BaseService baseService;

    public ComponentActionProdController(
        ObjectMapper jsonMapper,
        ComponentService componentService,
        ComponentAttributePreferenceService componentAttributePreferenceService,
        BomItemService bomItemService,
        ComponentCommentService componentCommentService,
        BaseService baseService
    ) {
        this.jsonMapper = jsonMapper;
        this.componentService = componentService;
        this.componentAttributePreferenceService = componentAttributePreferenceService;
        this.bomItemService = bomItemService;
        this.componentCommentService = componentCommentService;
        this.baseService = baseService;
    }

    // Загрузка списка компонентов
    @GetMapping("/list/load")
    public TabrOut<?> list_load(
        HttpServletRequest request,
        ModelMap model,
        String filterForm,
        Long lifecycleId,
        Long selectedId
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            long id;
            String position; // позиция
            String name; // наименование
            String producer; // производитель
            String category; // категория
            String description; // описание
            String product; // изделия
            String bom; // версия
            String techCharacteristics; // технические хар-ки
            String okei; // единица измерения
            String substituteComponent; // инфо о заместителе
            String purchaseComponent; // инфо о замене к закупке
            String purpose; // назначение
            String installation; // тип установки
            String type; // тип
            String kind; // еще один тип
            Double price; // ориентировочная цена
            String docPath; // путь к документации
            Integer deliveryTime; // срок поставки с завода, нед.
            LocalDateTime modifiedDatetime; // последнее изменение
            boolean processed; // обработан
            boolean approved; // утвержден
            boolean removeApprovedCtxMenu; // флаг разрешения кнопки снятия утверждения
            boolean substituteCtxMenu; // флаг разрешения на вывод кнопки добавления заместителя
            boolean addPurchaseCtxMenu; // флаг разрешения на вывод кнопки добавления замены к закупке
            boolean removePurchaseCtxMenu; // флаг разрешения на вывод кнопки удаления замены к закупке
            boolean replacementCtxMenu; // флаг разрешения кнопки замены по ЗС
        }
        TabrIn input = new TabrIn(request);
        ComponentListFilterForm form = jsonMapper.readValue(filterForm, ComponentListFilterForm.class);
        model.addAttribute(COMPONENT_LIST_FILTER_FORM_ATTR, form);
        form.setLifecycleId(lifecycleId);
        TabrResultQuery<Component> dataResultQuery = componentService.queryDataByFilterForm(input, form, selectedId);
        List<TableItemOut> itemOutList = dataResultQuery.getData().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            itemOut.position = item.getFormattedPosition();
            itemOut.name = item.getName();
            itemOut.producer = item.getProducer() == null ? null : item.getProducer().getName();
            itemOut.category = item.getCategory().getName();
            itemOut.description = item.getDescription();
            itemOut.product = item.getBomItemList().stream().map(bomItem -> bomItem.getBom().getProduct().getConditionalName())
                .collect(Collectors.joining("\n"));
            itemOut.bom = item.getBomItemList().stream().map(bomItem -> bomItem.getBom().getVersion())
                .collect(Collectors.joining("\n"));
            itemOut.techCharacteristics = generateTechCharacteristics(item);
            itemOut.okei = item.getOkei() == null ? null : item.getOkei().getSymbolNational();
            itemOut.purpose = item.getPurpose() == null ? null : item.getPurpose().getName();
            itemOut.installation = item.getInstallation() == null ? null : item.getInstallation().getName();
            itemOut.kind = item.getKind() == null ? null : item.getKind().getName();
            itemOut.type = messageSource.getMessage(ComponentType.getById(item.getType()).getProperty(), null, locale);
            itemOut.price = item.getPrice();
            itemOut.docPath = item.getDocPath();
            itemOut.deliveryTime = item.getDeliveryTime();
            itemOut.processed = item.isProcessed();
            itemOut.modifiedDatetime = item.getModifiedDatetime();
            itemOut.approved = item.isApproved();
            //
            Component substituteComponent = item.getSubstituteComponent();
            Component purchaseComponent = item.getPurchaseComponent();
            itemOut.substituteComponent = substituteComponent == null ? null : substituteComponent.getFormattedPosition() + DATA_DELIMITER + substituteComponent.getName();
            itemOut.purchaseComponent = purchaseComponent == null ? null : purchaseComponent.getFormattedPosition() + DATA_DELIMITER + purchaseComponent.getName()
                + (item.getPurchaseComponentDate() == null ? "" : " (" + item.getPurchaseComponentDate().format(BaseConstant.INSTANCE.getDATE_FORMATTER()) + ")");
            //
            List<ComponentLifecycle> lifeCycleList = item.getLifecycle();
            itemOut.removeApprovedCtxMenu = lifeCycleList.size() == 1 && lifeCycleList.contains(ComponentLifecycle.DESIGN) && item.isApproved();
            itemOut.substituteCtxMenu = lifeCycleList.contains(ComponentLifecycle.DESIGN) && item.getSubstituteComponent() == null;
            itemOut.addPurchaseCtxMenu = lifeCycleList.contains(ComponentLifecycle.DESIGN) && item.getPurchaseComponent() == null;
            itemOut.removePurchaseCtxMenu = lifeCycleList.contains(ComponentLifecycle.DESIGN) && item.getPurchaseComponent() != null;
            itemOut.replacementCtxMenu = lifeCycleList.contains(ComponentLifecycle.DESIGN);
            return itemOut;
        }).collect(Collectors.toList());
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), dataResultQuery.getCount());
        output.setData(itemOutList);
        return output;
    }

    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(EditComponentForm form) {
        List<ComponentAttributeItem> formAttributeList = form.getAttributeList();
        List<Long> formAttributeIdList = formAttributeList.stream().map(ComponentAttributeItem::getId).collect(Collectors.toList());
        // Узнаем некоторые настройки для валидации
        componentAttributePreferenceService.getAllByAttributeIdIn(formAttributeIdList).forEach(preference ->
            formAttributeList.forEach(attribute -> {
                if (Objects.equals(attribute.getId(), preference.getAttribute().getId())) {
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
                        case INPUT_MIN_LENGTH:
                            attribute.setInputMinLength(preference.getIntValue() == null ? 0 : preference.getIntValue());
                            break;
                        case INPUT_MAX_LENGTH:
                            attribute.setInputMaxLength(preference.getIntValue() == null ? 0 : preference.getIntValue());
                            break;
                    }
                }
            })
        );
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            Long formId = form.getId();
            Component component = formId == null ? new Component() : componentService.read(formId);
            component.setLockVersion(form.getLockVersion());
            component.setName(form.getName());
            component.setPosition(form.getPosition());
            component.setProcessed(form.isProcessed());
            component.setModifiedDatetime(LocalDateTime.now());
            component.setCategory(form.getCategory());
            component.setDescription(form.getDescription());
            component.setProcessed(form.isProcessed());
            component.setKind(form.getKind().getId() == null ? null : form.getKind());
            component.setProducer(form.getProducer().getId() == null ? null : form.getProducer());
            component.setOkei(form.getOkei().getId() == null ? null : form.getOkei());
            component.setPurpose(form.getPurpose().getId() == null ? null : form.getPurpose());
            component.setInstallation(form.getInstallation().getId() == null ? null : form.getInstallation());
            component.setPrice(form.getPrice());
            component.setDeliveryTime(form.getDeliveryTime());
            if (form.isAddAsDesign()) component.setApproved(true);
            if (formId == null) {
                component.setPosition(null);
                component.setType(ComponentType.NEW_COMPONENT.getId());
            }
            // Заполнение атрибутов
            List<ComponentAttributeValue> resultAttributeValueList = new ArrayList<>();
            List<ComponentAttributeValue> componentAttributeValueList = component.getComponentAttributeValueList();
            // Получаем значения атрибутов для селекта
            formAttributeList.forEach(formAttribute -> {
                if (ComponentAttributeType.SELECT.equals(formAttribute.getType())) {
                    List<ComponentAttributeValue> valueList = componentAttributeValueList.stream().filter(componentAttributeValue ->
                        ComponentAttributeType.SELECT.equals(componentAttributeValue.getAttribute().getType())
                            && Objects.equals(formAttribute.getId(), componentAttributeValue.getAttribute().getId())
                    ).collect(Collectors.toList());
                    if (formAttribute.isMultiple()) {
                        int size = valueList.size();
                        List<Long> optionIdList = formAttribute.getSelectOptionIdList();
                        for (int i = 0; i < optionIdList.size(); i++) {
                            Long optionId = optionIdList.get(i);
                            ComponentAttributeValue attributeValue;
                            if (i < size) {
                                attributeValue = valueList.get(i);
                            } else {
                                attributeValue = new ComponentAttributeValue();
                                attributeValue.setComponent(component);
                                //
                                ComponentAttribute componentAttribute = new ComponentAttribute();
                                componentAttribute.setId(formAttribute.getId());
                                attributeValue.setAttribute(componentAttribute);
                            }
                            attributeValue.setLongValue(optionId);
                            resultAttributeValueList.add(attributeValue);
                        }
                    } else {
                        Long selectOptionId = formAttribute.getSelectOptionId();
                        if (selectOptionId != null) {
                            ComponentAttributeValue attributeValue;
                            if (valueList.isEmpty()) {
                                attributeValue = new ComponentAttributeValue();
                                attributeValue.setComponent(component);
                                //
                                ComponentAttribute componentAttribute = new ComponentAttribute();
                                componentAttribute.setId(formAttribute.getId());
                                attributeValue.setAttribute(componentAttribute);
                            } else {
                                attributeValue = valueList.get(0);
                            }
                            attributeValue.setLongValue(selectOptionId);
                            resultAttributeValueList.add(attributeValue);
                        }
                    }
                }
            });
            // Получаем значения атрибутов чекбокса
            formAttributeList.forEach(formAttribute -> {
                if (ComponentAttributeType.CHECKBOX.equals(formAttribute.getType())) {
                    List<ComponentAttributeValue> valueList = componentAttributeValueList.stream().filter(componentAttributeValue ->
                        ComponentAttributeType.CHECKBOX.equals(componentAttributeValue.getAttribute().getType())
                            && Objects.equals(formAttribute.getId(), componentAttributeValue.getAttribute().getId())
                    ).collect(Collectors.toList());
                    ComponentAttributeValue attributeValue;
                    if (valueList.isEmpty()) {
                        attributeValue = new ComponentAttributeValue();
                        attributeValue.setComponent(component);
                        //
                        ComponentAttribute componentAttribute = new ComponentAttribute();
                        componentAttribute.setId(formAttribute.getId());
                        attributeValue.setAttribute(componentAttribute);
                        // Отключенный атрибут при привязки к компоненту не может менять свое состояние и всегда равен false
                        if (formAttribute.isDisabled()) {
                            attributeValue.setBoolValue(Boolean.FALSE);
                        } else {
                            attributeValue.setBoolValue(formAttribute.isBoolValue());
                        }
                    } else {
                        attributeValue = valueList.get(0);
                        if (!formAttribute.isDisabled()) {
                            attributeValue.setBoolValue(formAttribute.isBoolValue());
                        }
                    }
                    resultAttributeValueList.add(attributeValue);
                }
            });
            // Получаем значения атрибутов для поля ввода
            formAttributeList.forEach(formAttribute -> {
                if (ComponentAttributeType.INPUT.equals(formAttribute.getType())) {
                    List<ComponentAttributeValue> valueList = componentAttributeValueList.stream().filter(componentAttributeValue ->
                        ComponentAttributeType.INPUT.equals(componentAttributeValue.getAttribute().getType())
                            && Objects.equals(formAttribute.getId(), componentAttributeValue.getAttribute().getId())
                    ).collect(Collectors.toList());
                    ComponentAttributeValue attributeValue;
                    if (valueList.isEmpty() ) {
                        attributeValue = new ComponentAttributeValue();
                        attributeValue.setComponent(component);
                        //
                        ComponentAttribute componentAttribute = new ComponentAttribute();
                        componentAttribute.setId(formAttribute.getId());
                        attributeValue.setAttribute(componentAttribute);
                    } else {
                        attributeValue = valueList.get(0);
                    }
                    if (!formAttribute.isDisabled()) {
                        attributeValue.setStringValue(StringUtils.defaultIfBlank(formAttribute.getStringValue(), null));
                    }
                    resultAttributeValueList.add(attributeValue);
                }
            });

            componentAttributeValueList.clear();
            componentAttributeValueList.addAll(resultAttributeValueList);
            componentService.save(component);
            if (formId == null) response.putAttribute(ObjAttr.ID, component.getId());
        }
        return response;
    }

    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        componentService.deleteById(id);
    }

    // Утверждение/разутверждение компонентов
    @PostMapping("/list/component-approve")
    public void list_componentApprove(boolean approved, Long id) {
        Component component = componentService.read(id);
        component.setApproved(approved);
        componentService.save(component);
    }

    @GetMapping("/list/set-component-replacement/list-load")
    public TabrOut<?> list_setComponentReplacement_listLoad(
        HttpServletRequest request,
        String filterData
    ) {
        @Getter
        class TableItemOut {
            long id;
            String mark; // метка
            String name; // наименование
            String substituteComponent; // инфо о заместителе
            String producer; // производитель
            String position; // позиция
            String category; // категория
            String description; // описание
        }
        DynamicObject form = KtCommonUtil.INSTANCE.readDynamic(jsonMapper, filterData);
        TabrIn input = new TabrIn(request);
        TabrResultQuery<Component> dataResultQuery = componentService.findTableCompReplacementData(input, form);
        List<TableItemOut> itemOutList = dataResultQuery.getData().stream().map(item -> {
            TableItemOut itemOut = new TableItemOut();
            itemOut.id = item.getId();
            if (item.getSubstituteComponent() != null) {
                itemOut.mark = COMPONENT_SUBSTITUTE_MARK;
            } else if (!item.isProcessed()) {
                itemOut.mark = COMPONENT_NOT_PROCESSED_MARK;
            }
            itemOut.name = item.getName();
            Component substituteComponent = item.getSubstituteComponent();
            itemOut.substituteComponent = substituteComponent == null ?
                null : substituteComponent.getFormattedPosition() + DATA_DELIMITER + substituteComponent.getName();
            itemOut.producer = item.getProducer() == null ? null : item.getProducer().getName();
            itemOut.position = item.getFormattedPosition();
            itemOut.category = item.getCategory().getName();
            itemOut.description = item.getDescription();
            return itemOut;
        }).collect(Collectors.toList());
        TabrOut<TableItemOut> output = new TabrOut<>();
        output.setCurrentPage(input.getPage());
        output.setLastPage(input.getSize(), dataResultQuery.getCount());
        output.setData(itemOutList);
        return output;
    }

    // Сохранение компонента замены
    @PostMapping("/list/set-component-replacement/set-replacement")
    public Map<String, Object> list_setComponentReplacement_setReplacement(
        @RequestParam List<Long> compIdList,
        Long selectedComponentId,
        int mode,
        @DateTimeFormat(pattern = BaseConstant.DATE_PATTERN) LocalDate replacementDate
    ) {
        Map<String, Object> response = new HashMap<>();
        if (compIdList.isEmpty()) throw new AlertUIException("Укажите заменяемые компоненты");
        if (compIdList.stream().anyMatch(it -> Objects.equals(it, selectedComponentId))) {
            throw new AlertUIException("Список заменяемых компонентов не должнен содержать компонент замены");
        }
        AtomicInteger amount = new AtomicInteger();
        baseService.exec(em -> {
            Component selectedComponent = componentService.read(selectedComponentId);
            if (selectedComponent == null) throw new AlertUIException("Компонент замены был удален");

            var componentList = componentService.getAllById(compIdList);
            if (componentList.size() != compIdList.size()) throw new AlertUIException("Список заменяемых компонентов содержит компоненты, которые были удалены");
            componentList.forEach(comp -> {
                if (!comp.getLifecycle().contains(ComponentLifecycle.DESIGN)) throw new AlertUIException("Список выбранных компонент должен состоять из конструкторских компонент");
                if (REPLACEMENT_COMPONENT_SUBSTITUTE == mode) { // Компонент к замене в спецификациях
                    if (selectedComponent.getSubstituteComponent() != null) {
                        throw new AlertUIException("Компонент замены не может быть заместителем, т.к сам имеет заместителя");
                    }
                    if (comp.getSubstituteComponent() == null) {
                        componentService.setSubstituteComponent(comp.getId(), selectedComponentId);
                    }
                } else if (REPLACEMENT_COMPONENT_PURCHASE == mode) { // Замена к закупке
                    // TODO в оригинальной eco имеется логика расчета, по которой замену добавить нельзя
                    if (comp.getPurchaseComponent() == null) {
                        comp.setPurchaseComponent(selectedComponent);
                        comp.setPurchaseComponentDate(LocalDate.now());
                    }
                } else if (REPLACEMENT_COMPONENT_DEPUTY == mode) { // Компонент к замене в спецификациях
                    if (replacementDate == null) throw new AlertUIException("Укажите дату замены");
                    amount.set(amount.get() + componentService.setAnalogReplacement(
                        comp.getId(),
                        selectedComponent.getSubstituteComponent() == null ? selectedComponentId : selectedComponent.getSubstituteComponent().getId(),
                        replacementDate
                    ));
                }
            });
            return Unit.INSTANCE;
        });
        response.put(ObjAttr.AMOUNT, amount.get());
        return response;
    }

    // Очистка компонента замены
    @PostMapping("/list/unset-replacement")
    public void list_unsetReplacement(Long componentId, int mode) {
        Component component = componentService.read(componentId);
        if (component == null) throw new AlertUIException("Редактируемый компонент был удален");
        if (REPLACEMENT_COMPONENT_PURCHASE == mode) { // Замена к закупке
            if (component.getPurchaseComponent() == null) {
                throw new AlertUIException("Невозможно удалить замену к закупке, т.к. она была удалена ранее. Для просмотра актуального состояния нужно обновить страницу");
            } else {
                component.setPurchaseComponent(null);
                component.setPurchaseComponentDate(null);
                componentService.save(component);
            }
        }
    }

    // Загрузка списка вхождений
    @GetMapping("/list/occurrence/load")
    public List<?> list_occurrence_load(
        HttpServletRequest request,
        ModelMap model,
        String filterForm,
        Long componentId
    ) throws JsonProcessingException {
        @Getter
        class TableItemOut {
            String lead; // ведущий
            long productId; // id изделия
            long bomId; // id версии
            String conditionalName; // условное наименование изделия
            String version; // версия
            String identifier; // идентификатор
            boolean kd; // КД
            boolean purchase; // флаг закупки (вычисляемый)
            String approvedText; // утвержденные запуски
            String acceptedText; // принятые запуски
            boolean lastApprove; // последний подтвержденный
            boolean lastAccept; // последний принятый
        }
        ComponentListOccurrenceFilterForm form = jsonMapper.readValue(filterForm, ComponentListOccurrenceFilterForm.class);
        model.addAttribute(COMPONENT_LIST_OCCURRENCE_FILTER_FORM_ATTR, form);
        TabrIn input = new TabrIn(request);
        List<Component> componentReplacementList = componentService.read(componentId).getBomItemReplacementList().stream()
            .map(bomItemReplacement -> bomItemReplacement.getBomItem().getComponent()).collect(Collectors.toList());
        List<BomItem> bomItemList = bomItemService.queryDataByFilterForm(form, componentReplacementList);
        List<TableItemOut> itemOutTotalList = bomItemList.stream().map(bomItem -> {
            TableItemOut itemOut = new TableItemOut();
            //
            User lead = bomItem.getBom().getProduct().getLead();
            itemOut.lead = lead == null ? null : lead.getUserOfficialName();
            itemOut.productId = bomItem.getBom().getProduct().getId();
            itemOut.bomId = bomItem.getBom().getId();
            itemOut.conditionalName = bomItem.getBom().getProduct().getConditionalName();
            itemOut.version = bomItem.getBom().getVersion();
            itemOut.identifier = CommonUtil.formatZero(String.valueOf(bomItem.getBom().getDescriptor()), 5);
            itemOut.kd = Objects.equals(bomItem.getComponent().getId(), componentId);
            // Поиск компонента замены по приоритету - purchased флаг > глобальный компонент замены по закупке > целевой компонент
            List<BomItemReplacement> replacementList = bomItem.getBomItemReplacementList();
            Component purchaseComponent = replacementList.stream()
                .filter(BomItemReplacement::isPurchase)
                .map(BomItemReplacement::getComponent)
                .findFirst().orElse(null);
            if (purchaseComponent == null) {
                purchaseComponent = replacementList.stream()
                    .filter(replacement -> Objects.equals(replacement.getComponent(), bomItem.getComponent().getPurchaseComponent()))
                    .map(BomItemReplacement::getComponent)
                    .findFirst().orElse(null);
            }
            if (purchaseComponent == null) {
                purchaseComponent = bomItem.getComponent();
            }
            itemOut.purchase = Objects.equals(purchaseComponent.getId(), componentId);
            //
            List<BomAttribute> bomAttributeList = bomItem.getBom().getBomAttributeList();
            // Сортировка по запускам от позднего до раннего
            bomAttributeList.sort(
                Comparator
                    .comparing(BomAttribute::getLaunch, Comparator.comparing(Launch::getYear, Comparator.reverseOrder()))
                    .thenComparing(BomAttribute::getLaunch, Comparator.comparing(Launch::getNumber, Comparator.reverseOrder()))
            );
            //
            if (!bomAttributeList.isEmpty()) {
                itemOut.lastApprove = bomAttributeList.get(0).getApproveDate() != null;
                itemOut.lastAccept = bomAttributeList.get(0).getAcceptDate() != null;
            }
            //
            List<String> approvedText = new ArrayList<>();
            List<String> acceptedText = new ArrayList<>();
            for (var bomAttribute : bomAttributeList) {
                if (bomAttribute.getApproveDate() != null) {
                    approvedText.add(bomAttribute.getLaunch().getNumberInYear());
                }
                if (bomAttribute.getAcceptDate() != null) {
                    acceptedText.add(bomAttribute.getLaunch().getNumberInYear());
                }
            }
            itemOut.approvedText = String.join(", ", approvedText);
            itemOut.acceptedText = String.join(", ", acceptedText);
            return itemOut;
        }).collect(Collectors.toList());
        // Фильтр по тексту
        itemOutTotalList.removeIf(item ->
            StringUtils.isNotBlank(form.getApproveSearchText()) &&
                !StringUtils.contains(item.getApprovedText(), form.getApproveSearchText())
        );
        itemOutTotalList.removeIf(item ->
            StringUtils.isNotBlank(form.getAcceptSearchText()) &&
                !StringUtils.contains(item.getAcceptedText(), form.getAcceptSearchText())
        );
        // Фильтр по утверждению и принятию
        itemOutTotalList.removeIf(item -> form.isLastApprove() && item.lastApprove);
        itemOutTotalList.removeIf(item -> form.isLastAccept() && item.lastAccept);
        // Сортировка
        if (CollectionUtils.isNotEmpty(input.getSorters()) && CollectionUtils.isNotEmpty(itemOutTotalList)) {
            boolean isAsc = ASC.equals(input.getSorters().get(0).getDir());
            Comparator<String> orderStringComparator = Comparator.nullsLast(isAsc ? Comparator.<String>naturalOrder() : Comparator.<String>reverseOrder());
            Comparator<TableItemOut> comparator = Comparator.comparing(TableItemOut::getLead, orderStringComparator);
            switch (input.getSorters().get(0).getField()) {
                case "lead":
                    comparator = Comparator.comparing(TableItemOut::getLead, orderStringComparator);
                    break;
                case "conditionalName":
                    comparator = Comparator.comparing(TableItemOut::getConditionalName, orderStringComparator);
                    break;
                case "version":
                    comparator = Comparator.comparing(TableItemOut::getVersion, orderStringComparator);
                    break;
                case "identifier":
                    comparator = Comparator.comparing(TableItemOut::getIdentifier, orderStringComparator);
                    break;
                case "approvedText":
                    comparator = Comparator.comparing(TableItemOut::getApprovedText, orderStringComparator);
                    break;
                case "acceptedText":
                    comparator = Comparator.comparing(TableItemOut::getAcceptedText, orderStringComparator);
                    break;
            }
            itemOutTotalList.sort(comparator);
        }
        return itemOutTotalList;
    }

    @Getter @Setter @NoArgsConstructor
    public static class ListReplaceListItem {
        long id;
        String mark; // метка
        String name; // наименование
        String substituteComponent; // инфо о заместителе
        String producer; // производитель
        String position; // позиция
        String category; // категория
        String description; // описание
        //
        boolean processed;
        Integer positionNum; // позиция
        Long subComponentId;
        Integer subComponentPosition;
        String subComponentName;

        @SuppressWarnings("unused")
        public ListReplaceListItem(long id, String name, String producer, Integer positionNum, String category, String description, boolean processed, Long subComponentId, String subComponentName, Integer subComponentPosition) {
            this.id = id;
            this.name = name;
            this.producer = producer;
            this.positionNum = positionNum;
            this.category = category;
            this.description = description;
            this.processed = processed;
            this.subComponentId = subComponentId;
            this.subComponentName = subComponentName;
            this.subComponentPosition = subComponentPosition;
        }
    }

    @GetMapping("/list/replace/list-load")
    public TabrOut<?> list_replace_listLoad(
        HttpServletRequest request,
        String filterData
    ) throws JsonProcessingException {
        ComponentListReplaceFilterForm form = jsonMapper.readValue(filterData, ComponentListReplaceFilterForm.class);
        TabrIn input = new TabrIn(request);
        var resultQuery = componentService.queryDataByFilterForm(input, form, ListReplaceListItem.class);
        return TabrOut.Companion.instance(input, resultQuery, item -> {
            if (item.getSubComponentId() != null) {
                item.mark = COMPONENT_SUBSTITUTE_MARK;
            } else if (!item.processed) {
                item.mark = COMPONENT_NOT_PROCESSED_MARK;
            }
            item.substituteComponent = item.getSubComponentId() == null ?
                null : item.subComponentPosition == null ? StringUtils.EMPTY : StringUtils.leftPad(String.valueOf(item.subComponentPosition), 6, '0') + DATA_DELIMITER + item.subComponentName;
            item.position = item.positionNum == null ? StringUtils.EMPTY : StringUtils.leftPad(String.valueOf(item.positionNum), 6, '0');
            return item;
        });
    }

    // Замена нового компонента компонентом из справочника
    @PostMapping("/list/replace/save")
    public void list_replace_save(Long componentId, Long replaceId) {
        Component replace = componentService.read(replaceId);
        List<BomItem> bomItemList = bomItemService.getAllByComponentId(componentId).stream()
            .peek(bomItem -> bomItem.setComponent(replace)).collect(Collectors.toList());
        bomItemService.saveAll(bomItemList);
        componentService.deleteById(componentId);
    }

    @GetMapping("/list/replace/download")
    public void list_replace_download(
        HttpServletResponse response,
        HttpServletRequest request,
        String filterData
    ) throws JsonProcessingException {
        ComponentListReplaceFilterForm form = jsonMapper.readValue(filterData, ComponentListReplaceFilterForm.class);
        TabrIn input = new TabrIn(request);
        input.setSize(20000);
        var data = componentService.queryDataByFilterForm(input, form, ListReplaceListItem.class).getData();
        data.forEach(item -> {
            if (item.getSubComponentId() != null) {
                item.mark = COMPONENT_SUBSTITUTE_MARK;
            } else if (!item.processed) {
                item.mark = COMPONENT_NOT_PROCESSED_MARK;
            }
            item.substituteComponent = item.getSubComponentId() == null ?
                null : item.subComponentPosition == null ? StringUtils.EMPTY : StringUtils.leftPad(String.valueOf(item.subComponentPosition), 6, '0') + DATA_DELIMITER + item.subComponentName;
            item.position = item.positionNum == null ? StringUtils.EMPTY : StringUtils.leftPad(String.valueOf(item.positionNum), 6, '0');
        });
        var wb = new XSSFWorkbook();
        var sheet = wb.createSheet();
        var mainRow = sheet.createRow(0);
        mainRow.createCell(0).setCellValue("Состояние");
        mainRow.createCell(1).setCellValue("Позиция");
        mainRow.createCell(2).setCellValue("Наименование");
        mainRow.createCell(3).setCellValue("Заместитель");
        mainRow.createCell(4).setCellValue("Производитель");
        mainRow.createCell(5).setCellValue("Категория");
        mainRow.createCell(6).setCellValue("Описание");
        for (int i = 0; i < data.size(); i++) {
            var item = data.get(i);
            var row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(item.mark);
            row.createCell(1).setCellValue(item.position);
            row.createCell(2).setCellValue(item.name);
            row.createCell(3).setCellValue(item.substituteComponent);
            row.createCell(4).setCellValue(item.producer);
            row.createCell(5).setCellValue(item.category);
            row.createCell(6).setCellValue(item.description);
        }
        KtCommonUtil.INSTANCE.attachDocumentXLSX(response, wb, "Список компонентов замен");
    }

    /**
     * Генерация технических характеристик для компонентов
     * @param component компонент
     * @return технические характеристики
     */
    private String generateTechCharacteristics(Component component) {
        if (component == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        ComponentCategory componentCategory = component.getCategory();
        List<ComponentCategory> categoryList = new ArrayList<>();
        while (componentCategory != null) {
            categoryList.add(componentCategory);
            componentCategory = componentCategory.getParent();
        }
        categoryList.sort(Comparator.comparingInt(ComponentCategory::level));
        categoryList.stream().flatMap(c -> c.getComponentAttributeList().stream()).forEach(attribute -> {
            List<ComponentAttributePreference> preferenceList = attribute.getComponentAttributePreferenceList();
            preferenceList.sort(Comparator.comparingInt(ComponentAttributePreference::getSortIndex));
            ComponentAttributePreference techCharPreference = preferenceList.stream()
                .filter(pref -> ComponentAttributePreferenceType.TECH_CHAR_INCLUDE.equals(pref.getType()))
                .findFirst().orElse(null);
            if (techCharPreference != null && BooleanUtils.toBooleanDefaultIfNull(techCharPreference.getBoolValue(), Boolean.FALSE)) {
                List<ComponentAttributeValue> valueList = component.getComponentAttributeValueList();
                valueList.stream().filter(value -> Objects.equals(value.getAttribute(), attribute)).forEach(value -> {
                    switch (value.getAttribute().getType()) {
                        case INPUT:
                            String stringValue = value.getStringValue();
                            if (StringUtils.isNotBlank(stringValue)) {
                                stringBuilder.append(stringValue.trim()).append(TECH_CHAR_SEPARATOR);
                            }
                            break;
                        case CHECKBOX:
                            if (BooleanUtils.toBooleanDefaultIfNull(value.getBoolValue(), Boolean.FALSE)) {
                                stringBuilder.append(attribute.getName()).append(TECH_CHAR_SEPARATOR);
                            }
                            break;
                        case SELECT:
                            ComponentAttributePreference postfixPreference = preferenceList.stream()
                                .filter(pref -> ComponentAttributePreferenceType.SELECT_POSTFIX.equals(pref.getType()))
                                .findFirst().orElse(null);
                            String postfix = postfixPreference == null ?
                                StringUtils.EMPTY : StringUtils.defaultIfBlank(postfixPreference.getStringValue(), StringUtils.EMPTY);
                            preferenceList.stream().filter(pref -> ComponentAttributePreferenceType.SELECT_OPTION.equals(pref.getType())).forEach(pref -> {
                                if (Objects.equals(pref.getId(), value.getLongValue()) && StringUtils.isNotBlank(pref.getStringValue())) {
                                    stringBuilder
                                        .append(pref.getStringValue().trim())
                                        .append(postfix)
                                        .append(TECH_CHAR_SEPARATOR);
                                }
                            });
                            break;
                    }
                });
            }
        });
        // Обрезаем сепаратор в конце характеристик
        if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - TECH_CHAR_SEPARATOR.length(), stringBuilder.length());
        }
        return stringBuilder.toString();
    }

    @GetMapping("/list/comment/load")
    public TabrOut<?> list_comment_load(
        HttpServletRequest request,
        long componentId,
        String filterData
    ) {
        @Getter @Setter @AllArgsConstructor @NoArgsConstructor
        class Item {
            Long id;
            LocalDateTime createDate;
            String createdBy;
            String comment;
        }
        var form = KtCommonUtil.INSTANCE.readDynamic(jsonMapper, filterData);
        TabrIn input = new TabrIn(request);
        return TabrOut.Companion.instance(input, componentCommentService.findTableData(input, form, componentId), item -> new Item(
            item.getId(),
            item.getCreateDatetime(),
            item.getUser().getUserOfficialName(),
            item.getComment()
        ));
    }

    // Сохранение комментария
    @PostMapping("/list/comment/edit/save")
    public ValidatorResponse list_comment_edit_save(
        HttpSession session,
        @RequestPart DynamicObject form
    ) {
        var user = KtCommonUtil.INSTANCE.getUser(session);
        var response = new ValidatorResponse();
        var errors = new ValidatorErrors(response);

        Long formId = form.longValue(ObjAttr.ID);
        Long componentId = form.longValue(ObjAttr.COMPONENT_ID);
        String comment = form.string(ObjAttr.COMMENT);
        if (StringUtils.isBlank(comment)) errors.putError(ObjAttr.COMMENT, ValidatorMsg.RANGE_LENGTH, 1, 1024);

        if (response.isValid()) baseService.exec(em -> {
            var compComment = formId == null ? null : componentCommentService.read(formId);
            if (formId != null && compComment == null) throw new AlertUIException("Комментарий не найден");
            compComment = compComment == null ? new ComponentComment() : compComment;
            if (formId == null) {
                compComment.setComponent(new Component(componentId));
                compComment.setUser(user);
                compComment.setCreateDatetime(LocalDateTime.now());
            }
            compComment.setComment(comment);
            componentCommentService.save(compComment);
            if (formId == null) response.putAttribute(ObjAttr.ID, compComment.getId());
            return Unit.INSTANCE;
        });
        return response;
    }

    @DeleteMapping("/list/comment/delete/{id}")
    public void list_comment_delete(@PathVariable Long id) {
        componentCommentService.deleteById(id);
    }
}