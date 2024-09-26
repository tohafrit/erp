<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление служебной записки' : 'Редактирование служебной записки'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="version"/>
            <div class="field inline">
                <label>Номер в году</label>
                <span class="ui text">${form.numberInYear}</span>
            </div>
            <c:if test="${not empty form.agreedBy}">
                <div class="field inline">
                    <label>Согласована</label>
                    <span class="ui text">${form.agreedBy}</span>
                </div>
            </c:if>
            <c:if test="${not empty form.agreementDate}">
                <div class="field inline">
                    <label>Дата согласования</label>
                    <span class="ui text">${form.agreementDate}</span>
                </div>
            </c:if>
            <c:if test="${not empty form.createdBy}">
                <div class="field inline">
                    <label>Создана</label>
                    <span class="ui text">${form.createdBy}</span>
                </div>
            </c:if>
            <c:if test="${not empty form.createDate}">
                <div class="field inline">
                    <label>Дата создания</label>
                    <span class="ui text">${form.createDate}</span>
                </div>
            </c:if>
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