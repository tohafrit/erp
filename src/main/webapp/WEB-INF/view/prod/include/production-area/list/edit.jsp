<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление участка' : 'Редактирование участка'}
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
                <label>Код</label>
                <form:input path="code"/>
                <div class="ui compact message error" data-field="code"></div>
            </div>
            <div class="field inline">
                <div class="ui checkbox">
                    <form:checkbox path="technological"/>
                    <label>Технологическая</label>
                </div>
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