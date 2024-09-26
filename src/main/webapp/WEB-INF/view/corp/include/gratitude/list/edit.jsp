<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление благодарности' : 'Редактирование благодарности'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field">
                <label>Компания</label>
                <form:select cssClass="ui search dropdown label std-select" path="companyId" >
                    <c:forEach items="${companyList}" var="company">
                        <form:option value="${company.id}">${company.name}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="companyId"></div>
            </div>
            <div class="field required">
                <label>Дата</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="date"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="date"></div>
            </div>
            <div class="field required">
                <div class="erp-file-input ui action input">
                    <form:hidden path="fileStorage.id"/>
                    <input type="file" name="file"/>
                    <span>
                        ${form.fileStorage.name}
                        <a target="_blank" href="<c:url value="/download-file/${form.fileStorage.urlHash}"/>">
                            <fmt:message key="text.downloadFile"/>
                        </a>
                    </span>
                </div>
                <div class="ui compact message error" data-field="file"></div>
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