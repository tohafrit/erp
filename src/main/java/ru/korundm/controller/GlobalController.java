package ru.korundm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.korundm.constant.RequestPath;
import ru.korundm.dao.MenuItemService;
import ru.korundm.dao.UserService;
import ru.korundm.dto.GlobalDataModel;
import ru.korundm.dto.MenuStructure;
import ru.korundm.entity.MenuItem;
import ru.korundm.entity.User;
import ru.korundm.enumeration.MenuItemType;
import ru.korundm.helper.LoggedUser;
import ru.korundm.util.KtCommonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static ru.korundm.constant.BaseConstant.MODEL_GLOBAL_DATA_ATTRIBUTE_NAME;
import static ru.korundm.constant.BaseConstant.MODEL_SESSION_USER_ATTRIBUTE;

/**
 * Контроллер глобальной установки пользовательских параметров
 * @author pakhunov_an
 * Date:   20.04.2018
 */
@ControllerAdvice
public class GlobalController {

    private final UserService userService;
    private final MenuItemService menuItemService;
    private final ObjectMapper jsonMapper;

    public GlobalController(
        UserService userService,
        MenuItemService menuItemService,
        ObjectMapper jsonMapper
    ) {
        this.userService = userService;
        this.menuItemService = menuItemService;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Установка глобальной модели
     * @param model             инкапсуляция данных
     * @param request           содержимое запроса
     * @param session           сессия
     * @param redirectAttribute инкапсуляция данных
     */
    @ModelAttribute
    public void setup(
        ModelMap model,
        HttpServletRequest request,
        HttpSession session,
        RedirectAttributes redirectAttribute // Не удалять
    ) throws JsonProcessingException {
        if (LoggedUser.get() != null) {
            User user = userService.findByUserName(LoggedUser.get());
            if (user != null) {
                session.setAttribute(MODEL_SESSION_USER_ATTRIBUTE, user);
                model.addAttribute(MODEL_SESSION_USER_ATTRIBUTE, user);
                if (KtCommonUtil.INSTANCE.isAjax(request)) {
                    return;
                }
                String URI = request.getRequestURI();
                List<Object> leftMenuStructureList = new ArrayList<>();
                List<Object> topMenuStructureList = new ArrayList<>();
                var globalDataModel = new GlobalDataModel();
                if (URI.startsWith(RequestPath.CORP)) {
                    buildMenuStructure(globalDataModel, URI, leftMenuStructureList, menuItemService.getAllByParentNullAndType(MenuItemType.CORP), RequestPath.CORP);
                    buildMenuStructure(globalDataModel, URI, topMenuStructureList, menuItemService.getAllByParentNullAndType(MenuItemType.CORP_HEADER), null);
                } else if (URI.startsWith(RequestPath.PROD)) {
                    buildMenuStructure(globalDataModel, URI, leftMenuStructureList, menuItemService.getAllByParentNullAndType(MenuItemType.PROD), RequestPath.PROD);
                    buildMenuStructure(globalDataModel, URI, topMenuStructureList, menuItemService.getAllByParentNullAndType(MenuItemType.PROD_HEADER), null);
                }
                globalDataModel.setLeftMenu(jsonMapper.writeValueAsString(leftMenuStructureList));
                globalDataModel.setTopMenuList(topMenuStructureList);
                session.setAttribute(MODEL_GLOBAL_DATA_ATTRIBUTE_NAME, globalDataModel);
            }
        }
    }

    // Метод рекурсивного построения элементов структуры состава меню
    private void buildMenuStructure(GlobalDataModel model, String URI, List<Object> structureList, List<MenuItem> menuItemList, String prefix) {
        menuItemList.forEach(item -> {
            MenuStructure structureItem = new MenuStructure();
            structureItem.setId(item.getId());
            structureItem.setName(item.getName());
            StringBuilder sb = new StringBuilder();
            String rawHref = item.getHref();
            if (StringUtils.isNotEmpty(rawHref)) {
                if (prefix != null) sb.append(prefix);
                sb.append(rawHref);
            }
            String href = sb.toString();
            structureItem.setHref(href);
            structureItem.setIcon(item.getIcon());
            int slashIdx = StringUtils.ordinalIndexOf(URI, "/", 3);
            String hrefURI = slashIdx != -1 ? URI.substring(0, slashIdx) : URI;
            if (href.equals(hrefURI)) model.setTitle(item.getName());
            structureItem.setSelected(href.equals(hrefURI));
            List<MenuItem> childList = item.getChildList();
            if (!childList.isEmpty()) {
                List<Object> subStructureList = new ArrayList<>();
                buildMenuStructure(model, URI, subStructureList, childList, prefix);
                if (!subStructureList.isEmpty()) {
                    structureItem.setChildList(subStructureList);
                }
            }
            structureList.add(structureItem);
        });
    }
}