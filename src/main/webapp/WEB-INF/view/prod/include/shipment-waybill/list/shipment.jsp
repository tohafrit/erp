<div class="ui modal">
    <div class="ui small header">Отгрузка</div>
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
                <label>Дата отгрузки</label>
                <div class="ui calendar">
                    <div class="ui input left icon std-div-input-search">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date" name="shipmentDate" value="${shipmentDate}">
                    </div>
                </div>
                <div class="ui compact message small error" data-field="shipmentDate"></div>
            </div>
            <div class="field required">
                <label>Отпуск произвел</label>
                <select name="giveUserId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq giveUserId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="giveUserId"></div>
            </div>
            <div class="field required">
                <label>Отпуск разрешил</label>
                <select name="permitUserId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq permitUserId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="permitUserId"></div>
            </div>
            <div class="field required">
                <label>Главный бухгалтер</label>
                <select name="accountantUserId" class="ui dropdown search std-select">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq accountantUserId}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="accountantUserId"></div>
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