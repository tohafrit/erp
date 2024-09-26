<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление причины' : 'Редактирование причины'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Код</label>
                <form:input path="code" data-inputmask-alias="numeric" data-inputmask-rightAlign="false"/>
                <div class="ui compact message error" data-field="code"></div>
            </div>
            <div class="field">
                <label>Причина</label>
                <div class="ui textarea">
                    <form:textarea path="reason" rows="10"/>
                </div>
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