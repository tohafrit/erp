<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление группы' : 'Редактирование группы'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="version"/>
            <div class="field required">
                <label>Номер</label>
                <form:input cssClass="list_edit__number-field" path="number"/>
                <div class="ui compact message error" data-field="number"></div>
            </div>
            <div class="field required">
                <label>Характеристика</label>
                <form:input path="characteristic"/>
                <div class="ui compact message error" data-field="characteristic"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <form:textarea path="comment" rows="3"/>
                </div>
                <div class="ui compact message error" data-field="comment"></div>
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
        $('input.list_edit__number-field').inputmask('numeric', {
            min: 1,
            max: 99,
            rightAlign: false
        });
    })
</script>