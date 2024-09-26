<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление категории' : 'Редактирование категории'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Наименование</label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label>Категория-родитель</label>
                <form:select cssClass="std-tree-select" path="parent.id">
                    <std:treeChosen hierarchyList="${componentCategoryList}" selectedItems="${form.parent.id}"/>
                </form:select>
                <div class="ui compact message error" data-field="parent"></div>
            </div>
            <div class="field">
                <label>Описание</label>
                <div class="ui textarea">
                    <form:textarea path="description" rows="3"/>
                </div>
                <div class="ui compact message error" data-field="description"></div>
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