<div class="ui modal large">
    <div class="ui small header">
        <fmt:message key="product.detail.comment.edit.header.${empty form.id ? 'add' : 'edit'}">
            <fmt:param>${productConditionalName}</fmt:param>
        </fmt:message>
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="product.id"/>
            <div class="field">
                <label><fmt:message key="product.detail.comment.filter.field.author"/></label>
                ${form.createdBy.userOfficialName}
                <form:hidden path="createdBy.id"/>
            </div>
            <div class="field">
                <label><fmt:message key="product.detail.comment.filter.field.createDate"/></label>
                <javatime:format value="${form.createdDate}" pattern="dd.MM.yyyy HH:mm:ss"/>
                <form:hidden path="createdDate"/>
            </div>
            <div class="field required">
                <label><fmt:message key="product.detail.comment.filter.field.comment"/></label>
                <div class="ui textarea">
                    <form:textarea path="comment" rows="10"/>
                    <div class="ui compact message error" data-field="comment"></div>
                </div>
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