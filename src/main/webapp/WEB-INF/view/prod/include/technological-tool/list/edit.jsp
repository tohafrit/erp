<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление оснастки' : 'Редактирование оснастки'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field">
                <label>Тип</label>
                <form:select cssClass="ui dropdown std-select" path="type">
                    <c:forEach items="${technologicalToolTypeList}" var="type">
                        <form:option value="${type}"><fmt:message key="${type.property}"/></form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field">
                <label>Обозначение</label>
                <form:input path="sign"/>
                <div class="ui compact message error" data-field="sign"></div>
            </div>
            <div class="field required">
                <label>Наименование</label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label>Участок</label>
                <form:select cssClass="ui dropdown std-select" path="productionAreaIdList" multiple="true">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${productionAreaList}" var="productionArea">
                        <form:option value="${productionArea.id}">${productionArea.formatCode} ${productionArea.name}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <div class="field">
                <label>Назначение</label>
                <form:input path="appointment"/>
                <div class="ui compact message error" data-field="appointment"></div>
            </div>
            <div class="field">
                <label>Ссылка на файл</label>
                <div class="ui action input">
                    <form:input path="link"/>
                    <button class="ui icon button list_edit__btn-choose">
                        <i class="search icon blue"></i>
                    </button>
                </div>
                <div class="ui compact message error" data-field="link"></div>
            </div>
            <div class="field">
                <label>Состояние</label>
                <form:input path="state"/>
                <div class="ui compact message error" data-field="state"></div>
            </div>
            <div class="field">
                <label>Дата выпуска</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input path="issueDate" cssClass="std-date" autocomplete="off"/>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Кем выпущен</label>
                <form:select cssClass="ui dropdown search std-select" path="user.id">
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

<script>
    $(() => {
        $('button.list_edit__btn-choose').on({
            'click' : e => {
                e.preventDefault();
                $.modalWindow({
                    loadURL: '/api/view/prod/technological-tool/list/edit/file'
                });
            }
        });
    });
</script>