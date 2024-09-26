<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление заявки' : 'Редактирование заявки'}
    </div>
    <div class="scrolling content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Номер комнаты</label>
                <form:input path="roomNumber"/>
                <div class="ui compact message error" data-field="roomNumber"></div>
            </div>
            <div class="field required">
                <label>Описание проблемы</label>
                <form:textarea path="reason" rows="3"/>
                <div class="ui compact message error" data-field="reason"></div>
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