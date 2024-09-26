<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление госконтракта' : 'Редактирование госконтракта'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field">
                <label>Идентификатор</label>
                <form:input path="identifier" cssClass="list_edit__field-identifier"/>
                <div class="ui compact message error" data-field="identifier"></div>
            </div>
            <div class="field">
                <label>Дата заключения</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="date"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="date"></div>
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
        const $inputIdentifier = $('input.list_edit__field-identifier');

        $inputIdentifier.inputmask({
            placeholder: '',
            regex: '[0-9]{0,25}'
        });
    })
</script>