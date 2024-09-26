package ru.korundm.controller.action.prod;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.korundm.annotation.ActionController;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.ComponentAttributeService;
import ru.korundm.dao.ComponentCategoryService;
import ru.korundm.dto.DropdownOption;
import ru.korundm.dto.component.category.ComponentCategoryTreeItem;
import ru.korundm.entity.ComponentAttribute;
import ru.korundm.entity.ComponentAttributePreference;
import ru.korundm.entity.ComponentCategory;
import ru.korundm.enumeration.ComponentAttributePreferenceType;
import ru.korundm.enumeration.ComponentAttributeType;
import ru.korundm.form.edit.EditComponentAttributeForm;
import ru.korundm.form.edit.EditComponentCategoryForm;
import ru.korundm.helper.TabrIn;
import ru.korundm.helper.ValidatorResponse;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static ru.korundm.util.FormValidatorUtil.assertFormId;

@ActionController(RequestPath.Action.Prod.COMPONENT_CATEGORY)
public class ComponentCategoryActionProdController {

    @Resource
    private MessageSource messageSource;

    // Список одиночных настроек атрибута, установка выполняется по одинаковому алгоритму
    private static final Map<ComponentAttributePreferenceType, List<ComponentAttributeType>> singlePreferenceMap;
    static {
        Map<ComponentAttributePreferenceType, List<ComponentAttributeType>> map = new HashMap<>();
        map.put(ComponentAttributePreferenceType.REQUIRED, List.of(ComponentAttributeType.SELECT));
        map.put(ComponentAttributePreferenceType.DISABLED, List.of(ComponentAttributeType.SELECT, ComponentAttributeType.CHECKBOX, ComponentAttributeType.INPUT));
        map.put(ComponentAttributePreferenceType.MULTIPLE, List.of(ComponentAttributeType.SELECT));
        map.put(ComponentAttributePreferenceType.TECH_CHAR_INCLUDE, List.of(ComponentAttributeType.SELECT, ComponentAttributeType.CHECKBOX, ComponentAttributeType.INPUT));
        map.put(ComponentAttributePreferenceType.INPUT_MIN_LENGTH, List.of(ComponentAttributeType.INPUT));
        map.put(ComponentAttributePreferenceType.INPUT_MAX_LENGTH, List.of(ComponentAttributeType.INPUT));
        map.put(ComponentAttributePreferenceType.SELECT_POSTFIX, List.of(ComponentAttributeType.SELECT));
        singlePreferenceMap = Map.copyOf(map);
    }

    private final ComponentCategoryService componentCategoryService;
    private final ComponentAttributeService componentAttributeService;

    public ComponentCategoryActionProdController(
        ComponentCategoryService componentCategoryService,
        ComponentAttributeService componentAttributeService
    ) {
        this.componentCategoryService = componentCategoryService;
        this.componentAttributeService = componentAttributeService;
    }

    // Загрузка списка категорий
    @GetMapping("/list/load")
    public List<ComponentCategoryTreeItem> list_load() {
        return recursiveComponentCategory(componentCategoryService.getAllByParentIsNull());
    }

    /**
     * Метод для формирования списка категорий компонентов с вложенными пунктами
     * @param componentCategoryList список родительский документаций
     * @return полный список категорий компонентов с вложениями
     */
    private List<ComponentCategoryTreeItem> recursiveComponentCategory(List<ComponentCategory> componentCategoryList) {
        List<ComponentCategoryTreeItem> childrenList = new ArrayList<>();
        for (var componentCategory : componentCategoryList) {
            ComponentCategoryTreeItem children = new ComponentCategoryTreeItem();
            children.setId(componentCategory.getId());
            children.setName(componentCategory.getName());
            children.setDescription(componentCategory.getDescription());
            if (!componentCategory.getChildList().isEmpty()) {
                children.setChildrenList(recursiveComponentCategory(componentCategory.getChildList()));
            }
            childrenList.add(children);
        }
        return childrenList;
    }

    // Сохранение элемента в списке категорий
    @PostMapping("/list/edit/save")
    public ValidatorResponse list_edit_save(
        EditComponentCategoryForm form
    ) {
        ComponentCategory componentCategory = form.getId() != null ? componentCategoryService.read(form.getId()) : new ComponentCategory();
        form.setNotParentAllowedList(componentCategoryService.getAllSiblingsIdByParentId(componentCategory.getId()));
        ValidatorResponse response = new ValidatorResponse(form);
        if (response.isValid()) {
            componentCategory.setName(form.getName().trim());
            componentCategory.setParent(assertFormId(form.getParent()));
            componentCategory.setUnit(true);
            componentCategory.setDescription(StringUtils.defaultIfBlank(form.getDescription(), null));
            componentCategoryService.save(componentCategory);
        }
        return response;
    }

    // Удаление элемента из списка категорий
    @DeleteMapping("/list/delete/{id}")
    public void list_delete(@PathVariable Long id) {
        componentCategoryService.deleteById(id);
    }

