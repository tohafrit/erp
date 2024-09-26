<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление материала' : 'Редактирование материала'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Наименование</label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field required">
                <label>Операции</label>
                <form:select cssClass="ui search dropdown std-select" path="workTypeList" multiple="true">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${workTypeList}" var="workType">
                        <form:option value="${workType.id}">${workType.name}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="workTypeList"></div>
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