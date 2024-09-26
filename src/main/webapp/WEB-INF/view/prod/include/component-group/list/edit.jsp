<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление группы' : 'Редактирование группы'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Номер</label>
                <form:input path="number" cssClass="list_edit__number-field"/>
                <div class="ui compact message error" data-field="number"></div>
            </div>
            <div class="field required">
                <label>Наименование</label>
                <form:input path="name"/>
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

<script>
    $(() => {
        $('.list_edit__number-field').inputmask({
            placeholder: '',
            mask: '*{1,3}',
            definitions: { '*': { validator: '[0-9]' } }
        });
    });
</script>