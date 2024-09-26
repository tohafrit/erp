<div class="ui modal">
    <div class="header">
        <fmt:message key="producer.${empty producer.id ? 'adding' : 'editing'}"/>
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="producer" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label><fmt:message key="producer.result.name"/></label>
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