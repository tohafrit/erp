<div class="ui modal">
    <div class="header">
        <fmt:message key="documentation.list.header.${empty form.id ? 'add' : 'edit'}"/>
    </div>
    <div class="scrolling content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label><fmt:message key="documentation.field.name"/></label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label><fmt:message key="documentation.field.parent"/></label>
                <form:select cssClass="std-tree-select" path="parent.id" data-chosen-options='{"containerWidth":"400"}'>
                    <std:treeChosen hierarchyList="${documentationList}" selectedItems="${form.parent.id}"/>
                </form:select>
                <div class="ui compact message error" data-field="parent"></div>
            </div>
            <div class="field">
                <label><fmt:message key="documentation.field.menuItem"/></label>
                <form:select cssClass="std-tree-select" path="menuItem.id" data-chosen-options='{"containerWidth":"400"}'>
                    <std:treeChosen hierarchyList="${menuItemList}" selectedItems="${form.menuItem.id}"/>
                </form:select>
            </div>
            <div class="field required">
                <label><fmt:message key="documentation.field.content"/></label>
                <form:textarea path="content" cssClass="std-ckeditor" rows="10"/>
                <div class="ui compact message error" data-field="content"></div>
            </div>
            <div class="field">
                <label><fmt:message key="documentation.field.seeAlso"/></label>
                <form:select cssClass="std-tree-select" path="seeAlsoIdList" data-chosen-options='{"multiple": true}'>
                    <std:treeChosen hierarchyList="${documentationList}" selectedItems="${form.seeAlsoIdList}"/>
                </form:select>
            </div>
        </form:form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>