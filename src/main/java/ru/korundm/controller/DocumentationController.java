package ru.korundm.controller;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.dao.DocumentationService;
import ru.korundm.dao.MenuItemService;
import ru.korundm.dto.documentation.DocumentationSearchResult;
import ru.korundm.dto.documentation.DocumentationWithChildren;
import ru.korundm.entity.Documentation;
import ru.korundm.entity.MenuItem;
import ru.korundm.form.edit.EditDocumentationForm;
import ru.korundm.helper.ValidatorResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.korundm.util.FormValidatorUtil.assertFormId;
import static ru.korundm.util.FormValidatorUtil.formIdValid;

/**
 * @deprecated подлежит переработке на SPA с page.js
 */
@Controller
@Deprecated
public class DocumentationController {

    private final DocumentationService documentationService;
    private final MenuItemService menuItemService;

    public DocumentationController(
        DocumentationService documentationService,
        MenuItemService menuItemService
    ) {
        this.documentationService = documentationService;
        this.menuItemService = menuItemService;
    }

    @GetMapping("/admin/documentation")
    public String admin_documentation() {
        return "doc/documentation";
    }

    @GetMapping(
        value = "/admin/documentation/list",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public List<DocumentationWithChildren> documentation_list() {
        return recursiveDocumentation(documentationService.getParentDocumentationList());
    }

    @GetMapping("/admin/documentation/list/edit")
    public String documentation_list_edit(
        ModelMap model,
        Long id
    ) {
        EditDocumentationForm form = new EditDocumentationForm();
        if (id != null) {
            Documentation documentation = documentationService.read(id);
            form.setId(id);
            form.setName(documentation.getName());
            form.setParent(documentation.getParent());
            form.setMenuItem(documentation.getMenuItem());
            form.setContent(documentation.getContent());
            form.setSeeAlsoIdList(documentation.getSeeAlsoList().stream().map(Documentation::getId).collect(Collectors.toList()));
        }
        model.addAttribute("form", form);
        model.addAttribute("documentationList", documentationService.getParentDocumentationList());
        model.addAttribute("allDocumentation", documentationService.getAll());
        model.addAttribute("menuItemList", menuItemService.getAll());
        return "doc/documentation/list/edit";
    }

    @PostMapping(
        value = "/admin/documentation/list/ops/save",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public ValidatorResponse documentation_list_ops_save(
        EditDocumentationForm form
    ) {
        ValidatorResponse response = new ValidatorResponse();
        Documentation documentation = form.getId() != null ? documentationService.read(form.getId()) : new Documentation();
        Documentation exist = documentationService.getByName(form.getName().trim());
        if (exist != null && !Objects.equals(exist.getId(), form.getId())) {
            response.putError("name", ValidatorMsg.UNIQUE);
        }
        form.setNotParentAllowedList(documentationService.getAllSiblingsIdByParentId(form.getId()));
        if (formIdValid(form.getMenuItem())) {
            MenuItem menuItem = documentation.getMenuItem();
            if (menuItem == null || !Objects.equals(menuItem, form.getMenuItem())) {
                form.setNotMenuItemAllowedList(menuItemService.getAllAlreadyUsedMenuItem(form.getMenuItem()));
            }
        }
        response.fill(form);
        if (response.isValid()) {
            documentation.setName(form.getName());
            documentation.setParent(assertFormId(form.getParent()));
            documentation.setMenuItem(assertFormId(form.getMenuItem()));
            documentation.setContent(form.getContent());
            documentation.setSeeAlsoList(documentationService.getAllById(form.getSeeAlsoIdList()));
            documentationService.save(documentation);
        }
        return response;
    }

    @DeleteMapping("/admin/documentation/list/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void documentation_list_delete(@PathVariable Long id) {
        documentationService.deleteById(id);
    }

    /**
     * Метод для формирования списка документации с вложенными пунктами
     * @param documentationList список родительский документаций
     * @return полный список документации с вложениями
     */
    private List<DocumentationWithChildren> recursiveDocumentation(List<Documentation> documentationList) {
        List<DocumentationWithChildren> childrenList = new ArrayList<>();
        for (var documentation : documentationList) {
            DocumentationWithChildren children = new DocumentationWithChildren();
            children.setContent(documentation.getContent());
            children.setId(documentation.getId());
            children.setName(documentation.getName());
            children.setMenuName(documentation.getMenuItem() != null ? documentation.getMenuItem().getName() : "");
            if (!documentation.getChildList().isEmpty()) {
                children.setChildrenList(recursiveDocumentation(documentation.getChildList()));
            }
            childrenList.add(children);
        }
        return childrenList;
    }

    @GetMapping({"/documentation", "/documentation/{strId}"})
    public String documentation_id(
        ModelMap model,
        @PathVariable(required = false) String strId
    ) {
        if (strId != null) {
            long id = NumberUtils.isDigits(strId) ? Long.parseLong(strId) : 0;
            if (!documentationService.existsById(id)) {
                return "redirect:/notFound";
            }
            List<Documentation> documentationList = documentationService.recursiveGetAll(documentationService.getParentDocumentationList());
            Documentation previous = documentationService.getPrevious(documentationList, id);
            Documentation next = documentationService.getNext(documentationList, id);
            model.addAttribute("breadcrumbsList", documentationService.getListForBreadcrumbs(id));
            model.addAttribute("documentation", documentationService.read(id));
            model.addAttribute("prevDocId", previous == null ? null : previous.getId());
            model.addAttribute("nextDocId", next == null ? null : next.getId());
        }
        return "doc/content";
    }

    @GetMapping("/documentation/search")
    public String documentation_search(
        ModelMap model,
        @RequestParam(required = false) String text
    ) throws IOException, InvalidTokenOffsetsException {
        List<DocumentationSearchResult> resultList = new ArrayList<>();
        if (text != null) {
            resultList = documentationService.getByContent(text);
        }
        model.addAttribute("resultList", resultList);
        model.addAttribute("searchText", text);
        return "doc/search";
    }

    @GetMapping("/documentation/navigation")
    public String documentation_navigation() {
        return "doc/documentation/navigation";
    }
}