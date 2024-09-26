<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление кода' : 'Редактирование кода'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="version"/>
            <div class="field required">
                <label>
                    <c:if test="${form.type eq 'PRODUCT'}">Тип изделия</c:if>
                    <c:if test="${form.type eq 'COMPONENT'}">Группа компонента</c:if>
                </label>
                <form:select cssClass="ui dropdown search std-select" path="typeId">
                    <form:option value=""><fmt:message key="text.notSpecified"/></form:option>
                    <c:forEach items="${typeList}" var="type">
                        <form:option value="${type.id}">${type.value}</form:option>
                    </c:forEach>
                </form:select>
                <div class="ui compact message error" data-field="typeId"></div>
            </div>
            <div class="field required">
                <label>Код</label>
                <form:input path="code" cssClass="list_edit__input-code"/>
                <div class="ui compact message error" data-field="code"></div>
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
        //$('input.list_edit__input-code').inputmask({ alias: 'ip' });
    })
</script>