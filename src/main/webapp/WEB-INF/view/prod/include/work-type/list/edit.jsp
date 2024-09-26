<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление типа операции' : 'Редактирование типа операции'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="version"/>
            <div class="field required">
                <label>Наименование</label>
                <form:input cssClass="list_edit__name-field" path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <div class="ui checkbox">
                    <form:checkbox cssClass="list_edit__separate-delivery-field" path="separateDelivery"/>
                    <label>Отдельная поставка</label>
                </div>
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
        if ('${canEdit}' === 'false') {
            $('input.list_edit__name-field').closest('div.field').addClass('disabled');
            $('input.list_edit__separate-delivery-field').closest('div.field').addClass('disabled');
        }
    });
</script>