<div class="ui modal">
    <div class="header">
        <fmt:message key="printer.${empty form.id ? 'adding' : 'editing'}"/>
    </div>
    <div class="scrolling content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label><fmt:message key="printer.field.name"/></label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label><fmt:message key="printer.field.ip"/></label>
                <form:input path="ip"/>
            </div>
            <div class="field">
                <label><fmt:message key="printer.field.port"/></label>
                <form:input path="port"/>
            </div>
            <div class="field">
                <label><fmt:message key="printer.field.description"/></label>
                <form:textarea path="description" rows="3"/>
            </div>
            <div class="field">
                <label><fmt:message key="printer.field.users"/></label>
                <form:select cssClass="ui dropdown std-select search" path="userIdList" multiple="true">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${userList}" var="user">
                        <form:option value="${user.id}">${user.userOfficialName}</form:option>
                    </c:forEach>
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