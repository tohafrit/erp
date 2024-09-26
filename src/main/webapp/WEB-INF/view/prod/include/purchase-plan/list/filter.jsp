<div class="ui modal">
    <div class="header">Фильтр закупочной ведомости</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Наименование</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="name"/>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата создания с</label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="createDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="createDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Создана</label>
                <select class="ui dropdown std-select search" name="createdBy">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}">${user.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Запуск</label>
                <select class="std-tree-select" name="launchId" multiple>
                    <std:treeChosen hierarchyList="${launchList}"/>
                </select>
            </div>
            <div class="field">
                <label>Версия ЗС</label>
                <select class="ui dropdown std-select search" name="versionType">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${versionTypeList}" var="type">
                        <option value="${type.id}">${type.property}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата отсечки с</label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="onTheWayLastDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="onTheWayLastDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Учет запасов</label>
                <select class="ui dropdown std-select search" name="reserveUseType">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${reserveUseTypeList}" var="reserveType">
                        <option value="${reserveType.id}">${reserveType.property}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Дата утверждения с</label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="approvalDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input std-div-input-search left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="approvalDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Утверждена</label>
                <select class="ui dropdown std-select search" name="approvedBy">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}">${user.value}</option>
                    </c:forEach>
                </select>
            </div>
        </form>
    </div>
</div>