<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление метки шаблона' : 'Редактирование метки шаблона'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Метка</label>
                <form:input path="label"/>
                <div class="ui compact message error" data-field="label"></div>
            </div>
            <div class="field required">
                <label>должность сотрудника в документе </label>
                <form:input path="employeePosition"/>
                <div class="ui compact message error" data-field="employeePosition"></div>
            </div>
            <div class="field required">
                <label>Сотрудник</label>
                <form:select cssClass="ui dropdown label search std-select" path="employee.id">
                    <c:forEach items="${userList}" var="user">
                        <form:option value="${user.id}">${user.value}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="employee"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <form:textarea path="comment" rows="3"/>
                </div>
                <div class="ui compact message error" data-field="comment"></div>
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