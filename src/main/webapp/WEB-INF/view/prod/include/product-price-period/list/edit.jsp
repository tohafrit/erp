<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление периода' : 'Редактирование периода'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="version"/>
            <div class="field required">
                <label>Наименование</label>
                <form:input type="search" path="name"/>
                <div class="ui compact message small error" data-field="name"></div>
            </div>
            <div class="field required">
                <label>Дата начала</label>
                <div class="ui calendar <c:if test="${isDisableEdit}">disabled</c:if>">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input type="search" cssClass="std-date" path="startDate"/>
                    </div>
                </div>
                <div class="ui compact message small error" data-field="startDate"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <form:textarea path="comment" rows="3"/>
                </div>
                <div class="ui compact message small error" data-field="comment"></div>
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