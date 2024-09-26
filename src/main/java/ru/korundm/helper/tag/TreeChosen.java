package ru.korundm.helper.tag;

import lombok.Setter;
import lombok.extern.jbosslog.JBossLog;
import ru.korundm.util.HibernateUtil;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JBossLog
public class TreeChosen extends TagSupport {

    @Setter
    private List hierarchyList;

    @Setter
    private Object selectedItems;

    @Override
    public int doStartTag() {
        try {
            JspWriter out = pageContext.getOut();
            StringBuilder stringBuilder = new StringBuilder();
            // Идентификаторы выбранных опций
            List<Long> selectedIdList = new ArrayList<>();
            List selectedList = selectedItems instanceof List ? (List) selectedItems : Collections.singletonList(selectedItems);
            for (Object item : selectedList) {
                if (item instanceof Long) {
                    selectedIdList.add((Long) item);
                } else if (selectedItems != null) {
                    selectedIdList.add((Long) item.getClass().getMethod("getId").invoke(item));
                }
            }
            // Список уже добавленных id
            List<String> existsIdList = new ArrayList<>();
            recursiveFill(stringBuilder, hierarchyList, selectedIdList, existsIdList);
            out.print(stringBuilder.toString());
        } catch (IOException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("TreeChosen error: " + e.getMessage());
        }
        return SKIP_BODY;
    }

    private void recursiveFill(StringBuilder stringBuilder, List source, List<Long> selectedIdList, List<String> existsIdList) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        for (Object sourceObject : source) {
            String id = "", name = "", parentId = "";
            List siblings = null;
            sourceObject = HibernateUtil.initializeAndUnproxy(sourceObject);
            String simpleClassName = sourceObject.getClass().getSimpleName();
            for (Method method : sourceObject.getClass().getMethods()) {
                if (method.getName().equals("getId")) {
                    id = ((Long) method.invoke(sourceObject)).toString();
                } else if (method.getName().equals("getName")) {
                    name = (String) method.invoke(sourceObject);
                } else if (method.getName().equals("getParent" + simpleClassName) || method.getName().equals("getParent")) {
                    Object methodObject = method.invoke(sourceObject);
                    if (methodObject != null) {
                        parentId = ((Long) methodObject.getClass().getMethod("getId").invoke(methodObject)).toString();
                    }
                } else if (method.getName().equals("getChild" + simpleClassName + "List") || method.getName().equals("getChildList")) {
                    Object methodObject = method.invoke(sourceObject);
                    if (methodObject != null) {
                        siblings = (List) methodObject;
                    }
                }
            }
            StringBuilder siblingsString = new StringBuilder();
            if (siblings != null) {
                for (Object sibling : siblings) {
                    siblingsString.append(((Long) sibling.getClass().getMethod("getId").invoke(sibling)).toString()).append(",");
                }
                if (siblingsString.toString().contains(",")) {
                    siblingsString = new StringBuilder(siblingsString.substring(0, siblingsString.lastIndexOf(",")));
                }
            }
            if (!existsIdList.contains(id)) {
                existsIdList.add(id);
                final Long idL = Long.valueOf(id);
                stringBuilder.append("<option value=\"").append(id).append("\" ");
                stringBuilder.append("data-chosen-parent=\"").append(parentId).append("\" ");
                stringBuilder.append("data-chosen-childrens=\"").append(siblingsString).append("\" ");
                stringBuilder.append("data-chosen-name=\"").append(name).append("\" ");
                stringBuilder.append((selectedIdList.stream().anyMatch(selectedId -> selectedId != null && selectedId.equals(idL)) ? "class=\"load-selected\"" : ""));
                stringBuilder.append("></option>");
                if (siblings != null) {
                    recursiveFill(stringBuilder, siblings, selectedIdList, existsIdList);
                }
            }
        }
    }
}