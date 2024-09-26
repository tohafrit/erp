<div class="ui modal">
    <div class="ui small header">
        ${empty id ? 'Добавление закупочной ведомости' : 'Редактирование закупочной ведомости'}
    </div>
    <div class="content">
        <form class="ui form">
            <input type="hidden" name="id" value="${id}">
            <input type="hidden" name="version" value="${version}">
            <div class="field required">
                <label>Наименование</label>
                <input type="text" name="name" value="${name}"/>
                <div class="ui compact message small error" data-field="name"></div>
            </div>
            <div class="field required">
                <label>Дата создания</label>
                <div class="ui calendar">
                    <div class="ui input left icon std-div-input-search">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date" name="createDate" value="${createDate}">
                    </div>
                </div>
                <div class="ui compact message small error" data-field="createDate"></div>
            </div>
            <div class="field required">
                <label>Создана</label>
                <select name="createdById" class="ui dropdown search std-select">
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}" <c:if test="${user.id eq createdById}">selected</c:if>>${user.value}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="createdById"></div>
            </div>
            <div class="field required">
                <label>Запуск</label>
                <select class="std-tree-select" name="launchId">
                    <std:treeChosen hierarchyList="${launchList}" selectedItems="${launchId}"/>
                </select>
                <div class="ui compact message error" data-field="launchId"></div>
            </div>
            <div class="field">
                <label>Предыдущий запуск</label>
                <select class="std-tree-select" name="previousLaunchId">
                    <std:treeChosen hierarchyList="${launchList}" selectedItems="${previousLaunchId}"/>
                </select>
                <div class="ui compact message error" data-field="previousLaunchId"></div>
            </div>
            <div class="field required">
                <label>Версия ЗС</label>
                <select name="versionTypeId" class="ui dropdown search std-select">
                    <c:forEach items="${versionTypeList}" var="versionType">
                        <option value="${versionType.id}" <c:if test="${versionType.id eq versionTypeId}">selected</c:if>>${versionType.property}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="versionType"></div>
            </div>
            <div class="field required">
                <label>Дата отсечки</label>
                <div class="ui calendar">
                    <div class="ui input left icon std-div-input-search">
                        <i class="calendar icon"></i>
                        <input type="text" class="std-date" name="onTheWayLastDate" value="${onTheWayLastDate}">
                    </div>
                </div>
                <div class="ui compact message small error" data-field="onTheWayLastDate"></div>
            </div>
            <div class="field required">
                <label>Учет запасов</label>
                <select name="reserveUseTypeId" class="ui dropdown search std-select">
                    <c:forEach items="${reserveUseTypeList}" var="reserveUseType">
                        <option value="${reserveUseType.id}" <c:if test="${reserveUseType.id eq reserveUseTypeId}">selected</c:if>>${reserveUseType.property}</option>
                    </c:forEach>
                </select>
                <div class="ui compact message small error" data-field="versionType"></div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <div class="ui textarea">
                    <textarea name="comment" rows="3">${comment}</textarea>
                </div>
                <div class="ui compact message small error" data-field="comment"></div>
            </div>
        </form>
    </div>
    <div class="actions">
        <button class="ui small button" type="submit">
            <i class="icon blue save"></i>
            <fmt:message key="label.button.save"/>
        </button>
    </div>
</div>