<div class="ui modal">
    <div class="header">
        <fmt:message key="supplier.${empty supplier.id ? 'adding' : 'editing'}"/>
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="supplier" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label><fmt:message key="supplier.field.name"/></label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label><fmt:message key="supplier.field.inn"/></label>
                <form:input cssClass="list_edit__inn-field" path="inn"/>
                <div class="ui compact message error" data-field="inn"></div>
            </div>
            <div class="field">
                <label><fmt:message key="supplier.field.kpp"/></label>
                <form:input cssClass="list_edit__kpp-field" path="kpp"/>
                <div class="ui compact message error" data-field="kpp"></div>
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
        $('.list_edit__inn-field').inputmask({
            placeholder: '',
            mask: '*{1,13}',
            definitions: { '*': { validator: '[0-9]' } }
        });
        $('.list_edit__kpp-field').inputmask({
            placeholder: '',
            mask: '*{1,9}',
            definitions: { '*': { validator: '[0-9]' } }
        });
    });
</script>