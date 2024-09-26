package ru.korundm.controller.view.prod;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import ru.korundm.annotation.ViewController;
import ru.korundm.dao.ComponentAttributeService;
import ru.korundm.dao.ComponentCategoryService;
import ru.korundm.dto.DropdownOption;
import ru.korundm.entity.ComponentAttribute;
import ru.korundm.entity.ComponentAttributePreference;
import ru.korundm.entity.ComponentCategory;
import ru.korundm.enumeration.ComponentAttributeType;
import ru.korundm.form.edit.EditComponentAttributeForm;
import ru.korundm.form.edit.EditComponentCategoryForm;
import ru.korundm.constant.RequestPath;
import ru.korundm.exception.AlertUIException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ViewController(RequestPath.View.Prod.COMPONENT_CATEGORY)
public class ComponentCategoryViewProdController {

    private final ComponentCategoryService componentCategoryService;
    private final ComponentAttributeService componentAttributeService;

    public ComponentCategoryViewProdController(
        ComponentCategoryService componentCategoryService,
        ComponentAttributeService componentAttributeService
    ) {
        this.componentCategoryService = componentCategoryService;
        this.componentAttributeService = componentAttributeService;
    }

    @GetMapping("/list")
    public String list() {
        return "prod/include/component-category/list";
    }

    // Редактирование элемента в списке категорий
    @GetMapping("/list/edit")
    public String list_edit(ModelMap model, Long id) {
        EditComponentCategoryForm form = new EditComponentCategoryForm();
        if (id != null) {
            ComponentCategory componentCategory = componentCategoryService.read(id);
            form.setId(id);
            form.setName(componentCategory.getName());
            form.setParent(componentCategory.getParent());
            form.setDescription(componentCategory.getDescription());
        }
        model.addAttribute("form", form);
        model.addAttribute("componentCategoryList", componentCategoryService.getAllByParentIsNull());
        return "prod/include/component-category/list/edit";
    }

    // Получение списка атрибутов для категорий
    @GetMapping("/attribute")
    public String attribute(ModelMap model, Long categoryId) {
        ComponentCategory category = componentCategoryService.read(categoryId);
        if (category == null) {
            throw new AlertUIException("Категория была удалена");
        }
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categoryName", category.getName());
        return "prod/include/component-category/attribute";
    }

    // Добавление/редактирование атрибута
    @GetMapping("/attribute/edit")
    public String attribute_edit(
        ModelMap model,
        Long id,
        Long categoryId,
        ComponentAttributeType type
    ) {
        EditComponentAttributeForm form = new EditComponentAttributeForm();
        if (id == null) {
            ComponentCategory category = new ComponentCategory();
            category.setId(categoryId);
            form.setType(type);
            form.setCategory(category);
        } else {
            ComponentAttribute attribute = componentAttributeService.read(id);
            // Общие параметры
            form.setId(attribute.getId());
            form.setType(attribute.getType());
            form.setCategory(attribute.getCategory());
            form.setName(attribute.getName());
            form.setDescription(attribute.getDescription());
            // Динамические параметры
            List<DropdownOption> selectOptionList = new ArrayList<>();
            List<ComponentAttributePreference> preferenceList = attribute.getComponentAttributePreferenceList();
            preferenceList.sort(Comparator.comparingInt(ComponentAttributePreference::getSortIndex));
            preferenceList.forEach(pref -> {
                switch (pref.getType()) {
                    case REQUIRED:
                        form.setRequired(BooleanUtils.toBooleanDefaultIfNull(pref.getBoolValue(), Boolean.FALSE));
                        break;
                    case DISABLED:
                        form.setDisabled(BooleanUtils.toBooleanDefaultIfNull(pref.getBoolValue(), Boolean.FALSE));
                        break;
                    case MULTIPLE:
                        form.setMultiple(BooleanUtils.toBooleanDefaultIfNull(pref.getBoolValue(), Boolean.FALSE));
                        break;
                    case TECH_CHAR_INCLUDE:
                        form.setTechCharInclude(BooleanUtils.toBooleanDefaultIfNull(pref.getBoolValue(), Boolean.TRUE));
                        break;
                    case INPUT_MIN_LENGTH:
                        form.setInputMinLength(pref.getIntValue());
                        break;
                    case INPUT_MAX_LENGTH:
                        form.setInputMaxLength(pref.getIntValue());
                        break;
                    case SELECT_OPTION:
                        selectOptionList.add(new DropdownOption(pref.getId(), pref.getStringValue(), false));
                        break;
                    case SELECT_POSTFIX:
                        form.setSelectPostfix(pref.getStringValue());
                        break;
                }
            });
            form.setSelectOptionList(selectOptionList);
        }
        model.addAttribute("form", form);
        return "prod/include/component-category/attribute/edit";
    }
}