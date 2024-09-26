<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление обозначения' : 'Редактирование обозначения'}
    </div>
    <div class="scrolling content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Обозначение</label>
                <form:input path="mark"/>
                <div class="ui compact message error" data-field="mark"></div>
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