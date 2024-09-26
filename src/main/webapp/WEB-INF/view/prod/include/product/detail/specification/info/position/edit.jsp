<div class="ui modal">
    <div class="header">Редактирование позиционного обозначения</div>
    <div class="scrolling content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="lockVersion"/>
            <div class="field">
                <label>Прошивка</label>
                <form:input path="firmware"/>
                <div class="ui compact message error" data-field="firmware"></div>
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