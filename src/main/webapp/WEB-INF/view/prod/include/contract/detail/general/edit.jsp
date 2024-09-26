<div class="ui modal">
    <div class="header">${isSectionNumberZero ? "Добавление" : "Редактирование"} дополнительного соглашения</div>
    <div class="content">
        <form:form method="POST" modelAttribute="form" cssClass="ui small form">
            <form:hidden path="id"/>
            <form:hidden path="sectionNumber"/>
            <div class="field">
                <label>Номер</label>
                <c:choose>
                    <c:when test="${isSectionNumberZero}">
                        ${fullNumber} Доп.№ ${newSectionNumber}
                    </c:when>
                    <c:otherwise>
                        ${fullNumber}
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="field">
                <label>Дата создания</label>
                <div class="ui calendar">
                    <div class="ui input left icon">
                        <i class="calendar icon"></i>
                        <form:input cssClass="std-date" path="createDate"/>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Активное</label>
                <std:trueOrFalse name="archive" value="${form.archiveDate eq null}"/>
            </div>
            <div class="field">
                <label>Идентификатор</label>
                <form:input path="identifier"/>
                <div class="ui compact message error" data-field="identifier"></div>
            </div>
            <div class="field">
                <label>Внешний номер</label>
                <form:input  path="externalNumber"/>
            </div>
            <div class="field required">
                <label>Ведущий</label>
                <form:select cssClass="ui dropdown label search std-select" path="manager.id">
                    <c:forEach items="${userList}" var="user">
                        <form:option value="${user.id}">${user.value}</form:option>
                    </c:forEach>
                </form:select>
            </div>
            <c:if test="${form.sendToClientDate ne null}">
                <div class="field">
                    <label>Дата отправки заказчику</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <form:input cssClass="std-date" path="sendToClientDate"/>
                        </div>
                    </div>
                </div>
            </c:if>
            <div class="field">
                <label>Комментарий</label>
                <form:textarea path="comment" rows="3"/>
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