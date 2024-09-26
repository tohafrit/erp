<div class="ui modal">
    <div class="header">
        <fmt:message key="bank.${empty bank.id ? 'add' : 'edit'}"/>
    </div>
    <div class="scrolling content">
        <form:form method="POST" modelAttribute="bank" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field">
                <label><fmt:message key="bank.name"/></label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field">
                <label><fmt:message key="bank.location"/></label>
                <form:input path="location"/>
                <div class="ui compact message error" data-field="location"></div>
            </div>
            <div class="field">
                <label><fmt:message key="bank.bik"/></label>
                <form:input path="bik"/>
                <div class="ui compact message error" data-field="bik"></div>
            </div>
            <div class="field">
                <label><fmt:message key="bank.account"/></label>
                <form:input path="correspondentAccount"/>
                <div class="ui compact message error" data-field="correspondentAccount"></div>
            </div>
            <div class="field">
                <label><fmt:message key="bank.address"/></label>
                <form:input path="address"/>
                <div class="ui compact message error" data-field="address"></div>
            </div>
            <div class="field">
                <label><fmt:message key="bank.phone"/></label>
                <form:input path="phone"/>
                <div class="ui compact message error" data-field="phone"></div>
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