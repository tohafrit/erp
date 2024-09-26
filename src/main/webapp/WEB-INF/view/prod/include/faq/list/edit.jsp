<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление вопроса' : 'Редактирование вопроса'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Вопрос</label>
                <form:input path="question"/>
                <div class="ui compact message error" data-field="question"></div>
            </div>
            <div class="field required">
                <label>Ответ</label>
                <form:input path="answer"/>
                <div class="ui compact message error" data-field="answer"></div>
            </div>
            <div class="field">
                <label>Сортировка</label>
                <form:input cssClass="list_edit__sort-input" path="sort"/>
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

<script>
    $(() => {
        const $sort = $('input.list_edit__sort-input');

        $sort.inputmask('integer', {
            min: 0,
            max: 65535,
            rightAlign: false,
            allowMinus: false
        });
    });
</script>