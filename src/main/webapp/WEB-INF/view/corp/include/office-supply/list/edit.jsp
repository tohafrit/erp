<div class="ui modal tiny">
    <div class="header">
        ${empty form.id ? 'Добавление канцелярского товара' : 'Редактирование канцелярского товара'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Артикул</label>
                <form:input path="article"/>
                <div class="ui compact message error" data-field="article"></div>
            </div>
            <div class="field">
                <label>Активность</label>
                <std:trueOrFalse name="active" value="${form.active}"/>
            </div>
            <div class="field">
                <label>Только для секретариата</label>
                <std:trueOrFalse name="onlySecretaries" value="${form.onlySecretaries}"/>
            </div>
            <div class="field required">
                <label>Наименование</label>
                <form:textarea path="name" rows="10"/>
                <div class="ui compact message error" data-field="name"></div>
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