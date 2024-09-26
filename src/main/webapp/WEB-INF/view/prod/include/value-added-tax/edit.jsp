<div class="ui modal">
    <div class="header">
        <fmt:message key="valueAddedTax.${empty form.id ? 'add' : 'edit'}"/>
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label><fmt:message key="valueAddedTax.periodName"/></label>
                <form:input path="periodName"/>
                <div class="ui compact message error" data-field="periodName"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="valueAddedTax.dateFrom"/></label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="dateFrom" autocomplete="off"/>
                    </div>
                </div>
                <div class="ui compact message error" data-field="dateFrom"></div>
            </div>
            <div class="field required">
                <label><fmt:message key="valueAddedTax.amount"/></label>
                <form:input path="amount"/>
                <div class="ui compact message error" data-field="amount"></div>
            </div>
            <%--<div class="field">
                <div class="erp-file-input ui action input">
                    <form:hidden path="fileStorage.id"/>
                    <input type="file" name="file"/>
                    <span>
                        ${form.fileStorage.name}
                        <a target="_blank" href="<c:url value="/download-file/${form.fileStorage.storeHash}"/>">
                            <fmt:message key="text.downloadFile"/>
                        </a>
                    </span>
                </div>
                <div class="ui compact message error" data-field="fileStorage"></div>
            </div>--%>
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
        $('input[name^="amount"]').inputmask('9{0,2}.9{0,2}');
    });
</script>