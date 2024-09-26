<div class="ui modal">
    <div class="header">
        <fmt:message key="productionWarehouse.${empty productionWarehouse.id ? 'adding' : 'editing'}"/>
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="productionWarehouse" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label><fmt:message key="productionWarehouse.name"/></label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="productionWarehouse.code"/></label>
                <form:input cssClass="edit__code-field" path="code"/>
                <div class="ui compact message error" data-field="code"></div>
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
        $('input.edit__code-field').inputmask({
            placeholder: '',
            mask: '*{1,2}',
            definitions: { '*': { validator: '[0-9]' } }
        });
    });
</script>