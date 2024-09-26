<div class="ui modal">
    <div class="header">
        ${empty form.id ? 'Добавление новости' : 'Редактирование новости'}
    </div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui form">
            <form:hidden path="id"/>
            <div class="field required">
                <label>Заголовок</label>
                <form:input path="title"/>
                <div class="ui compact message error" data-field="title"></div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Тип</label>
                    <form:select cssClass="ui dropdown std-select list_edit__type-selector" path="typeId">
                        <c:forEach items="${typeList}" var="type">
                            <form:option value="${type.id}">${type.name}</form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="field">
                    <label>Закрепленная</label>
                    <form:checkbox path="topStatus" />
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата активности с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <form:input cssClass="std-datetime" path="dateActiveFrom"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="dateActiveFrom"></div>
                </div>
                <div class="field">
                    <label>до</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <form:input cssClass="std-datetime" path="dateActiveTo"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="dateActiveTo"></div>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата события с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <form:input cssClass="std-datetime" path="dateEventFrom"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="dateEventFrom"></div>
                </div>
                <div class="field">
                    <label>до</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <form:input cssClass="std-datetime" path="dateEventTo"/>
                        </div>
                    </div>
                    <div class="ui compact message error" data-field="dateEventTo"></div>
                </div>
            </div>
            <div class="field required">
                <label>Анонс</label>
                <form:textarea path="previewText" cssClass="std-ckeditor"/>
                <div class="ui compact message error" data-field="previewText"></div>
            </div>
            <div class="field">
                <label>Содержимое</label>
                <form:textarea path="detailText" cssClass="std-ckeditor"/>
                <div class="ui compact message error" data-field="detailText"></div>
            </div>
            <div class="field required">
                <div class="erp-file-input ui action input">
                    <form:hidden path="fileStorage.id"/>
                    <input type="file" name="file"/>
                    <span>
                        ${form.fileStorage.name}
                        <a target="_blank" href="<c:url value="/download-file/${form.fileStorage.urlHash}"/>">
                            <fmt:message key="text.downloadFile"/>
                        </a>
                    </span>
                </div>
                <div class="ui compact message error" data-field="file"></div>
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