<div class="ui modal">
    <div class="header">
        <fmt:message key="company.list.edit.title.${empty form.id ? 'add' : 'edit'}"/>
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label><fmt:message key="company.field.type"/></label>
                <form:select cssClass="ui dropdown std-select" path="companyTypeList" multiple="multiple">
                    <c:forEach items="${companyTypeList}" var="companyType">
                        <form:option value="${companyType}"><fmt:message key="${companyType.property}"/></form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="companyTypeList"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="company.field.name"/></label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.fullName"/></label>
                <form:input path="fullName"/>
                <div class="ui compact message error" data-field="fullName"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.location"/></label>
                <form:input path="location"/>
                <div class="ui compact message error" data-field="location"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.addresses"/></label>
                <table class="ui tiny blue celled table">
                    <tbody>
                        <tr>
                            <td>Фактический адрес</td>
                            <td>
                                <div class="ui textarea">
                                    <form:textarea path="factualAddress" rows="4"/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>Юридический адрес</td>
                            <td>
                                <div class="ui textarea">
                                    <form:textarea path="juridicalAddress" rows="4"/>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>Почтовый адрес</td>
                            <td>
                                <div class="ui textarea">
                                    <form:textarea path="mailAddress" rows="4"/>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.chiefName"/></label>
                <form:input path="chiefName"/>
                <div class="ui compact message error" data-field="chiefName"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.chiefPosition"/></label>
                <form:input path="chiefPosition"/>
                <div class="ui compact message error" data-field="chiefPosition"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.phoneNumber"/></label>
                <form:input path="phoneNumber"/>
                <div class="ui compact message error" data-field="phoneNumber"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.contactPerson"/></label>
                <form:input path="contactPerson"/>
                <div class="ui compact message error" data-field="contactPerson"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.inn"/></label>
                <form:input path="inn"/>
                <div class="ui compact message error" data-field="inn"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.kpp"/></label>
                <form:input path="kpp"/>
                <div class="ui compact message error" data-field="kpp"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.ogrn"/></label>
                <form:input path="ogrn"/>
                <div class="ui compact message error" data-field="ogrn"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.inspectorName"/></label>
                <form:input path="inspectorName"/>
                <div class="ui compact message error" data-field="inspectorName"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.inspectorHead"/></label>
                <form:input path="inspectorHead"/>
                <div class="ui compact message error" data-field="inspectorHead"></div>
            </div>
            <div class="field">
                <label><fmt:message key="company.field.note"/></label>
                <div class="ui textarea">
                    <form:textarea path="note" rows="3"/>
                </div>
                <div class="ui compact message error" data-field="note"></div>
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