    // Загрузка списка атрибутов категорий
    @GetMapping("/attribute/load")
    public List<?> componentCategory_attribute_ops_ListLoad(
        HttpServletRequest request,
        Long categoryId
    ) {
        @Getter
        class ItemOut {
            long id;
            String name; // наименование
            String type; // тип
            String description; // описание
        }
        List<ItemOut> itemOutList = componentAttributeService.getAllByCategoryId(categoryId).stream().map(attribute -> {
            ItemOut itemOut = new ItemOut();
            itemOut.id = attribute.getId();
            itemOut.type = messageSource.getMessage(attribute.getType().getNameProperty(), null, request.getLocale());
            itemOut.name = attribute.getName();
            itemOut.description = attribute.getDescription();
            return itemOut;
        }).collect(Collectors.toList());

        TabrIn input = new TabrIn(request);
        if (CollectionUtils.isNotEmpty(itemOutList)) {
            Comparator<ItemOut> comparator = Comparator.comparing(ItemOut::getType);
            if (CollectionUtils.isNotEmpty(input.getSorters())) {
                boolean isAsc = ASC.equals(input.getSorters().get(0).getDir());
                Comparator<String> orderStringComparator = Comparator.nullsLast(isAsc ? Comparator.<String>naturalOrder() : Comparator.<String>reverseOrder());
                switch (input.getSorters().get(0).getField()) {
                    case "name":
                        comparator = comparator.thenComparing(ItemOut::getName, orderStringComparator);
                        break;
                    case "description":
                        comparator = comparator.thenComparing(ItemOut::getDescription, orderStringComparator);
                        break;
                    default:
                        comparator = comparator.thenComparing(ItemOut::getId);
                }
            }
            itemOutList.sort(comparator);
        }
        return itemOutList;
    }

    // Сохранение атрибута
    @PostMapping("/attribute/save")
    public ValidatorResponse attribute_save(EditComponentAttributeForm form) {
        ValidatorResponse response = new ValidatorResponse(form);
        Long id = form.getId();
        Long categoryId = form.getCategory() == null ? null : form.getCategory().getId();
        String name = form.getName();
        // Выполняем проверку на уникальность по имени
        if (categoryId != null && name != null) {
            if (
                (id != null && componentAttributeService.existsByIdNotAndCategoryIdAndName(id, categoryId, name))
                || (id == null && componentAttributeService.existsByCategoryIdAndName(categoryId, name))
            ) {
                response.putError("name", "validator.editComponentAttributeForm.existByName");
            }
        }
        if (response.isValid()) {
            ComponentAttribute attribute = id == null ? new ComponentAttribute() : componentAttributeService.read(id);
            attribute.setType(form.getType());
            attribute.setCategory(form.getCategory());
            attribute.setName(form.getName());
            attribute.setDescription(form.getDescription());
            // Устанавливаем настройки по одиночным атрибутам
            ComponentAttributeType attributeType = form.getType();
            List<ComponentAttributePreference> preferenceList = attribute.getComponentAttributePreferenceList();
            // Обрабатываем одиночные атрибуты
            singlePreferenceMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(attributeType))
                .forEach(entry -> {
                    ComponentAttributePreferenceType preferenceType = entry.getKey();
                    ComponentAttributePreference preference = preferenceList
                        .stream().filter(pref -> Objects.equals(pref.getType(), preferenceType))
                        .findFirst().orElse(null);
                    if (preference == null) {
                        preference = new ComponentAttributePreference();
                        preference.setType(preferenceType);
                        preference.setAttribute(attribute);
                        preferenceList.add(preference);
                    }
                    switch (preferenceType) {
                        case REQUIRED:
                            preference.setBoolValue(form.isRequired());
                            break;
                        case DISABLED:
                            preference.setBoolValue(form.isDisabled());
                            break;
                        case MULTIPLE:
                            preference.setBoolValue(form.isMultiple());
                            break;
                        case TECH_CHAR_INCLUDE:
                            preference.setBoolValue(form.isTechCharInclude());
                            break;
                        case INPUT_MIN_LENGTH:
                            preference.setIntValue(form.getInputMinLength());
                            break;
                        case INPUT_MAX_LENGTH:
                            preference.setIntValue(form.getInputMaxLength());
                            break;
                        case SELECT_POSTFIX:
                            preference.setStringValue(form.getSelectPostfix().trim());
                    }
                });
            // Для выпадающих списков по каждому пункту создается настройка
            if (Objects.equals(attributeType, ComponentAttributeType.SELECT)) {
                List<ComponentAttributePreference> optionPreferenceList = preferenceList.stream()
                    .filter(pref -> Objects.equals(pref.getType(), ComponentAttributePreferenceType.SELECT_OPTION)).collect(Collectors.toList());
                preferenceList.removeAll(optionPreferenceList);
                //
                List<DropdownOption> selectOptionList = form.getSelectOptionList();
                for (int i = 0; i < selectOptionList.size(); i++) {
                    DropdownOption formOption = selectOptionList.get(i);
                    long formOptionId = formOption.getId();
                    String formOptionValue = formOption.getValue();
                    int sortIndex = i;
                    if (formOptionId == 0) { // Вставляем новый пункт списка
                        ComponentAttributePreference preference = new ComponentAttributePreference();
                        preference.setType(ComponentAttributePreferenceType.SELECT_OPTION);
                        preference.setAttribute(attribute);
                        preference.setSortIndex(sortIndex);
                        preference.setStringValue(formOptionValue);
                        preferenceList.add(preference);
                    } else { // Обновляем пункт списка
                        optionPreferenceList.forEach(prefOption -> {
                            if (Objects.equals(formOptionId, prefOption.getId())) {
                                prefOption.setSortIndex(sortIndex);
                                prefOption.setStringValue(formOptionValue);
                                preferenceList.add(prefOption);
                            }
                        });
                    }
                }
            }
            componentAttributeService.save(attribute);
        }
        return response;
    }

    // Удаление элемента из списка атрибутов
    @DeleteMapping("/attribute/delete/{id}")
    public void attribute_delete(@PathVariable Long id) {
        componentAttributeService.deleteById(id);
    }
}