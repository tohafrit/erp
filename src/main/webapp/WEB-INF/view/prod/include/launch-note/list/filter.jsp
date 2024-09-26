<div class="ui modal">
    <div class="header">Фильтр служебных записок</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Наименование изделия</label>
                <input type="search" name="productName"/>
            </div>
            <div class="field">
                <label>Согласована</label>
                <select class="ui dropdown std-select search" name="agreedBy">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}">${user.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Согласована с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="agreementDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="agreementDateTo"/>
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
                <div class="two fields">
                    <div class="field">
                        <label>Создана с</label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="createDateFrom"/>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label><fmt:message key="label.to"/></label>
                        <div class="ui calendar">
                            <div class="ui input left icon">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="createDateTo"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>