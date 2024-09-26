<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление служебного символа' : 'Редактирование служебного символа'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Обозначение</label>
                <form:input path="name"/>
                <div class="ui compact message error" data-field="name"></div>
            </div>
            <div class="field required">
                <label>Кодовое представление обозначения</label>
                <form:input path="code"/>
                <div class="ui compact message error" data-field="code"></div>
            </div>
            <div class="field required">
                <label>Описание</label>
                <div class="ui textarea">
                    <form:textarea path="description" rows="3"/>
                </div>
                <div class="ui compact message error" data-field="description"></div>
            </div>
            <div class="field inline">
                <div class="ui checkbox">
                    <form:checkbox path="technologicalProcess"/>
                    <label>Техпроцесс</label>
                </div>
            </div>
            <div class="field inline">
                <div class="ui checkbox">
                    <form:checkbox path="operationCard"/>
                    <label>Операционная карта</label>
                </div>
            </div>
            <div class="field inline">
                <div class="ui checkbox">
                    <form:checkbox path="routeMap"/>
                    <label>Маршрутная карта</label>
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