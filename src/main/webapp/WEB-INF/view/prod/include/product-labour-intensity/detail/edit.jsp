<div class="ui modal list_edit__modal">
    <div class="header">Редактирование трудоемкости</div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <form:hidden path="version"/>
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