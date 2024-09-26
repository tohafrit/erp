<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление типа сообщения' : 'Редактирование типа сообщения'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Название</label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field required">
                <label>Уникальный код</label>
                <form:input path="code" placeholder="TEST_CODE"/>
                <div class="ui compact message error" data-field="code"></div>
            </div>
            <div class="field required">
                <label>Описание</label>
                <form:textarea path="description" rows="10"/>
                <div class="ui compact message error" data-field="description"></div>
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