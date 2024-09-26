<div class="ui modal">
    <div class="header">
        <fmt:message key="componentAppointment.${empty componentAppointment.id ? 'add' : 'edit'}"/>
    </div>
    <div class="scrolling content">
        <form:form method="POST" modelAttribute="componentAppointment" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label><fmt:message key="componentAppointment.table.field.name"/></label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label><fmt:message key="componentAppointment.table.field.comment"/></label>
                <form:textarea path="comment" rows="3"/>
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