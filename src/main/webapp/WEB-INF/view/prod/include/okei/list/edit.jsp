<div class="ui modal">
    <div class="header">
        <fmt:message key="okei.list.header.${empty okei.id ? 'add' : 'edit'}"/>
    </div>
    <div class="content">
        <form:form action="okei" method="POST" modelAttribute="okei" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label><fmt:message key="okei.field.code"/></label>
                <form:input placeholder="003" path="code" cssClass="list_edit__input-code"/>
                <div class="ui compact message error" data-field="code"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="okei.field.coefficient"/></label>
                <form:input path="coefficient"/>
                <div class="ui compact message error" data-field="coefficient"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="okei.field.name"/></label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="ui ribbon label"><fmt:message key="okei.field.symbol"/></div>
            <div class="field required">
                <label><fmt:message key="okei.field.national"/></label>
                <form:input path="symbolNational"/>
                <div class="ui compact message error" data-field="symbolNational"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="okei.field.international"/></label>
                <form:input path="symbolInternational"/>
                <div class="ui compact message error" data-field="symbolInternational"></div>
            </div>
            <div class="ui ribbon label"><fmt:message key="okei.field.codeLetter"/></div>
            <div class="field required">
                <label><fmt:message key="okei.field.national"/></label>
                <form:input path="codeLetterNational"/>
                <div class="ui compact message error" data-field="codeLetterNational"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="okei.field.international"/></label>
                <form:input path="codeLetterInternational"/>
                <div class="ui compact message error" data-field="codeLetterInternational"></div>
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
        $('input.list_edit__input-code').inputmask({
            placeholder: '000',
            regex: '[0-9]{0,3}'
        });
    });
</script>