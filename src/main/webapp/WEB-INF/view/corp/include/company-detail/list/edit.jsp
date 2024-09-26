<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление позиции' : 'Редактирование позиции'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Название позиции</label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field required">
                <label>Значение позиции</label>
                <form:textarea path="value" cssClass="std-ckeditor" rows="10"/>
                <div class="ui compact message error" data-field="value"></div>
            </div>
            <div class="field required">
                <label>Сортировка</label>
                <form:input path="sort"/>
                <div class="ui compact message error" data-field="sort"></div>
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