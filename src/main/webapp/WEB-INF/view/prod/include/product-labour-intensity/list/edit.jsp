<div class="ui modal list_edit__modal">
    <div class="header">
        ${empty form.id ? 'Добавление расчета трудоемкости' : 'Редактирование расчета трудоемкости'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="version"/>
            <div class="field required">
                <label>Наименование</label>
                <form:input type="search" path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <c:if test="${empty form.id}">
                <div class="field required">
                    <label>Дата добавления</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <form:input type="search" cssClass="std-date" path="createDate"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="createDate"></div>
                </div>
            </c:if>
            <c:if test="${not empty form.id}">
                <div class="field inline">
                    <label>Дата добавления</label>
                    <span class="ui text"><javatime:format value="${form.createDate}" pattern="dd.MM.yyyy"/></span>
                </div>
                <div class="field inline">
                    <label>Добавлен</label>
                    <span class="ui text">${form.createdBy}</span>
                </div>
                <div class="field inline">
                    <label>Утверждено</label>
                    <span class="ui text">${form.approved}</span>
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