<div class="ui modal">
    <div class="ui small header">Принятие по МСН</div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <div class="field inline">
                <label>Номер</label>
                <span class="ui text">${number}</span>
            </div>
            <div class="field inline">
                <label>Дата создания</label>
                <span class="ui text">${createDate}</span>
            </div>
            <div class="field required">
                <label>Дата принятия</label>
                <div class="ui calendar">
                    <div class="ui input left icon std-div-input-search">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date" name="acceptDate">
                    </div>
                </div>
                <div class="ui compact message small error" data-field="acceptDate"></div>
            </div>
            <div class="field required">
                <label>Отпустил</label>
                <select name="giveUserId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq giveUserId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="giveUserId"></div>
            </div>
            <div class="field required">
                <label>Получил</label>
                <select name="acceptUserId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq acceptUserId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="acceptUserId"></div>
            </div>
        </form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.apply"/>
        </button>
    </div>
</div>