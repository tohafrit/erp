<div class="ui modal">
    <div class="header">Фильтр запусков</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Утвержден</label>
                <select class="ui dropdown std-select search" name="approvedBy">
                    <option value=""><fmt:message key="text.notSpecified"/></option>
                    <c:forEach items="${userList}" var="user">
                        <option value="${user.id}">${user.value}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Утвержден с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="approvalDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="approvalDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Наименование изделия</label>
                <input type="search" name="productName"/>
            </div>
            <div class="field">
                <label>Краткая тех. хар-ка</label>
                <select class="ui dropdown std-select search" name="productTypeIdList" multiple>
                    <c:forEach items="${productTypeList}" var="type">
                        <option value="${type.id}">${type.value}</option>
                    </c:forEach>
                </select>
            </div>
        </form>
    </div>
</div>